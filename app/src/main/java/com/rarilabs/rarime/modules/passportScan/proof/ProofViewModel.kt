package com.rarilabs.rarime.modules.passportScan.proof

import RegisterIdentityCircuitType
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.api.registration.PassportAlreadyRegisteredByOtherPK
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.modules.passportScan.CircuitUseCase
import com.rarilabs.rarime.modules.passportScan.DownloadRequest
import com.rarilabs.rarime.modules.passportScan.models.CryptoUtilsPassport
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.RegisterIdentityInputs
import com.rarilabs.rarime.modules.passportScan.nfc.SODFileOwn
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.SecurityUtil
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.ZkpUtil
import com.rarilabs.rarime.util.circuits.CircuitUtil
import com.rarilabs.rarime.util.circuits.RegisteredCircuitData
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.toBits
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.CallDataBuilder
import identity.X509Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.web3j.utils.Numeric
import java.io.IOException
import java.math.BigInteger
import java.util.concurrent.Executors
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

    private var _state = MutableStateFlow(PassportProofState.READING_DATA)
    val state: StateFlow<PassportProofState>
        get() = _state.asStateFlow()

    private val second = 1000L

    private val TAG = ProofViewModel::class.java.simpleName

    val eDoc = registrationManager.eDocument
    val regProof = registrationManager.registrationProof
    val pointsToken = walletManager.pointsToken

    suspend fun joinRewardProgram(eDocument: EDocument) {
        val res = pointsManager.joinRewardProgram(eDocument)
        res
    }

    private var _progress = MutableStateFlow(0)
    private var _progressVisibility = MutableStateFlow(false)
    val progress: StateFlow<Int>
        get() = _progress.asStateFlow()

    val progressVisibility: StateFlow<Boolean>
        get() = _progressVisibility.asStateFlow()

    private suspend fun registerCertificate(eDocument: EDocument) {
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
            icao,
            slaveCertificate.toByteArray()
        )

        val response = withContext(Dispatchers.IO) {
            registrationManager.relayerRegister(callData, BaseConfig.REGISTER_CONTRACT_ADDRESS)
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


        val customDispatcher = Executors.newFixedThreadPool(1) { runnable ->
            Thread(null, runnable, "LargeStackThread", 100 * 1024 * 1024) // 100 MB stack size
        }.asCoroutineDispatcher()


        val proof = withContext(customDispatcher) {
            when (registeredCircuitData) {
                RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_5_576_248_NA -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity125635576248NA
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_6_576_248_1_2432_5_296 -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity125636576248124325296
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_336_264_21_2448_6_2008 -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity22563633626421244862008
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_7_336_264_21_3072_6_2008 -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity212563733626421307262008
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_6_576_264_1_2448_3_256 -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity125636576264124483256
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_336_248_1_2432_3_256 -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity225636336248124323256
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_576_248_1_2432_3_256 -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity225636576248124323256
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_11_256_3_3_576_248_1_1184_5_264 -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity1125633576248111845264
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_12_256_3_3_336_232_NA -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity1225633336232NA
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_4_336_232_1_1480_5_296 -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity125634336232114805296
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_4_600_248_1_1496_3_256 -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity125634600248114963256
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_1_160_3_4_576_200_NA -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity116034576200NA
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_3_336_232_NA -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity2125633336232NA
                    )
                }

                RegisteredCircuitData.REGISTER_IDENTITY_24_256_3_4_336_232_NA -> {
                    zkp.generateRegisterZKP(
                        filePaths!!.zkey,
                        filePaths.zkeyLen,
                        filePaths.dat,
                        filePaths.datLen,
                        inputs,
                        ZkpUtil::registerIdentity2425634336232NA
                    )
                }
            }
        }

        return proof
    }

    suspend fun registerByDocument() {
        val eDocument = eDoc.value!!

        try {
            registerCertificate(eDocument)
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error: $e", e)
        }

        val registerIdentityCircuitType = try {
            eDocument.getRegisterIdentityCircuitType()
        } catch (e: Exception) {
            ErrorHandler.logError("registerByDocument", "Cant getRegisterIdentityCircuitType", e)
            throw e
        }


        val registerIdentityCircuitName = try {
            registerIdentityCircuitType.buildName()
        } catch (e: Exception) {
            ErrorHandler.logError(
                "registerIdentityCircuitType.buildName()",
                "cant get register identity name",
                e
            )
            ErrorHandler.logError(
                "registerIdentityCircuitType",
                Gson().toJson(registerIdentityCircuitType)
            )
            throw e
        }

        registrationManager.setCircuitData(registerIdentityCircuitType)

        ErrorHandler.logDebug("registerIdentityCircuitName", registerIdentityCircuitName)
        val registeredCircuitData = RegisteredCircuitData.fromValue(registerIdentityCircuitName)
            ?: throw IllegalStateException("Circuit $registerIdentityCircuitName is not supported")


        val filePaths = withContext(Dispatchers.Default) {
            CircuitUseCase(application as Context).download(registeredCircuitData) { progress, visibility ->
                if (_state.value.value < PassportProofState.APPLYING_ZERO_KNOWLEDGE.value) {
                    _progress.value = progress
                    _progressVisibility.value = !visibility
                }
            }
        }

        _state.value = PassportProofState.READING_DATA

        val proof = withContext(Dispatchers.IO) {
            generateRegisterIdentityProof(
                eDocument,
                registeredCircuitData,
                filePaths,
                registerIdentityCircuitType
            )
        }

        registrationManager.setRegistrationProof(proof)

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE
        _progressVisibility.value = false


        val passportInfo = try {
            registrationManager.getPassportInfo(eDocument)
        } catch (e: Exception) {
            ErrorHandler.logError(TAG, "Error: $e", e)
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
                    registerIdentityCircuitName
                )

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
                registerIdentityCircuitName
            )

            _state.value = PassportProofState.FINALIZING

            delay(second * 1)
        }
    }

    private suspend fun buildRegistrationCircuits(
        eDocument: EDocument,
        circuitType: RegisterIdentityCircuitType
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

            contract.getProof(indexHex.decodeHexString()).send()
        }

        val encapsulatedContent = Numeric.hexStringToByteArray(sodFile.readASN1Data())
        val signedAttributes = sodFile.eContent
        val publicKey = sodFile.getDocSigningCertificate().publicKey
        val signature = sodFile.encryptedDigest

        ErrorHandler.logDebug("sign", proof.siblings.size.toString())

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
            signedAttributes,
            2,
            smartChunkingToBlockSize.toLong()
        )

        val pubKeyChunks = if (publicKey is ECPublicKey) {
            pubKeyData.toBits().map { it }
        } else {
            CircuitUtil.smartChunking(
                BigInteger(1, pubKeyData),
                smartChunkingNumber
            ).map { it.toLong() }
        }

        val signatureChunks = if (publicKey is ECPublicKey) {
            CircuitUtil.parseECDSASignature(signature)?.toBits() ?: throw Exception("Invalid ECDSA signature")
        } else {
            CircuitUtil.smartChunking(
                BigInteger(1, signature),
                smartChunkingNumber
            ).map { it.toLong() }
        }


        val dg1Chunks = CircuitUtil.smartChunking2(
            eDocument.dg1!!.decodeHexString(),
            2,
            smartChunkingToBlockSize.toLong()
        )

        val inputs = RegisterIdentityInputs(
            skIdentity = Numeric.toHexStringWithPrefix(BigInteger(privateKeyBytes)),
            encapsulatedContent = encapsulatedChunks,
            signedAttributes = signedAttrChunks,
            pubkey = pubKeyChunks,
            signature = signatureChunks,
            dg1 = dg1Chunks,
            dg15 = dg15,
            slaveMerkleRoot = Numeric.toHexString(proof.root),
            slaveMerkleInclusionBranches = proof.siblings.map { Numeric.toHexString(it) }
        )



        registrationManager.setMasterCertProof(proof)

        val clipboard =
            (application as Context).getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText(null, Gson().toJson(inputs))
        clipboard!!.setPrimaryClip(clip)

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