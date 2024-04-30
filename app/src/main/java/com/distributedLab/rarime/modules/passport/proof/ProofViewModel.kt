package com.distributedLab.rarime.modules.passport.proof

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.distributedLab.rarime.modules.passport.PassportProofState
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.nfc.SODFileOwn
import com.distributedLab.rarime.util.SecurityUtil
import com.distributedLab.rarime.util.ZKPUseCase
import com.distributedLab.rarime.util.addCharAtIndex
import com.distributedLab.rarime.util.decodeHexString
import com.distributedLab.rarime.util.publicKeyToPem
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import identity.Profile
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class ProofViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val zkp = ZKPUseCase(application as Context)

    val clipboardManager =
        (application as Context).getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private var _state = MutableStateFlow(PassportProofState.READING_DATA)

    val state: StateFlow<PassportProofState>
        get() = _state.asStateFlow()


    @OptIn(ExperimentalStdlibApi::class)
    suspend fun generateProof(eDocument: EDocument) {

        coroutineScope {
            val sodFile = SODFileOwn(eDocument.sod!!.decodeHexString().inputStream())
            val startTime = System.currentTimeMillis()

            val endTime = System.currentTimeMillis()

            val privKey = Identity.newBJJSecretKey()
            val profile = Profile().newProfile(privKey)
            val challlenge = profile.registrationChallenge

            val encapsulatedContent =
                sodFile.readASN1Data()!!.toHexString().substring(8).decodeHexString()
            val signedAttribute = sodFile.eContent

            val certificate = sodFile.docSigningCertificate
            var pemFile = SecurityUtil.convertToPem(certificate)


            val index = pemFile.indexOf("-----END CERTIFICATE-----")
            pemFile = pemFile.addCharAtIndex('\n', index)

            Log.d("PUB", sodFile.docSigningCertificate.publicKey.encoded.toHexString())

            Log.d("Cert", pemFile)

            val inputs = profile.buildRegisterIdentityInputs(
                preprocessMessage(encapsulatedContent),//
                preprocessMessage(signedAttribute),//
                preprocessMessage(eDocument.dg1!!.toByteArray()),//
                preprocessMessage(eDocument.dg15!!.toByteArray()),//
                sodFile.docSigningCertificate.publicKey.publicKeyToPem().toByteArray(),
                sodFile.encryptedDigest
            )



            Log.d("inputs", inputs.decodeToString())
            val clip = ClipData.newPlainText("password", inputs.decodeToString())

            Log.i("encapsulated content", encapsulatedContent.toHexString())
            clipboardManager.setPrimaryClip(clip)

            _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE
//            val proof = zkp.generateZKP(
//                "registerIdentityZkey.zkey",
//                R.raw.register_identity,
//                inputs,
//                ZkpUtil::registerIdentity
//            )


            _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE


            Log.e("Proof", ((endTime - startTime) / 1000).toString())

            _state.value = PassportProofState.FINALIZING
        }
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