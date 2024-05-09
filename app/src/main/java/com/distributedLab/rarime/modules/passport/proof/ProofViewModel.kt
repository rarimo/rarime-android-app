package com.distributedLab.rarime.modules.passport.proof

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.manager.ApiServiceRemoteData
import com.distributedLab.rarime.data.manager.ContractManager
import com.distributedLab.rarime.domain.data.EvmTxResponse
import com.distributedLab.rarime.domain.data.RegisterRequest
import com.distributedLab.rarime.domain.data.RegisterRequestData
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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.CallDataBuilder
import identity.Profile
import identity.X509Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val ENCAPSULATED_CONTENT_2688 = 2688
const val ENCAPSULATED_CONTENT_2704 = 2704

@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class ProofViewModel @Inject constructor(
    application: Application,
    private val dataStoreManager: SecureSharedPrefsManager,
    private val apiService: ApiServiceRemoteData,
    private val contractManager: ContractManager
) : AndroidViewModel(application) {
    val zkp = ZKPUseCase(application as Context)

    private val secretKey: ByteArray = dataStoreManager.readPrivateKey()!!.decodeHexString()

    private lateinit var proof: ZkProof
    private var _state = MutableStateFlow(PassportProofState.READING_DATA)

    val state: StateFlow<PassportProofState>
        get() = _state.asStateFlow()

    fun getRegistrationProof(): ZkProof {
        return proof
    }

    private suspend fun registerMasterCertificate(eDocument: EDocument) {
        val sod = SODFileOwn(eDocument.sod!!.decodeHexString().inputStream())
        val certPem = SecurityUtil.convertToPem(sod.docSigningCertificate)

        val certificatesSMTAddress = withContext(Dispatchers.IO) {
            contractManager.getRegistration().certificatesSmt().send()
        }


        val x509Util = X509Util()

        val masterCertificateIndex =
            x509Util.getMasterCertificateIndex(certPem.toByteArray(), "ICAO".toByteArray())

        val proof = withContext(Dispatchers.IO) {
            apiService.getProof(masterCertificateIndex, certificatesSMTAddress)
        }

        if (proof?.existence == true) {
            return
        }


        val callDataBuilder = CallDataBuilder()
        val callData = callDataBuilder.buildRegisterCertificateCalldata(certPem.toByteArray(), "ICAO".toByteArray())

        register(callData)
    }

    private fun buildRegistrationInputs(eDocument: EDocument): ByteArray {

        _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE
        val sodFile = SODFileOwn(eDocument.sod!!.decodeHexString().inputStream())

        val encapsulatedContent =
            sodFile.readASN1Data()!!.toHexString().substring(8).decodeHexString()
        val signedAttribute = sodFile.eContent

        val certificate = sodFile.docSigningCertificate
        val publicKeyPem = certificate.publicKey.publicKeyToPem()

        val signature = sodFile.encryptedDigest

        val profile = Profile().newProfile(secretKey)

        Log.i("DG1hex", eDocument.dg1!!.toByteArray().toHexString())

        Log.i("DG1hex", eDocument.dg15!!)

        return profile.buildRegisterIdentityInputs(
            encapsulatedContent,
            signedAttribute,
            eDocument.dg1!!.toByteArray().toHexString().decodeHexString(),
            eDocument.dg15!!.decodeHexString(),
            publicKeyPem.toByteArray(),
            signature,
            false,
            "".toByteArray()
        )
    }

    private fun generateRegisterIdentityProof(edocument: EDocument): ZkProof? {

        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE
        val inputs = buildRegistrationInputs(edocument)
        val sodFile = SODFileOwn(edocument.sod!!.decodeHexString().inputStream())

        val size = sodFile.readASN1Data()!!.toHexString().substring(8).decodeHexString().size * 8

        Log.e("SIZE", size.toString())
        zkp.generateZKP(
            zkeyFileName = "circuit_registration.zkey",
            R.raw.register_identity_universal,
            inputs,
            ZkpUtil::registerIdentityUniversal
        )

        Log.e("Proof", GsonBuilder().setPrettyPrinting().create().toJson(proof))

        return proof
    }

    private suspend fun register(callData: ByteArray): EvmTxResponse {

        val payload =
            RegisterRequest(data = RegisterRequestData(tx_data = "0x" + callData.toHexString()))

        val gson = GsonBuilder().setPrettyPrinting().create()
        Log.i("Payload", gson.toJson(payload))
        return apiService.sendRegistration(payload)!!
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun generateProof(eDocument: EDocument) {


        _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE

        proof = withContext(Dispatchers.IO) { generateRegisterIdentityProof(eDocument)!! }
        val proofJson = Gson().toJson(proof)
        val sodFile = SODFileOwn(eDocument.sod!!.decodeHexString().inputStream())

        Log.i("SOD: ", sodFile.encoded.toHexString())

        sodFile.docSigningCertificate.publicKey

        Log.i("PUBKEY PEM", sodFile.docSigningCertificate.publicKey.publicKeyToPem())


        val callData = CallDataBuilder().buildRegisterCalldata(
            proofJson.toByteArray(),
            eDocument.aaSignature,
            sodFile.docSigningCertificate.publicKey.publicKeyToPem().toByteArray(),
            (sodFile.readASN1Data()!!.toHexString().substring(8)
                .decodeHexString().size * 8).toLong(),
        )

        //val response = register(callData)

        //Log.i("Register", response.toString())

        dataStoreManager.saveEDocument(eDocument)
        dataStoreManager.saveRegistrationProof(proof)

        _state.value = PassportProofState.FINALIZING
    }
}


fun preprocessMessage(message: ByteArray): ByteArray {
    val messageLengthBits = message.size * 8
    val messageWithOneBit = message + byteArrayOf(0x80.toByte())

    val currentMod512 = (messageWithOneBit.size * 8) % 512
    val zeroPaddingNeeded = if (currentMod512 <= 448) {
        448 - currentMod512
    } else {
        512 - currentMod512 + 448
    }

    val zeroPaddingBytes = ByteArray(zeroPaddingNeeded / 8)
    val lengthInBits = messageLengthBits.toLong()

    val lengthBytes = ByteArray(8)
    for (i in 0..7) {
        lengthBytes[7 - i] = (lengthInBits shr (i * 8) and 0xFF).toByte()
    }

    return messageWithOneBit + zeroPaddingBytes + lengthBytes
}