@file:OptIn(ExperimentalStdlibApi::class)

package com.rarilabs.rarime.manager

import CircuitPassportHashType
import RegisterIdentityCircuitType
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.api.registration.PassportAlreadyRegisteredByOtherPK
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.modules.passportScan.CircuitUseCase
import com.rarilabs.rarime.modules.passportScan.DownloadCircuitError
import com.rarilabs.rarime.modules.passportScan.DownloadRequest
import com.rarilabs.rarime.modules.passportScan.models.CryptoUtilsPassport
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.RegisterIdentityInputs
import com.rarilabs.rarime.modules.passportScan.models.RegisterIdentityLightInputs
import com.rarilabs.rarime.modules.passportScan.nfc.SODFileOwn
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.SecurityUtil
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.circuits.CircuitUtil
import com.rarilabs.rarime.util.circuits.RegisteredCircuitData
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.generateLightRegistrationProofByCircuitType
import com.rarilabs.rarime.util.generateRegistrationProofByCircuitType
import com.rarilabs.rarime.util.toBits
import identity.X509Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.web3j.utils.Numeric
import java.io.IOException
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

enum class PassportProofState(val value: Int) {
    READING_DATA(0),
    APPLYING_ZERO_KNOWLEDGE(1),
    CREATING_CONFIDENTIAL_PROFILE(2),
    FINALIZING(3)
}

@Singleton
class ProofGenerationManager @Inject constructor(
    private val application: Context,
    private val identityManager: IdentityManager,
    private val registrationManager: RegistrationManager,
    private val rarimoContractManager: RarimoContractManager,
    private val pointsManager: PointsManager,
) {
    // State that can be observed by other components (e.g. view models).
    private val _state =
        MutableStateFlow(PassportProofState.READING_DATA)
    val state: StateFlow<PassportProofState> get() = _state.asStateFlow()
    private val _downloadProgress = MutableStateFlow(0)

    //Download for circuits
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private val TAG = ProofGenerationManager::class.java.simpleName
    private val second = 1000L
    private val privateKeyBytes = identityManager.privateKeyBytes

    fun resetState() {
        _state.value =
            PassportProofState.READING_DATA
    }

    /**
     * Join the rewards program using the provided EDocument.
     */
    suspend fun joinRewardProgram(eDocument: EDocument) {
        try {
            pointsManager.joinRewardProgram(eDocument)
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error joining reward program", e)
            throw e
        }
    }

    /**
     * Registers a certificate using the data from the provided EDocument.
     */
    private suspend fun registerCertificate(eDocument: EDocument) {
        try {
            val sodStream = eDocument.sod!!.decodeHexString().inputStream()
            val sodFile = SODFileOwn(sodStream)
            val x509Util = X509Util() // Adjust import if needed

            val slaveCertificate = SecurityUtil.convertToPEM(sodFile.docSigningCertificate)
            val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS
            val certificatesSMTContract =
                rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)
            val icao = readICAO(application.applicationContext)
            val slaveCertificateIndex = x509Util.getSlaveCertificateIndex(
                slaveCertificate.toByteArray(), icao
            )

            val proof = withContext(Dispatchers.IO) {
                certificatesSMTContract.getProof(slaveCertificateIndex).send()
            }
            if (proof?.existence == true) {
                ErrorHandler.logDebug(TAG, "Passport certificate is already registered")
                return
            }
            val callDataBuilder = identity.CallDataBuilder() // Adjust import if needed
            val callData = callDataBuilder.buildRegisterCertificateCalldata(
                icao, slaveCertificate.toByteArray()
            )
            val response = withContext(Dispatchers.IO) {
                registrationManager.relayerRegister(
                    callData.calldata,
                    BaseConfig.REGISTER_CONTRACT_ADDRESS
                )
            }
            ErrorHandler.logDebug(
                TAG,
                "Passport certificate EVM Tx Hash ${response.data.attributes.tx_hash}"
            )

            val res =
                rarimoContractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)
            if (!res) {
                ErrorHandler.logError(TAG, "Transaction failed ${response.data.attributes.tx_hash}")
            }
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error in registerCertificate", e)
            throw e
        }
    }

    /**
     * Registers by document. Generates proof and registers using full registration logic.
     */
    suspend fun registerByDocument(eDocument: EDocument): ZkProof {
        try {
            _state.value =
                PassportProofState.READING_DATA
            val circuitType = getCircuitType(eDocument)
            registrationManager.setCircuitData(circuitType)
            val circuitName = getCircuitName(circuitType)
            val circuitData = getCircuitData(circuitName)

            // Download circuit files with CircuitUseCase.
            val circuitUseCase = CircuitUseCase(application)
            val filePaths = withContext(Dispatchers.Default) {
                circuitUseCase.download(circuitData) { progress, visibility ->
                    _downloadProgress.value = progress
                }
            } ?: throw DownloadCircuitError()

            // Proof generation
            val proof =
                generateRegisterIdentityProof(eDocument, circuitData, filePaths, circuitType)

            if (!BuildConfig.isTestnet) {
                try {
                    ErrorHandler.logDebug(TAG, "Deleting redundant circuit files")
                    circuitUseCase.deleteRedunantFiles(circuitData)
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Error deleting redundant circuit files", e)
                }
            }

            Log.i("Registration proof", GsonBuilder().setPrettyPrinting().create().toJson(proof))
            registrationManager.setRegistrationProof(proof)
            _state.value =
                PassportProofState.APPLYING_ZERO_KNOWLEDGE

            // Get passport info
            val passportInfo = try {
                registrationManager.getPassportInfo(eDocument, proof)
            } catch (e: Exception) {
                ErrorHandler.logError(TAG, "Error getting passport info", e)
                null
            }

            val ZERO_BYTES32 = ByteArray(32) { 0 }
            val currentIdentityKey = identityManager.getProfiler().publicKeyHash
            _state.value =
                PassportProofState.CREATING_CONFIDENTIAL_PROFILE

            passportInfo?.let {
                if (it.component1()?.activeIdentity?.toHexString() == currentIdentityKey.toHexString()) {
                    ErrorHandler.logDebug(TAG, "Passport is already registered with this PK")
                } else if (passportInfo.component1().activeIdentity.contentEquals(ZERO_BYTES32)) {
                    registrationManager.register(
                        proof,
                        eDocument,
                        registrationManager.masterCertProof.value!!,
                        false,
                        circuitName
                    )
                    delay(second)
                    _state.value =
                        PassportProofState.FINALIZING
                    delay(second)
                } else {
                    throw PassportAlreadyRegisteredByOtherPK()
                }
            } ?: run {
                registrationManager.register(
                    proof,
                    eDocument,
                    registrationManager.masterCertProof.value!!,
                    false,
                    circuitName
                )
                delay(second)
                _state.value =
                    PassportProofState.FINALIZING
                delay(second)
            }
            delay(second)
            return proof
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error in registerByDocument", e)
            throw e
        }
    }

    /**
     * Performs light registration.
     */
    suspend fun lightRegistration(eDocument: EDocument): ZkProof {
        try {
            if (privateKeyBytes == null)
                throw IllegalStateException("privateKeyBytes is null")
            _state.value =
                PassportProofState.READING_DATA

            val registerIdentityCircuitName = eDocument.getRegisterIdentityLightCircuitName()
            ErrorHandler.logDebug(TAG, "registerIdentityCircuitName: $registerIdentityCircuitName")
            val registeredCircuitData = RegisteredCircuitData.fromValue(registerIdentityCircuitName)
                ?: throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")

            delay(second * 2)
            _state.value =
                PassportProofState.APPLYING_ZERO_KNOWLEDGE

            // Download circuit files
            val filePaths = withContext(Dispatchers.Default) {
                CircuitUseCase(application).download(registeredCircuitData) { progress, visibility ->
                    _downloadProgress.value = progress
                }
            } ?: throw DownloadCircuitError()

            delay(second * 2)
            val lightProof = withContext(Dispatchers.Default) {
                generateLightRegistrationProof(
                    filePaths,
                    eDocument,
                    privateKeyBytes,
                    registeredCircuitData
                )
            }
            delay(second * 2)
            _state.value =
                PassportProofState.CREATING_CONFIDENTIAL_PROFILE

            val registerResponse = registrationManager.lightRegistration(eDocument, lightProof)
            val profile = identityManager.getProfiler()
            val currentIdentityKey = profile.publicKeyHash

            val passportInfoKey = withContext(Dispatchers.IO) {
                registrationManager.getPassportInfo(
                    eDocument,
                    lightProof,
                    registerResponse.data.attributes
                )!!.component1()
            }
            if (passportInfoKey.activeIdentity.contentEquals(currentIdentityKey)) {
                ErrorHandler.logDebug(TAG, "Passport is already registered with this PK")
                registrationManager.setRegistrationProof(lightProof)
                identityManager.setLightRegistrationData(registerResponse.data.attributes)
                return lightProof
            }
            delay(second * 2)
            _state.value =
                PassportProofState.FINALIZING
            val res = withContext(Dispatchers.IO) {
                registrationManager.lightRegisterRelayer(lightProof, registerResponse)
            }
            res
            registrationManager.setRegistrationProof(lightProof)
            identityManager.setLightRegistrationData(registerResponse.data.attributes)
            delay(second)
            return lightProof
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error in lightRegistration", e)
            throw e
        }
    }

    /**
     * Use-case function that performs default registration.
     * If any error occurs during the full registration process,
     * it falls back to light registration.
     */
    suspend fun performRegistration(eDocument: EDocument): ZkProof {
        try {
            // First, attempt default registration.
            registerCertificate(eDocument)
            val proof = registerByDocument(eDocument)
            return proof
        } catch (e: PassportAlreadyRegisteredByOtherPK) {
            ErrorHandler.logError(TAG, "Passport already registered", e)
            throw e
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Default registration failed, trying light registration", e)
            return lightRegistration(eDocument)
        }
    }

    // ----- PRIVATE HELPER FUNCTIONS -----

    private suspend fun generateRegisterIdentityProof(
        eDocument: EDocument,
        registeredCircuitData: RegisteredCircuitData,
        filePaths: DownloadRequest?,
        registerIdentityCircuitType: RegisterIdentityCircuitType
    ): ZkProof {
        ErrorHandler.logDebug(TAG, "Generating full registration proof")
        val inputs = buildRegistrationCircuits(eDocument, registerIdentityCircuitType)
        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets
        val zkp = ZKPUseCase(application, assetManager)
        return generateRegistrationProofByCircuitType(registeredCircuitData, filePaths, zkp, inputs)
    }

    private fun getCircuitType(eDocument: EDocument): RegisterIdentityCircuitType {
        return try {
            eDocument.getRegisterIdentityCircuitType()
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Cannot get RegisterIdentityCircuitType", e)
            throw e
        }
    }

    private fun getCircuitData(registerIdentityCircuitName: String): RegisteredCircuitData {
        ErrorHandler.logDebug(TAG, "registerIdentityCircuitName: $registerIdentityCircuitName")
        return RegisteredCircuitData.fromValue(registerIdentityCircuitName)
            ?: throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")
    }

    private fun getCircuitName(registerIdentityCircuitType: RegisterIdentityCircuitType): String {
        return try {
            registerIdentityCircuitType.buildName()
        } catch (e: Exception) {
            ErrorHandler.logError(
                TAG,
                "Cannot get circuit name from registerIdentityCircuitType",
                e
            )
            ErrorHandler.logError(TAG, Gson().toJson(registerIdentityCircuitType))
            throw e
        }
    }

    private fun generateLightRegistrationProof(
        filePaths: DownloadRequest,
        eDocument: EDocument,
        privateKey: ByteArray,
        circuitData: RegisteredCircuitData
    ): ZkProof {
        val inputs = Gson().toJson(getLightRegistrationInputs(eDocument, privateKey)).toByteArray()
        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets
        val zkp = ZKPUseCase(application, assetManager)
        return generateLightRegistrationProofByCircuitType(circuitData, filePaths, zkp, inputs)
    }

    private fun getLightRegistrationInputs(
        eDocument: EDocument, privateKey: ByteArray
    ): RegisterIdentityLightInputs {
        val digestAlgorithm = eDocument.getSodFile().digestAlgorithm
        val passportHashType = CircuitPassportHashType.fromValue(digestAlgorithm)
            ?: throw IllegalArgumentException("Invalid digest algorithm")
        val smartChunkingToBlockSize = passportHashType.getChunkSize()
        val dg1Chunks = CircuitUtil.smartChunking2(
            Numeric.hexStringToByteArray(eDocument.dg1), 1, smartChunkingToBlockSize.toLong()
        )
        return RegisterIdentityLightInputs(
            skIdentity = Numeric.toHexString(privateKey),
            dg1 = dg1Chunks
        )
    }

    private suspend fun buildRegistrationCircuits(
        eDocument: EDocument, circuitType: RegisterIdentityCircuitType
    ): ByteArray {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val sodFile = eDocument.getSodFile()
        val cert = sodFile.docSigningCertificate
        val certPem = SecurityUtil.convertToPEM(cert)
        val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS
        val x509Utils = X509Util() // Adjust import if needed

        val proof = withContext(Dispatchers.IO) {
            val icao = readICAO(application.applicationContext)
            val slaveCertificateIndex =
                x509Utils.getSlaveCertificateIndex(certPem.toByteArray(), icao)
            val indexHex = slaveCertificateIndex.toHexString()
            val contract = rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)
            contract.getProof(indexHex.hexToByteArray()).send()
        }

        val encapsulatedContent = Numeric.hexStringToByteArray(sodFile.readASN1Data())
        val signedAttributes = sodFile.eContent
        val publicKey = sodFile.getDocSigningCertificate().publicKey
        val signature = sodFile.encryptedDigest

        val pubKeyData = CryptoUtilsPassport.getDataFromPublicKey(publicKey)
            ?: throw IllegalArgumentException("Invalid public key data")
        val smartChunkingNumber = CircuitUtil.calculateSmartChunkingNumber(pubKeyData.size * 8)
        val smartChunkingToBlockSize = circuitType.passportHashType.getChunkSize()

        val dg15: List<Long> = if (eDocument.dg15.isNullOrEmpty()) {
            listOf()
        } else {
            CircuitUtil.smartChunking2(
                eDocument.dg15!!.decodeHexString(),
                circuitType.aaType!!.dg15ChunkNumber.toLong(),
                smartChunkingToBlockSize.toLong()
            )
        }

        val encapsulatedChunks = CircuitUtil.smartChunking2(
            encapsulatedContent,
            circuitType.ecChunkNumber.toLong(),
            smartChunkingToBlockSize.toLong()
        )

        val signedAttrChunks = CircuitUtil.smartChunking2(
            signedAttributes, 2, smartChunkingToBlockSize.toLong()
        )

        ErrorHandler.logDebug(TAG, "Signed attributes chunks: ${Gson().toJson(signedAttrChunks)}")

        val pubKeyChunks = when (publicKey) {
            is ECPublicKey -> pubKeyData.toBits().map { it.toString() }
            else -> CircuitUtil.smartChunking(BigInteger(1, pubKeyData), smartChunkingNumber)
                .map { it.toString() }
        }

        val signatureChunks = when (publicKey) {
            is ECPublicKey -> {
                CircuitUtil.parseECDSASignature(signature)?.toBits()?.map { it.toString() }
                    ?: throw Exception("Invalid ECDSA signature")
            }

            else -> CircuitUtil.smartChunking(BigInteger(1, signature), smartChunkingNumber)
                .map { it.toString() }
        }

        val dg1Chunks = CircuitUtil.smartChunking2(
            eDocument.dg1!!.decodeHexString(), 2, smartChunkingToBlockSize.toLong()
        )

        val inputs = RegisterIdentityInputs(
            skIdentity = Numeric.toHexStringWithPrefix(BigInteger(privateKeyBytes)),
            encapsulatedContent = encapsulatedChunks,
            signedAttributes = signedAttrChunks,
            pubkey = pubKeyChunks,
            signature = signatureChunks,
            dg1 = dg1Chunks,
            dg15 = dg15,
            slaveMerkleRoot = (BigInteger(proof.root)).toString(),
            slaveMerkleInclusionBranches = proof.siblings.map { BigInteger(it).toString() }
        )
        registrationManager.setMasterCertProof(proof)
        return gson.toJson(inputs).toByteArray()
    }

    private fun readICAO(context: Context): ByteArray? {
        return try {
            val assetContext: Context = context.createPackageContext("com.rarilabs.rarime", 0)
            assetContext.assets.open("masters_asset.pem").use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: IOException) {
            ErrorHandler.logError(TAG, "Error reading ICAO", e)
            null
        }
    }
}