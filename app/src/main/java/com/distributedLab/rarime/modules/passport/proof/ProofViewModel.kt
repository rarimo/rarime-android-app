package com.distributedLab.rarime.modules.passport.proof

import android.R.attr.label
import android.R.attr.text
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.R
import com.distributedLab.rarime.contracts.PoseidonSMT.Proof
import com.distributedLab.rarime.data.manager.ApiServiceRemoteData
import com.distributedLab.rarime.data.manager.ContractManager
import com.distributedLab.rarime.domain.data.ProofTx
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.modules.passport.PassportProofState
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.nfc.SODFileOwn
import com.distributedLab.rarime.util.SecurityUtil
import com.distributedLab.rarime.util.ZKPUseCase
import com.distributedLab.rarime.util.ZkpUtil
import com.distributedLab.rarime.util.data.ZkProof
import com.distributedLab.rarime.util.decodeHexString
import com.distributedLab.rarime.util.publicKeyToPem
import com.distributedLab.rarime.util.toBase64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.CallDataBuilder
import identity.Profile
import identity.X509Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.bouncycastle.asn1.eac.ECDSAPublicKey
import org.jmrtd.lds.icao.DG15File
import java.io.IOException
import javax.inject.Inject


@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class ProofViewModel @Inject constructor(
    private val application: Application,
    private val dataStoreManager: SecureSharedPrefsManager,
    private val apiService: ApiServiceRemoteData,
    private val contractManager: ContractManager
) : AndroidViewModel(application) {
    private val TAG = ProofViewModel::class.java.simpleName
    private val zkp = ZKPUseCase(application as Context)

    private lateinit var proof: ZkProof
    private var _state = MutableStateFlow(PassportProofState.READING_DATA)
    private lateinit var masterCertProof: Proof

    private val second = 1000L
    val state: StateFlow<PassportProofState>
        get() = _state.asStateFlow()

    fun getRegistrationProof(): ZkProof {
        return proof
    }

    private suspend fun registerCertificate(eDocument: EDocument) {
        val sodStream = eDocument.sod!!.decodeHexString().inputStream()
        val sodFile = SODFileOwn(sodStream)


        val certificate = SecurityUtil.convertToPEM(sodFile.docSigningCertificate)

        val certificatesSMTAddress = withContext(Dispatchers.IO) {
            val registrationContract = contractManager.getRegistration()
            registrationContract.certificatesSmt().send()
        }

        val x509Util = X509Util()


        val proof = withContext(Dispatchers.IO) {
            val icao = readICAO(context = application.applicationContext)
            val slaveCertificateIndex =
                x509Util.getSlaveCertificateIndex(certificate.toByteArray(), icao)
            var indexHex = slaveCertificateIndex.toHexString()
            val poseidonSMT = contractManager.getPoseidonSMT(certificatesSMTAddress)
            poseidonSMT.getProof(indexHex.decodeHexString()).send()
        }


        if (proof?.existence == true) {
            return
        }

        val callDataBuilder = CallDataBuilder()
        val callData = callDataBuilder.buildRegisterCertificateCalldata(
            BaseConfig.ICAO_COSMOS_RPC,
            certificate.toByteArray(),
            BaseConfig.MASTER_CERTIFICATES_BUCKETNAME,
            BaseConfig.MASTER_CERTIFICATES_FILENAME
        )

        val response = withContext(Dispatchers.IO) {
            apiService.sendRegistration(callData)
        }

        if (response == null) {
            return
        }

        Log.i(TAG, "Passport certificate EVM Tx Hash " + response.data.attributes.tx_hash)


        val res = contractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)
        if (!res) {
            Log.e(TAG, "Transaction failed" + response.data.attributes.tx_hash)
        }
    }

    private suspend fun generateRegisterIdentityProof(eDocument: EDocument): ZkProof {
        val inputs = buildRegistrationCircuits(eDocument)

        Log.i("INPUTS", inputs.decodeToString())

        val manager = ContextCompat.getSystemService(application as Context, ClipboardManager::class.java)
        manager!!.setPrimaryClip(
            ClipData.newPlainText(
                "label",
                inputs.decodeToString()
            )
        )


        val proof = withContext(Dispatchers.Default) {
            zkp.generateZKP(
                "circuit_registration.zkey",
                R.raw.register_identity_universal,
                inputs,
                ZkpUtil::registerIdentityUniversal
            )
        }

        this.proof = proof

        return proof
    }

    suspend fun registerByDocument(eDocument: EDocument) {

        _state.value = PassportProofState.READING_DATA

        registerCertificate(eDocument)

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE
        val proof = generateRegisterIdentityProof(eDocument)


        _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE
        register(proof, eDocument)


        _state.value = PassportProofState.FINALIZING

        dataStoreManager.saveEDocument(eDocument)
        dataStoreManager.saveRegistrationProof(proof)

        delay(second * 1)
    }

    private suspend fun register(zkProof: ZkProof, eDocument: EDocument) {
        val jsonProof = Gson().toJson(zkProof)

        val dG15File = DG15File(eDocument.dg15!!.decodeHexString().inputStream())

        val pubKeyPem = dG15File.publicKey.publicKeyToPem()

        val callDataBuilder = CallDataBuilder()
        val callData = callDataBuilder.buildRegisterCalldata(
            jsonProof.toByteArray(),
            eDocument.aaSignature,
            pubKeyPem.toByteArray(),
            masterCertProof.root
        )

        withContext(Dispatchers.IO) {
            val response = apiService.sendRegistration(callData)
            contractManager.checkIsTransactionSuccessful(response!!.data.attributes.tx_hash)
        }
    }

    private suspend fun buildRegistrationCircuits(eDocument: EDocument): ByteArray {
        val secretKey = dataStoreManager.readPrivateKey()

        val sodStream = eDocument.sod!!.decodeHexString().inputStream()
        val sodFile = SODFileOwn(sodStream)
        val dg15 = eDocument.dg15!!.decodeHexString()
        val dG15File = DG15File(dg15.inputStream())

        val cert = sodFile.docSigningCertificate
        val certPem = SecurityUtil.convertToPEM(cert)

        val registrationContract = contractManager.getRegistration()

        val certificatesSMTAddress = withContext(Dispatchers.IO) {
            registrationContract.certificatesSmt().send()
        }

        val x509Utils = X509Util()


        val proof = withContext(Dispatchers.IO) {
            val icao = readICAO(context = application.applicationContext)

            val slaveCertificateIndex =
                x509Utils.getSlaveCertificateIndex(certPem.toByteArray(), icao)
            val indexHex = slaveCertificateIndex.toHexString()
            val contract = contractManager.getPoseidonSMT(certificatesSMTAddress)

            contract.getProof(indexHex.decodeHexString()).send()
        }

        val encapsulatedContent = sodFile.readASN1Data()
        val signedAttributes = sodFile.eContent

        val publicKey = sodFile.docSigningCertificate.publicKey
        val publicKeyPem = publicKey.publicKeyToPem()

        val signature = sodFile.encryptedDigest

        val identityProfile = Profile()
        val profile = identityProfile.newProfile(secretKey!!.decodeHexString())

        val dg15PublicKey = dG15File.publicKey
        val isEcdsaActiveAuthentication = true//(dg15PublicKey is ECDSAPublicKey)


        val gson = GsonBuilder().create()

        Log.i("sign", proof.siblings.size.toString())

        val proofTx = ProofTx(
            proof.root.toBase64(),
            proof.siblings.map { it.toBase64() },
            existence = proof.existence,
        )

        val proofJson = gson.toJson(proofTx)

        Log.i("proof", proofJson)

        val inputs = profile.buildRegisterIdentityInputs(
            encapsulatedContent.decodeHexString(),
            signedAttributes,
            eDocument.dg1!!.decodeHexString(),
            eDocument.dg15!!.decodeHexString(),
            publicKeyPem.toByteArray(),
            signature,
            isEcdsaActiveAuthentication,
            proofJson.toByteArray()
        )


        this.masterCertProof = proof!!

        return inputs
    }


    private fun readICAO(context: Context): ByteArray? {
        return try {

            context.assets.open("masters.pem").use { inputStream ->
                val res = inputStream.readBytes()
                res
            }


        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


}
