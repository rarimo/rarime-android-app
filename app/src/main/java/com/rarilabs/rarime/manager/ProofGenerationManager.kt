@file:OptIn(ExperimentalStdlibApi::class)

package com.rarilabs.rarime.manager

import CircuitPassportHashType
import RegisterIdentityCircuitType
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.noirandroid.lib.Circuit
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.api.registration.PassportAlreadyRegisteredByOtherPK
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.modules.passportScan.CircuitDownloader
import com.rarilabs.rarime.modules.passportScan.CircuitNoirDownloader
import com.rarilabs.rarime.modules.passportScan.DownloadCircuitError
import com.rarilabs.rarime.modules.passportScan.DownloadRequest
import com.rarilabs.rarime.modules.passportScan.models.CryptoUtilsPassport
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.PlonkRegistrationInputs
import com.rarilabs.rarime.modules.passportScan.models.RegisterIdentityInputs
import com.rarilabs.rarime.modules.passportScan.models.RegisterIdentityLightInputs
import com.rarilabs.rarime.modules.passportScan.nfc.SODFileOwn
import com.rarilabs.rarime.util.Constants.NOT_ALLOWED_COUNTRIES
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.SecurityUtil
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.circuits.CircuitUtil
import com.rarilabs.rarime.util.circuits.RegisterNoirCircuitData
import com.rarilabs.rarime.util.circuits.RegisteredCircuitData
import com.rarilabs.rarime.util.data.GrothProof
import com.rarilabs.rarime.util.data.UniversalProof
import com.rarilabs.rarime.util.data.UniversalProofFactory

import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.generateLightRegistrationProofByCircuitType
import com.rarilabs.rarime.util.generateRegistrationProofByCircuitType
import com.rarilabs.rarime.util.toBits
import identity.X509Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.web3j.utils.Numeric
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

enum class PassportProofState(val value: Int) {
    READING_DATA(0), APPLYING_ZERO_KNOWLEDGE(1), CREATING_CONFIDENTIAL_PROFILE(2), FINALIZING(3)
}

@Singleton
class ProofGenerationManager @Inject constructor(
    private val application: Context,
    private val identityManager: IdentityManager,
    private val registrationManager: RegistrationManager,
    private val passportManager: PassportManager,
    private val rarimoContractManager: RarimoContractManager,
    private val pointsManager: PointsManager,
) {

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // State that can be observed by other components (e.g. view models).
    private val _state = MutableStateFlow(PassportProofState.READING_DATA)
    val state: StateFlow<PassportProofState> get() = _state.asStateFlow()
    private val _downloadProgress = MutableStateFlow(0)
    private var currentRegistration: kotlinx.coroutines.Deferred<UniversalProof>? = null

    //Download for circuits
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private var _proofError: MutableStateFlow<Exception?> = MutableStateFlow(null)

    val proofError: StateFlow<Exception?> get() = _proofError.asStateFlow()

    private val TAG = ProofGenerationManager::class.java.simpleName
    private val second = 1000L
    private val privateKeyBytes = identityManager.privateKeyBytes

    private fun resetState() {

        _state.value = PassportProofState.READING_DATA
        _proofError.value = null
    }


    suspend fun joinRewardProgram(eDocument: EDocument) {
        try {
            pointsManager.joinRewardProgram(eDocument)
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error joining reward program", e)
            throw e
        }
    }

    private suspend fun registerCertificate(eDocument: EDocument) {
        try {
            val sodStream = eDocument.sod!!.decodeHexString().inputStream()
            val sodFile = SODFileOwn(sodStream)
            val x509Util = X509Util()

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
            val callDataBuilder = identity.CallDataBuilder()
            val callData = callDataBuilder.buildRegisterCertificateCalldata(
                icao, slaveCertificate.toByteArray()
            )

            val response = withContext(Dispatchers.IO) {
                registrationManager.relayerRegister(
                    callData.calldata, BaseConfig.REGISTER_CONTRACT_ADDRESS
                )
            }
            ErrorHandler.logDebug(
                TAG, "Passport certificate EVM Tx Hash ${response.data.attributes.tx_hash}"
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

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun registerByDocument(eDocument: EDocument): UniversalProof {
        try {
            _state.value = PassportProofState.READING_DATA
            val circuitType = getCircuitType(eDocument)
            registrationManager.setCircuitData(circuitType)
            val circuitName = getCircuitName(circuitType)
            val circuitData = getCircuitData(circuitName)


            val proof = if (RegisterNoirCircuitData.fromValue(circuitType.buildName()) != null) {
                generateRegisterIdentityProofPlonk(
                    eDocument, registerIdentityCircuitType = circuitType
                )
            } else {
                generateRegisterIdentityProofGroth(eDocument, circuitData, circuitType)
            }


            if (!BuildConfig.isTestnet) {
                try {
                    ErrorHandler.logDebug(TAG, "Deleting redundant circuit files")
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Error deleting redundant circuit files", e)
                }
            }

            Log.i("Registration proof", GsonBuilder().setPrettyPrinting().create().toJson(proof))
            registrationManager.setRegistrationProof(proof)

            // Get passport info
            val passportInfo = try {
                registrationManager.getPassportInfo(eDocument, proof)
            } catch (e: Exception) {
                ErrorHandler.logError(TAG, "Error getting passport info", e)
                null
            }

            val ZERO_BYTES32 = ByteArray(32) { 0 }
            val currentIdentityKey = identityManager.getProfiler().publicKeyHash
            _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE

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
                    _state.value = PassportProofState.FINALIZING
                    delay(second)
                } else {
                    registrationManager.setRegistrationProof(proof)
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
                _state.value = PassportProofState.FINALIZING
                delay(second)
            }
            delay(second)
            return proof
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error in registerByDocument", e)
            throw e
        }
    }

    private suspend fun lightRegistration(eDocument: EDocument): UniversalProof {
        try {
            if (privateKeyBytes == null) throw IllegalStateException("privateKeyBytes is null")
            _state.value = PassportProofState.READING_DATA

            val registerIdentityCircuitName = eDocument.getRegisterIdentityLightCircuitName()
            ErrorHandler.logDebug(TAG, "registerIdentityCircuitName: $registerIdentityCircuitName")
            val registeredCircuitData = RegisteredCircuitData.fromValue(registerIdentityCircuitName)
                ?: throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")

            delay(second * 2)
            _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

            // Download circuit files
            val filePaths = withContext(Dispatchers.Default) {
                CircuitDownloader(application).downloadGrothFiles(registeredCircuitData) { progress, visibility ->
                    _downloadProgress.value = progress
                }
            } ?: throw DownloadCircuitError()

            delay(second * 2)
            val lightProof = withContext(Dispatchers.Default) {
                generateLightRegistrationProof(
                    filePaths, eDocument, privateKeyBytes, registeredCircuitData
                )
            }
            delay(second * 2)
            _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE

            val registerResponse = registrationManager.lightRegistration(eDocument, lightProof)
            val profile = identityManager.getProfiler()
            val currentIdentityKey = profile.publicKeyHash

            val proof =
                UniversalProofFactory.fromLight(registerResponse.data.attributes, lightProof)

            val passportInfoKey = withContext(Dispatchers.IO) {
                registrationManager.getPassportInfo(
                    eDocument, proof
                )!!.component1()
            }
            if (passportInfoKey.activeIdentity.contentEquals(currentIdentityKey)) {
                ErrorHandler.logDebug(TAG, "Passport is already registered with this PK")
                registrationManager.setRegistrationProof(proof)
                identityManager.setLightRegistrationData(registerResponse.data.attributes)
                return proof
            }
            delay(second * 2)
            _state.value = PassportProofState.FINALIZING
            val res = withContext(Dispatchers.IO) {
                registrationManager.lightRegisterRelayer(proof, registerResponse)
            }
            res
            registrationManager.setRegistrationProof(proof)
            identityManager.setLightRegistrationData(registerResponse.data.attributes)
            delay(second)
            return proof
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error in lightRegistration", e)
            throw e
        }
    }

    fun setAlreadyRegisteredByOtherPK() {
        _proofError.value = PassportAlreadyRegisteredByOtherPK()
    }

    suspend fun performRegistration(eDocument: EDocument): UniversalProof =
        withContext(managerScope.coroutineContext) {
            // If a registration is already in progress, return its result.
            currentRegistration?.let { ongoing ->
                if (ongoing.isActive) {
                    return@withContext ongoing.await()
                }
            }

            currentRegistration = managerScope.async {
                try {
                    resetState()
                    registerCertificate(eDocument)


                    val proof = registerByDocument(eDocument)
                    identityManager.setRegistrationProof(proof)

                    if (!NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.nationality)) {
                        passportManager.updatePassportStatus(PassportStatus.ALLOWED)
                    } else {
                        passportManager.updatePassportStatus(PassportStatus.NOT_ALLOWED)
                    }

                    proof
                } catch (e: Exception) {
                    when (e) {
                        is PassportAlreadyRegisteredByOtherPK -> {
                            ErrorHandler.logError(TAG, "Passport already registered", e)
                            _proofError.value = e
                            passportManager.updatePassportStatus(PassportStatus.ALREADY_REGISTERED_BY_OTHER_PK)
                            throw e
                        }

                        is DownloadCircuitError -> {
                            resetState()
                            ErrorHandler.logError(
                                TAG, "Error during default registration: ${e::class.simpleName}", e
                            )
                            _proofError.value = e
                            throw e
                        }

                        else -> {
                            ErrorHandler.logError(
                                TAG, "Default registration failed, trying light registration", e
                            )
                            try {
                                val lightProof = lightRegistration(eDocument)
                                identityManager.setRegistrationProof(lightProof)

                                if (!NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.nationality)) {
                                    passportManager.updatePassportStatus(PassportStatus.ALLOWED)
                                } else {
                                    passportManager.updatePassportStatus(PassportStatus.NOT_ALLOWED)
                                }

                                lightProof
                            } catch (e2: Exception) {
                                when (e2) {
                                    is PassportAlreadyRegisteredByOtherPK -> {
                                        ErrorHandler.logError(
                                            TAG,
                                            "Passport already registered during light registration",
                                            e2
                                        )
                                        passportManager.updatePassportStatus(PassportStatus.ALREADY_REGISTERED_BY_OTHER_PK)
                                        _proofError.value = e2
                                        throw e2
                                    }

                                    is DownloadCircuitError -> {
                                        resetState()
                                        ErrorHandler.logError(
                                            TAG,
                                            "Connection/Unpacking error during light registration",
                                            e2
                                        )
                                        _proofError.value = e2
                                        throw e2
                                    }

                                    else -> {
                                        if (!NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.nationality)) {
                                            passportManager.updatePassportStatus(PassportStatus.WAITLIST)
                                        } else {
                                            passportManager.updatePassportStatus(PassportStatus.WAITLIST_NOT_ALLOWED)
                                        }
                                        ErrorHandler.logError(TAG, "Light registration failed", e2)
                                        _proofError.value = e2
                                        throw e2
                                    }
                                }
                            }
                        }
                    }
                }
            }

            currentRegistration!!.await()
        }

    private suspend fun generateRegisterIdentityProofPlonk(
        eDocument: EDocument, registerIdentityCircuitType: RegisterIdentityCircuitType
    ): UniversalProof {
        val customDispatcher = Executors.newFixedThreadPool(1) { runnable ->
            Thread(null, runnable, "LargeStackThread", 100 * 1024 * 1024) // 100 MB stack size
        }.asCoroutineDispatcher()

        val circuitDownloader = CircuitNoirDownloader(application)


        val trustedSetupPath =
            circuitDownloader.downloadTrustedSetup(onProgressUpdate = { progress, isEnded ->
                _downloadProgress.value = progress
            })

        val circuitData = RegisterNoirCircuitData.fromValue(registerIdentityCircuitType.buildName())

        val byteCodePath =
            circuitDownloader.downloadNoirByteCode(circuitData = circuitData!!) { progress, isEnded ->
                _downloadProgress.value = progress
            }

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        val inputs = buildPlonkRegistrationInputs(eDocument, registerIdentityCircuitType)

        return withContext(customDispatcher) {
            val circuitByteCode = File(byteCodePath).readText()


            val circuit = Circuit.fromJsonManifest(circuitByteCode)

            circuit.setupSrs(trustedSetupPath, false)

            val proof = circuit.prove(inputs, proofType = "plonk", recursive = false)


            val zk = UniversalProofFactory.fromPlonkBytes(proof.proof.toByteArray())

            return@withContext zk
        }
    }


    private suspend fun generateRegisterIdentityProofGroth(
        eDocument: EDocument,
        registeredCircuitData: RegisteredCircuitData,
        registerIdentityCircuitType: RegisterIdentityCircuitType
    ): UniversalProof {

        val circuitDownloader = CircuitDownloader(application)

        val filePaths = withContext(Dispatchers.Default) {
            circuitDownloader.downloadGrothFiles(registeredCircuitData) { progress, visibility ->
                _downloadProgress.value = progress
            }
        } ?: throw DownloadCircuitError()

        ErrorHandler.logDebug(TAG, "Generating full registration proof")

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        val inputs = buildGrothRegistrationInputs(eDocument, registerIdentityCircuitType)
        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets
        val zkp = ZKPUseCase(application, assetManager)

        val proof = UniversalProofFactory.fromGroth(
            generateRegistrationProofByCircuitType(
                registeredCircuitData, filePaths, zkp, inputs
            )
        )
        return proof
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
                TAG, "Cannot get circuit name from registerIdentityCircuitType", e
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
    ): GrothProof {
        val inputs = Gson().toJson(getLightRegistrationInputs(eDocument, privateKey)).toByteArray()
        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets
        val zkp = ZKPUseCase(application, assetManager)
        return generateLightRegistrationProofByCircuitType(
            circuitData, filePaths, zkp, inputs
        )

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
            skIdentity = Numeric.toHexString(privateKey), dg1 = dg1Chunks
        )
    }

    private suspend fun buildPlonkRegistrationInputs(
        eDocument: EDocument, circuitType: RegisterIdentityCircuitType
    ): Map<String, Any> = withContext(Dispatchers.IO) {

        val dg1Bytes = Numeric.hexStringToByteArray(eDocument.dg1)
        val dg15Bytes = eDocument.dg15?.decodeHexString() ?: ByteArray(0)
        val sodFile = eDocument.getSodFile()
        val ecBytes = Numeric.hexStringToByteArray(sodFile.readASN1Data())
        val saBytes = sodFile.eContent


        val cert = sodFile.docSigningCertificate
        val certPem = SecurityUtil.convertToPEM(cert)

        val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS

        val x509Utils = X509Util()

        val proof = withContext(Dispatchers.IO) {
            val icao = readICAO(application.applicationContext)
            val slaveCertificateIndex =
                x509Utils.getSlaveCertificateIndex(certPem.toByteArray(), icao)
            val indexHex = slaveCertificateIndex.toHexString()
            val contract = rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)
            contract.getProof(indexHex.hexToByteArray()).send()
        }

        val chunkSize = circuitType.passportHashType.getChunkSize().toInt()

        val dg1Count = CircuitUtil.noirChunkNumber(dg1Bytes.size * 8, chunkSize)
        val ecCount = CircuitUtil.noirChunkNumber(ecBytes.size * 8, chunkSize)
        val saCount = CircuitUtil.noirChunkNumber(saBytes.size * 8, chunkSize)
        val dg15Count = if (dg15Bytes.isEmpty()) 0 else CircuitUtil.noirChunkNumber(
            dg15Bytes.size * 8, chunkSize
        )


        val dg1Chunks =
            CircuitUtil.bigIntToChunkingArray(chunkSize, dg1Count, BigInteger(1, dg1Bytes))
        val ecChunks = CircuitUtil.bigIntToChunkingArray(chunkSize, ecCount, BigInteger(1, ecBytes))
        val saChunks = CircuitUtil.bigIntToChunkingArray(chunkSize, saCount, BigInteger(1, saBytes))
        val dg15Chunks =
            if (dg15Bytes.isEmpty()) emptyList() else CircuitUtil.bigIntToChunkingArray(
                chunkSize, dg15Count, BigInteger(1, dg15Bytes)
            )

        val publicKey = sodFile.docSigningCertificate.publicKey
        val sigBytes = sodFile.encryptedDigest
        val pubKeyData = CryptoUtilsPassport.getDataFromPublicKey(publicKey)
            ?: throw IllegalArgumentException("Invalid public key data")
        val keyCount = CircuitUtil.noirChunkNumber(pubKeyData.size * 8, chunkSize)

        val pkChunks =
            CircuitUtil.bigIntToChunkingArray(chunkSize, keyCount, BigInteger(1, pubKeyData))
        val sigChunks =
            CircuitUtil.bigIntToChunkingArray(chunkSize, keyCount, BigInteger(1, sigBytes))

        val reduction =
            CircuitUtil.computeBarrettReduction(pubKeyData.size * 8, BigInteger(1, pubKeyData))

        val skIdentity = Numeric.toHexStringWithPrefix(BigInteger(privateKeyBytes))
        val icaoRoot = (BigInteger(proof.root)).toString()
        val inclusionBranches = proof.siblings.map { BigInteger(it).toString() }

        val inputs = PlonkRegistrationInputs(
            dg1 = dg1Chunks,
            dg15 = dg15Chunks,
            ec = ecChunks,
            sa = saChunks,
            pk = pkChunks,
            reduction = reduction,
            sig = sigChunks,
            sk_identity = skIdentity,
            icao_root = icaoRoot,
            inclusion_branches = inclusionBranches
        ).let { input ->
            input::class.members.filterIsInstance<kotlin.reflect.KProperty1<PlonkRegistrationInputs, *>>()
                .associate { it.name to it.get(input)!! }
        }

        inputs
    }


    private suspend fun buildGrothRegistrationInputs(
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
            slaveMerkleInclusionBranches = proof.siblings.map { BigInteger(it).toString() })
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