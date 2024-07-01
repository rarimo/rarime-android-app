package com.rarilabs.rarime.modules.passportScan.proof

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.data.ProofTx
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.passportScan.CircuitUseCase
import com.rarilabs.rarime.modules.passportScan.DownloadRequest
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.RegisteredCircuitData
import com.rarilabs.rarime.modules.passportScan.nfc.SODFileOwn
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.SecurityUtil
import com.rarilabs.rarime.util.ZKPUseCase
import com.rarilabs.rarime.util.ZkpUtil
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.publicKeyToPem
import com.rarilabs.rarime.util.toBase64
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.CallDataBuilder
import identity.Identity
import identity.Profile
import identity.X509Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class ProofViewModel @Inject constructor(
    private val application: Application,
    private val identityManager: IdentityManager,
    private val registrationManager: RegistrationManager,
    private val rarimoContractManager: RarimoContractManager,
    private val pointsManager: PointsManager
) : AndroidViewModel(application) {
    private val privateKeyBytes = identityManager.privateKeyBytes

    private val TAG = ProofViewModel::class.java.simpleName

    private lateinit var registerIdentityProof: ZkProof
    private var _state = MutableStateFlow(PassportProofState.READING_DATA)

    private val second = 1000L
    val state: StateFlow<PassportProofState>
        get() = _state.asStateFlow()

    fun getRegistrationProof(): ZkProof {
        return registerIdentityProof
    }

    suspend fun joinRewardProgram(eDocument: EDocument) {
        val res = pointsManager.joinRewardProgram(eDocument)
        res
    }

    private suspend fun registerCertificate(eDocument: EDocument): RegisteredCircuitData {
        val sodStream = eDocument.sod!!.decodeHexString().inputStream()
        val sodFile = SODFileOwn(sodStream)

        val publicKey = sodFile.docSigningCertificate.publicKey
        val publicKeyPem = publicKey.publicKeyToPem()
        val publicKeyBytes = publicKeyPem.toByteArray()

        val x509Util = X509Util()

        val pubKeySize = x509Util.getRSASize(publicKeyBytes)

        val certificate = SecurityUtil.convertToPEM(sodFile.docSigningCertificate)

        val certificatesSMTAddress = BaseConfig.CERTIFICATES_SMT_CONTRACT_ADDRESS

        val certificatesSMTContract = rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)

        val icao = readICAO(context = application.applicationContext)
        val slaveCertificateIndex =
            x509Util.getSlaveCertificateIndex(certificate.toByteArray(), icao)

        val proof = withContext(Dispatchers.IO) {
            certificatesSMTContract.getProof(slaveCertificateIndex).send()
        }

        if (proof?.existence == true) {
            ErrorHandler.logDebug("ProofViewModel", "Passport certificate is already registered")
            return if (pubKeySize == 4096L) {
                RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA4096
            } else {
                RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA2048
            }
        }
        val callDataBuilder = CallDataBuilder()
        val callData = callDataBuilder.buildRegisterCertificateCalldata(
            BaseConfig.ICAO_COSMOS_RPC,
            certificate.toByteArray(),
            BaseConfig.MASTER_CERTIFICATES_BUCKETNAME,
            BaseConfig.MASTER_CERTIFICATES_FILENAME //TODO: CHECK VAL
        )

//        val proof = withContext(Dispatchers.IO) {
//            val icao = readICAO(context = application.applicationContext)
//            val slaveCertificateIndex =
//                x509Util.getSlaveCertificateIndex(certificate.toByteArray(), icao)
//            val indexHex = slaveCertificateIndex.toHexString()
//            val poseidonSMT = rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)
//            poseidonSMT.getProof(indexHex.decodeHexString()).send()
//        }

        val response = withContext(Dispatchers.IO) {
            registrationManager.relayerRegister(callData)
        }

        ErrorHandler.logDebug(TAG, "Passport certificate EVM Tx Hash " + response.data.attributes.tx_hash)

        val res =
            rarimoContractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)
        if (!res) {
            ErrorHandler.logError(TAG, "Transaction failed" + response.data.attributes.tx_hash)
        }
        return if (pubKeySize == 4096L) {
            RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA4096
        } else {
            RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA2048
        }
    }

    private suspend fun generateRegisterIdentityProof(
        eDocument: EDocument,
        registeredCircuitData: RegisteredCircuitData,
        filePaths: DownloadRequest?
    ): ZkProof {
        ErrorHandler.logDebug("ProofViewModel", "Generating proof")
        try {
            ErrorHandler.logDebug("eDocument", Gson().toJson(eDocument))
        } catch (e: Exception) {
            ErrorHandler.logError("Err log eDocument", "Error: $e", e)
        }

        val inputs = buildRegistrationCircuits(eDocument)

        try {
            ErrorHandler.logDebug("inputs", Gson().toJson(inputs))
        } catch (e: Exception) {
            ErrorHandler.logError("Err log inputs", "Error: $e", e)
        }

        //copyToClipboard(application as Context, inputs.decodeToString())
        val assetContext: Context =
            (application as Context).createPackageContext("com.rarilabs.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(application as Context, assetManager)

        val proof = withContext(Dispatchers.Default) {
            when (registeredCircuitData) {
                RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA2048 -> zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentityUniversalRSA2048
                )

                RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA4096 -> zkp.generateRegisterZKP(
                    filePaths!!.zkey,
                    filePaths.zkeyLen,
                    filePaths.dat,
                    filePaths.datLen,
                    inputs,
                    ZkpUtil::registerIdentityUniversalRSA4096
                )
            }
        }

        try {
            ErrorHandler.logDebug("proof", Gson().toJson(proof))
        } catch (e: Exception) {
            ErrorHandler.logError("Err log proof", "Error: $e", e)
        }

        this.registerIdentityProof = proof

        return proof
    }

    suspend fun registerByDocument(eDocument: EDocument) {
        _state.value = PassportProofState.READING_DATA

        val registeredCircuitData = registerCertificate(eDocument)

        val filePaths = withContext(Dispatchers.Default) {
            CircuitUseCase(application as Context).download(registeredCircuitData)
        }
        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE
        val proof = generateRegisterIdentityProof(eDocument, registeredCircuitData, filePaths)

        registrationManager.setCertSize(
            when (registeredCircuitData) {
                RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA2048 -> 2048L
                RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA4096 -> 4096L
            }
        )

        val passportInfoKey: String = if (eDocument.dg15?.isEmpty() ?: false) {
            proof.pub_signals[1]
        } else {
            proof.pub_signals[0]
        }

        var passportInfoKeyBytes = Identity.bigIntToBytes(passportInfoKey)

        if (passportInfoKeyBytes.size != 32) {
            passportInfoKeyBytes = passportInfoKeyBytes.copyOf(32)
        }

        ErrorHandler.logDebug("passportInfoKeyBytes", passportInfoKeyBytes.size.toString())

        _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE

        registrationManager.register(
            proof,
            eDocument,
            registrationManager.masterCertProof.value!!,
            registrationManager.certificatePubKeySize.value,
            false
        )

        _state.value = PassportProofState.FINALIZING

        delay(second * 1)
    }

    private suspend fun buildRegistrationCircuits(eDocument: EDocument): ByteArray {
        val sodStream = eDocument.sod!!.decodeHexString().inputStream()
        val sodFile = SODFileOwn(sodStream)
        //val dG15File = DG15File(dg15.inputStream())

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

        val encapsulatedContent = sodFile.readASN1Data()
        val signedAttributes = sodFile.eContent

        val publicKey = sodFile.docSigningCertificate.publicKey
        val publicKeyPem = publicKey.publicKeyToPem()

        val signature = sodFile.encryptedDigest

        val identityProfile = Profile()
        val profile = identityProfile.newProfile(privateKeyBytes)

        //val dg15PublicKey = dG15File.publicKey
        //ErrorHandler.logDebug("DG15File", dg15PublicKey::class.java.name)

        val gson = GsonBuilder().setPrettyPrinting().create()

        ErrorHandler.logDebug("sign", proof.siblings.size.toString())

        val proofTx = ProofTx(
            proof.root.toBase64(),
            proof.siblings.map { it.toBase64() },
            existence = proof.existence,
        )

        val proofJson = gson.toJson(proofTx)

        ErrorHandler.logDebug("proofTX", proofJson)

        val inputs = profile.buildRegisterIdentityInputs(
            encapsulatedContent.decodeHexString(),
            signedAttributes,
            eDocument.dg1!!.decodeHexString(),
            if (eDocument.dg15 == null) {
                ByteArray(0)
            } else {
                eDocument.dg15!!.decodeHexString()
            },
            publicKeyPem.toByteArray(Charsets.UTF_8),
            signature,
            proofJson.toByteArray(Charsets.UTF_8)
        )

        registrationManager.setMasterCertProof(proof)

        return inputs
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
            e.printStackTrace()
            null
        }
    }

    //DEV
    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }

}