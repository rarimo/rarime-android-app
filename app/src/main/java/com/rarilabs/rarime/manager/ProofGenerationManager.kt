@file:OptIn(ExperimentalStdlibApi::class)

package com.rarilabs.rarime.manager

import CircuitAlgorithmType
import CircuitPassportHashType
import RegisterIdentityCircuitType
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.noirandroid.lib.Circuit
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.api.registration.PassportAlreadyRegisteredByOtherPK
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.modules.passportScan.CircuitDownloader
import com.rarilabs.rarime.modules.passportScan.CircuitNoirDownloader
import com.rarilabs.rarime.modules.passportScan.DownloadCircuitError
import com.rarilabs.rarime.modules.passportScan.DownloadRequest
import com.rarilabs.rarime.modules.passportScan.models.CryptoUtilsPassport
import com.rarilabs.rarime.modules.passportScan.models.EDocument
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
import kotlinx.coroutines.coroutineScope
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

    private suspend fun registerByDocument(eDocument: EDocument): UniversalProof {
        try {
            _state.value = PassportProofState.READING_DATA
            val circuitType = getCircuitType(eDocument)
            registrationManager.setCircuitData(circuitType)
            val circuitName = getCircuitName(circuitType)

            _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

            val proof = if (RegisterNoirCircuitData.fromValue(circuitType.buildName()) != null) {
                generateRegisterIdentityProofPlonk(
                    eDocument, registerIdentityCircuitType = circuitType
                )
            } else {

                generateRegisterIdentityProofGroth(eDocument, circuitType)
            }


            if (!BuildConfig.isTestnet) {
                try {
                    ErrorHandler.logDebug(TAG, "Deleting redundant circuit files")
                    //circuitDownloader.deleteRedunantFiles(circuitData)
                } catch (e: Exception) {
                    ErrorHandler.logError(TAG, "Error deleting redundant circuit files", e)
                }
            }

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

    private suspend fun lightRegistration(eDocument: EDocument): UniversalProof.Light {
        try {
            if (privateKeyBytes == null) throw IllegalStateException("privateKeyBytes is null")
            _state.value = PassportProofState.READING_DATA

            val registerIdentityCircuitName = eDocument.getRegisterIdentityLightCircuitName()
            ErrorHandler.logDebug(TAG, "registerIdentityCircuitName: $registerIdentityCircuitName")
            val registeredCircuitData = RegisteredCircuitData.fromValue(registerIdentityCircuitName)
                ?: throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")


            _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

            // Download circuit files
            val filePaths = withContext(Dispatchers.Default) {
                CircuitDownloader(application).downloadGrothFiles(registeredCircuitData) { progress, visibility ->
                    _downloadProgress.value = progress
                }
            } ?: throw DownloadCircuitError()


            val lightProof = withContext(Dispatchers.Default) {
                generateLightRegistrationProof(
                    filePaths, eDocument, privateKeyBytes, registeredCircuitData
                )
            }

            _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE

            val registerResponse = registrationManager.lightRegistration(eDocument, lightProof)
            val profile = identityManager.getProfiler()
            val currentIdentityKey = profile.publicKeyHash

            val universalProof =
                UniversalProofFactory.fromLight(registerResponse.data.attributes, lightProof)

            val passportInfoKey = withContext(Dispatchers.IO) {
                registrationManager.getPassportInfo(
                    eDocument, universalProof
                )!!.component1()
            }
            if (passportInfoKey.activeIdentity.contentEquals(currentIdentityKey)) {
                ErrorHandler.logDebug(TAG, "Passport is already registered with this PK")
                registrationManager.setRegistrationProof(universalProof)
                identityManager.setLightRegistrationData(registerResponse.data.attributes)
                return UniversalProof.fromLight(registerResponse.data.attributes, lightProof)
            }
            delay(second * 2)
            _state.value = PassportProofState.FINALIZING
            val res = withContext(Dispatchers.IO) {
                registrationManager.lightRegisterRelayer(lightProof, registerResponse)
            }
            res
            registrationManager.setRegistrationProof(universalProof)
            identityManager.setLightRegistrationData(registerResponse.data.attributes)
            delay(second)
            return UniversalProof.fromLight(registerResponse.data.attributes, lightProof)
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

        ErrorHandler.logDebug("Plonk", "Plonk Start registration")

        val trustedSetupPath =
            circuitDownloader.downloadTrustedSetup(onProgressUpdate = { progress, isEnded ->
                if (progress != _downloadProgress.value) {
                    _downloadProgress.value = progress
                }
            })

        ErrorHandler.logDebug("Plonk", "Plonk Circuit downloaded")


        val circuitData = RegisterNoirCircuitData.fromValue(registerIdentityCircuitType.buildName())

        val byteCodePath =
            circuitDownloader.downloadNoirByteCode(circuitData = circuitData!!) { progress, isEnded ->

                if (_downloadProgress.value != progress) {
                    _downloadProgress.value = progress
                }
            }


        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        val inputs = buildPlonkRegistrationInputs(eDocument, registerIdentityCircuitType)

        return withContext(customDispatcher) {
            val circuitByteCode = File(byteCodePath).readText()


            val circuit = Circuit.fromJsonManifest(circuitByteCode)

            circuit.setupSrs(trustedSetupPath, false)

            ErrorHandler.logDebug("Plonk", "Start proving")

            val proof = circuit.prove(inputs, proofType = "plonk", recursive = false)

            val zk = UniversalProofFactory.fromPlonkBytes(Numeric.hexStringToByteArray(proof.proof))

            return@withContext zk
        }
    }

    private suspend fun generateRegisterIdentityProofGroth(
        eDocument: EDocument, registerIdentityCircuitType: RegisterIdentityCircuitType
    ): UniversalProof {

        val circuitName = getCircuitName(registerIdentityCircuitType)
        val circuitData = getCircuitData(circuitName)

        val circuitDownloader = CircuitDownloader(application)

        val filePaths = withContext(Dispatchers.Default) {
            circuitDownloader.downloadGrothFiles(circuitData) { progress, visibility ->
                _downloadProgress.value = progress
            }
        } ?: throw DownloadCircuitError()

        ErrorHandler.logDebug(TAG, "Generating Groth registration proof")

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        val inputs = buildGrothRegistrationInputs(eDocument, registerIdentityCircuitType)
        val assetContext: Context = application.createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets
        val zkp = ZKPUseCase(application, assetManager)

        val proof = UniversalProofFactory.fromGroth(
            generateRegistrationProofByCircuitType(
                circuitData, filePaths, zkp, inputs
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
            skIdentity = Numeric.toHexString(privateKey), dg1 = dg1Chunks
        )
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


    private suspend fun buildPlonkRegistrationInputs(
        eDocument: EDocument, circuitType: RegisterIdentityCircuitType
    ): Map<String, Any> = withContext(Dispatchers.IO) {

        val sodFile = eDocument.getSodFile()
        val toHexList: (ByteArray) -> List<String> = { bytes ->
            bytes.map { byte -> Numeric.toHexString(byteArrayOf(byte)) }
        }

        coroutineScope {
            val proofDeferred = async {
                val cert = sodFile.docSigningCertificate
                val certPem = SecurityUtil.convertToPEM(cert)
                val icao = readICAO(application.applicationContext)
                val x509Utils = X509Util()
                val slaveCertificateIndex =
                    x509Utils.getSlaveCertificateIndex(certPem.toByteArray(), icao)
                val contract =
                    rarimoContractManager.getPoseidonSMT(BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS)
                contract.getProof(slaveCertificateIndex.toHexString().hexToByteArray()).send()
            }


            val publicKey = sodFile.docSigningCertificate.publicKey
            val sigBytes = sodFile.encryptedDigest
            val pubKeyData = CryptoUtilsPassport.getDataFromPublicKey(publicKey)!!
            val (pk, reductionPk, sig) = processSignatureData(circuitType, pubKeyData, sigBytes)


            val dg1Deferred = toHexList(eDocument.dg1!!.decodeHexString())
            val dg15Deferred = eDocument.dg15?.decodeHexString()?.let(toHexList) ?: listOf()
            val ecDeferred = toHexList(Numeric.hexStringToByteArray(sodFile.readASN1Data()))
            val saDeferred = toHexList(sodFile.eContent)
            val skIdentityDeferred = Numeric.toHexString(privateKeyBytes)

            val proof = proofDeferred.await()

            registrationManager.setMasterCertProof(proof)

            mapOf(
                "dg15" to dg15Deferred,
                "sa" to saDeferred,
                "pk" to pk,
                "icao_root" to Numeric.toHexString(proof.root),
                "inclusion_branches" to proof.siblings.map { Numeric.toHexString(it) },
                "ec" to ecDeferred,
                "sk_identity" to skIdentityDeferred,
                "dg1" to dg1Deferred,
                "sig" to sig,
                "reduction_pk" to reductionPk
            )
        }
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

    private fun processSignatureData(
        circuitType: RegisterIdentityCircuitType, pubKeyData: ByteArray, sigBytes: ByteArray
    ): Triple<List<String>, List<String>, List<String>> {
        val toHex: (BigInteger) -> String = { Numeric.toHexString(it.toByteArray()) }

        return when (circuitType.signatureType.algorithm) {
            CircuitAlgorithmType.RSA, CircuitAlgorithmType.RSAPSS -> {
                val pk = CircuitUtil.splitBy120Bits(pubKeyData).map(toHex)
                val reductionPk = CircuitUtil.rsaBarrettReductionParam(
                    BigInteger(1, pubKeyData), pubKeyData.size * 8
                ).map(toHex)
                val sig = CircuitUtil.splitBy120Bits(sigBytes).map(toHex)
                Triple(pk, reductionPk, sig)
            }

            CircuitAlgorithmType.ECDSA -> {
                val half = pubKeyData.size / 2
                val pubKeyX = pubKeyData.copyOfRange(0, half)
                val pubKeyY = pubKeyData.copyOfRange(half, pubKeyData.size)

                val pk =
                    (CircuitUtil.splitBy120Bits(pubKeyX) + CircuitUtil.splitBy120Bits(pubKeyY)).map(
                        toHex
                    )
                val reductionPk =
                    (CircuitUtil.splitEmptyData(pubKeyX) + CircuitUtil.splitEmptyData(pubKeyY)).map(
                        toHex
                    )

                val sigBytes64 = CircuitUtil.parseECDSASignature(sigBytes)!!
                val sigHalf = sigBytes64.size / 2
                val sigR = sigBytes64.copyOfRange(0, sigHalf)
                val sigS = sigBytes64.copyOfRange(sigHalf, sigBytes64.size)

                val sig =
                    (CircuitUtil.splitBy120Bits(sigR) + CircuitUtil.splitBy120Bits(sigS)).map(toHex)
                Triple(pk, reductionPk, sig)
            }
        }
    }

}