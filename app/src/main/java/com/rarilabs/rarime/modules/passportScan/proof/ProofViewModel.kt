package com.rarilabs.rarime.modules.passportScan.proof

import CircuitPassportHashType
import RegisterIdentityCircuitType
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.api.registration.PassportAlreadyRegisteredByOtherPK
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.manager.RegistrationManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.modules.passportScan.CircuitUseCase
import com.rarilabs.rarime.modules.passportScan.ConnectionError
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
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.CallDataBuilder
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


@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class ProofViewModel @Inject constructor(
    private val application: Application,
    private val identityManager: IdentityManager,
    private val registrationManager: RegistrationManager,
    private val rarimoContractManager: RarimoContractManager,
    private val pointsManager: PointsManager,
    private val walletManager: WalletManager
) : AndroidViewModel(application) {
    private val privateKeyBytes = identityManager.privateKeyBytes

    private var _state: MutableStateFlow<PassportProofState> =
        MutableStateFlow(PassportProofState.READING_DATA)
    val state: StateFlow<PassportProofState?>
        get() = _state.asStateFlow()

    private val second = 1000L

    fun resetState() {
        _progressVisibility.value = false
        _state.value = PassportProofState.READING_DATA
    }

    private val TAG = ProofViewModel::class.java.simpleName

    private val eDoc = registrationManager.eDocument
    val regProof = registrationManager.registrationProof
    val pointsToken = walletManager.pointsToken

    suspend fun joinRewardProgram(eDocument: EDocument) {
        val res = pointsManager.joinRewardProgram(eDocument)
    }

    private var _progress = MutableStateFlow(0)
    private var _progressVisibility = MutableStateFlow(false)
    val progress: StateFlow<Int>
        get() = _progress.asStateFlow()

    val progressVisibility: StateFlow<Boolean>
        get() = _progressVisibility.asStateFlow()

    suspend fun registerCertificate(eDocument: EDocument) {
        val sodStream = eDocument.sod!!.decodeHexString().inputStream()
        val sodFile = SODFileOwn(sodStream)
        val x509Util = X509Util()

        val slaveCertificate = SecurityUtil.convertToPEM(sodFile.docSigningCertificate)

        val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS

        val certificatesSMTContract = rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)

        val icao = readICAO(context = application.applicationContext)
        val slaveCertificateIndex =
            x509Util.getSlaveCertificateIndex(slaveCertificate.toByteArray(), icao)

        val proof = withContext(Dispatchers.IO) {
            certificatesSMTContract.getProof(slaveCertificateIndex).send()
        }

        if (proof?.existence == true) {
            ErrorHandler.logDebug("ProofViewModel", "Passport certificate is already registered")
            return
        }
        val callDataBuilder = CallDataBuilder()
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
            TAG, "Passport certificate EVM Tx Hash " + response.data.attributes.tx_hash
        )

        val res =
            rarimoContractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)
        if (!res) {
            ErrorHandler.logError(TAG, "Transaction failed" + response.data.attributes.tx_hash)
        }
    }

    private suspend fun generateRegisterIdentityProof(
        eDocument: EDocument,
        registeredCircuitData: RegisteredCircuitData,
        filePaths: DownloadRequest?,
        registerIdentityCircuitType: RegisterIdentityCircuitType
    ): ZkProof {
        ErrorHandler.logDebug("ProofViewModel", "Generating proof")

        val inputs = buildRegistrationCircuits(eDocument, registerIdentityCircuitType)

        val assetContext: Context =
            (application as Context).createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(application as Context, assetManager)

        val proof = generateRegistrationProofByCircuitType(
            registeredCircuitData, filePaths, zkp, inputs
        )



        return proof
    }

    private fun getCircuitType(eDocument: EDocument): RegisterIdentityCircuitType {
        val registerIdentityCircuitType = try {
            eDocument.getRegisterIdentityCircuitType()
        } catch (e: Exception) {
            ErrorHandler.logError("registerByDocument", "Cant getRegisterIdentityCircuitType", e)
            throw e
        }
        return registerIdentityCircuitType

    }

    private fun getCircuitData(registerIdentityCircuitName: String): RegisteredCircuitData {
        ErrorHandler.logDebug("registerIdentityCircuitName", registerIdentityCircuitName)
        val registeredCircuitData = RegisteredCircuitData.fromValue(registerIdentityCircuitName)
            ?: throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")

        return registeredCircuitData
    }

    private fun getCircuitName(registerIdentityCircuitType: RegisterIdentityCircuitType): String {
        val registerIdentityCircuitName = try {
            registerIdentityCircuitType.buildName()
        } catch (e: Exception) {
            ErrorHandler.logError(
                "registerIdentityCircuitType.buildName()", "cant get register identity name", e
            )
            ErrorHandler.logError(
                "registerIdentityCircuitType", Gson().toJson(registerIdentityCircuitType)
            )
            throw e
        }
        return registerIdentityCircuitName
    }

    suspend fun registerByDocument() {
        val eDocument = eDoc.value!!

        //Get circuit type
        val circuitType = getCircuitType(eDocument)

        registrationManager.setCircuitData(circuitType)

        val circuitName = getCircuitName(circuitType)

        val circuitData = getCircuitData(circuitName)

        val circuitUseCase = CircuitUseCase(application as Context)

        val filePaths = try {
            withContext(Dispatchers.Default) {
                circuitUseCase.download(circuitData) { progress, visibility ->
                    _progress.value = progress
                    _progressVisibility.value = !visibility
                }
            } ?: throw DownloadCircuitError()
        } catch (e: ConnectionError) {
            ErrorHandler.logError("CircuitUseCase", "Network issue encountered", e)
            throw e
        } catch (e: DownloadCircuitError) {
            ErrorHandler.logError("CircuitUseCase", "Circuit download failed", e)
            throw e
        } catch (e: Exception) {
            ErrorHandler.logError("CircuitUseCase", "Unexpected error occurred", e)
            circuitUseCase.deleteRedunantFiles(circuitData)
            throw DownloadCircuitError().apply { initCause(e) }
        }
        _state.value = PassportProofState.READING_DATA


        //Proof generation
        val proof =
            generateRegisterIdentityProof(
                eDocument, circuitData, filePaths, circuitType
            )

        if (!BuildConfig.isTestnet) {
            try {
                ErrorHandler.logDebug("deleting zkey, dat and Archive", "Start")
                circuitUseCase.deleteRedunantFiles(circuitData)
                ErrorHandler.logDebug("deleting zkey, dat and Archive", "Finish")
            } catch (e: Exception) {
                ErrorHandler.logError("Error deleting zkey, dat and Archive", "Error", e)
            }
        }

        Log.i("Registration proof", GsonBuilder().setPrettyPrinting().create().toJson(proof))

        registrationManager.setRegistrationProof(proof)

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE
        _progressVisibility.value = false

        // Get passport info
        val passportInfo = try {
            registrationManager.getPassportInfo(eDocument, proof)
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error: $e", e)
            null
        }

        val ZERO_BYTES32 = ByteArray(32) { 0 }

        val currentIdentityKey = identityManager.getProfiler().publicKeyHash
        _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE

        //registration
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

                delay(second * 1)


                _state.value = PassportProofState.FINALIZING

                delay(second * 1)
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
            delay(second * 1)


            _state.value = PassportProofState.FINALIZING

            delay(second * 1)
        }

        delay(second * 1)
    }

    suspend fun lightRegistration(): ZkProof {
        val privateKeyBytes = privateKeyBytes!!
        val eDocument = eDoc.value!!

        val registerIdentityCircuitName = eDocument.getRegisterIdentityLightCircuitName()

        ErrorHandler.logDebug("registerIdentityCircuitName", registerIdentityCircuitName)
        val registeredCircuitData = RegisteredCircuitData.fromValue(registerIdentityCircuitName)
            ?: throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")

        delay(second * 2)
        _state.value = PassportProofState.READING_DATA


        //TODO: Don't forget to update download manager here
        val filePaths = withContext(Dispatchers.Default) {
            CircuitUseCase(application as Context).download(registeredCircuitData) { progress, visibility ->
                if (_state.value.value < PassportProofState.APPLYING_ZERO_KNOWLEDGE.value) {
                    _progress.value = progress
                    _progressVisibility.value = !visibility
                }
            }
        }

        delay(second * 2)
        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        val lightProof = withContext(Dispatchers.Default) {
            generateLightRegistrationProof(
                filePaths!!,
                eDocument,
                privateKeyBytes,
                circuitData = registeredCircuitData
            )
        }

        delay(second * 2)
        _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE


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
        _state.value = PassportProofState.FINALIZING

        val res = withContext(Dispatchers.IO) {
            registrationManager.lightRegisterRelayer(lightProof, registerResponse)
        }

        res

        registrationManager.setRegistrationProof(lightProof)
        identityManager.setLightRegistrationData(registerResponse.data.attributes)
        delay(second * 1)
        return lightProof
    }

    private fun generateLightRegistrationProof(
        filePaths: DownloadRequest,
        eDocument: EDocument,
        privateKey: ByteArray,
        circuitData: RegisteredCircuitData
    ): ZkProof {

        val inputs = Gson().toJson(getLightRegistrationInputs(eDocument, privateKey)).toByteArray()
        val assetContext: Context =
            (application as Context).createPackageContext("com.rarilabs.rarime", 0)

        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(application as Context, assetManager)

        val zkProof =
            generateLightRegistrationProofByCircuitType(circuitData, filePaths, zkp, inputs)

        return zkProof
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

    private suspend fun buildRegistrationCircuits(
        eDocument: EDocument, circuitType: RegisterIdentityCircuitType
    ): ByteArray {
        val gson = GsonBuilder().setPrettyPrinting().create()

        val sodFile = eDocument.getSodFile()

        val cert = sodFile.docSigningCertificate
        val certPem = SecurityUtil.convertToPEM(cert)

        val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS

        val x509Utils = X509Util()

        val proof = withContext(Dispatchers.IO) {
            val icao = readICAO(context = application.applicationContext)

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
            ?: throw IllegalArgumentException("invalid pubkey data")

        val smartChunkingNumber = CircuitUtil.calculateSmartChunkingNumber(pubKeyData.size * 8)
        val smartChunkingToBlockSize = (circuitType.passportHashType.getChunkSize())

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

        Log.i("signedAttrChunks gson", Gson().toJson(signedAttrChunks))
        Log.i("signedAttrChunks", signedAttrChunks.toString())

        val pubKeyChunks = when (publicKey) {
            is ECPublicKey -> {
                pubKeyData.toBits().map { it.toString() }
            }

            else -> {
                CircuitUtil.smartChunking(
                    BigInteger(1, pubKeyData), smartChunkingNumber
                ).map { it.toString() }
            }
        }

        val signatureChunks = when (publicKey) {
            is ECPublicKey -> {
                CircuitUtil.parseECDSASignature(signature)?.toBits()?.map { it.toString() }
                    ?: throw Exception("Invalid ECDSA signature")
            }

            else -> {
                CircuitUtil.smartChunking(
                    BigInteger(1, signature), smartChunkingNumber
                ).map { it.toString() }
            }
        }

        val dg1Chunks = CircuitUtil.smartChunking2(
            eDocument.dg1!!.decodeHexString(), 2, smartChunkingToBlockSize.toLong()
        )

        val inputs = RegisterIdentityInputs(
            skIdentity = Numeric.toHexStringWithPrefix(
                BigInteger(privateKeyBytes)
            ),
            encapsulatedContent = encapsulatedChunks,
            signedAttributes = signedAttrChunks,
            pubkey = pubKeyChunks,
            signature = signatureChunks,
            dg1 = dg1Chunks,
            dg15 = dg15,
            slaveMerkleRoot = (BigInteger(proof.root)).toString(),
            slaveMerkleInclusionBranches = proof.siblings.map { (BigInteger(it).toString()) })

        registrationManager.setMasterCertProof(proof)

        return gson.toJson(inputs).toByteArray()
    }

    private fun readICAO(context: Context): ByteArray? {
        return try {
            val assetContext: Context = context.createPackageContext("com.rarilabs.rarime", 0)
            val assetManager = assetContext.assets
            assetManager.open("masters_asset.pem").use { inputStream ->
                val res = inputStream.readBytes()
                res
            }
        } catch (e: IOException) {
            ErrorHandler.logError("registerByDocument", "Error during readICAO", e)
            null
        }
    }
}