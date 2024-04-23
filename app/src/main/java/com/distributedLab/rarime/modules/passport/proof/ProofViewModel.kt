package com.distributedLab.rarime.modules.passport.proof

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.nfc.SODFileOwn
import com.distributedLab.rarime.util.SecurityUtil
import com.distributedLab.rarime.util.ZKPUseCase
import com.distributedLab.rarime.util.ZkpUtil
import com.distributedLab.rarime.util.addCharAtIndex
import com.distributedLab.rarime.util.decodeHexString
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import identity.Profile
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


enum class GenerateProofState {
    READING_DATA, GENERATE_PROOF, CREATING_PROFILE, FINALIZING
}

@HiltViewModel
class ProofViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val zkp = ZKPUseCase(application as Context)

    val clipboardManager =
        (application as Context).getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private var _state = MutableStateFlow(GenerateProofState.READING_DATA)

    val state: StateFlow<GenerateProofState>
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

            Log.d("Cert", pemFile)

            val inputs = profile.buildRegisterIdentityInputs(
                privKey,
                encapsulatedContent,
                signedAttribute,
                eDocument.dg1!!.toByteArray(),
                eDocument.dg15!!.toByteArray(),
                eDocument.dg15Pem!!.toByteArray(),
                sodFile.eContent
            )


            Log.d("inputs", inputs.decodeToString())
            val clip = ClipData.newPlainText("password", inputs.decodeToString())

            clipboardManager.setPrimaryClip(clip)

            _state.value = GenerateProofState.GENERATE_PROOF
            val proof = zkp.generateZKP(
                "registerIdentityZkey.zkey",
                R.raw.register_identity,
                inputs,
                ZkpUtil::registerIdentity
            )

            Log.e("Proof", proof.toString())

            Log.e("Proof", ((endTime - startTime) / 1000).toString())
        }
    }
}