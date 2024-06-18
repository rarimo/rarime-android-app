package com.distributedLab.rarime.modules.passportScan.proof

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.registration.RegistrationManager
import com.distributedLab.rarime.contracts.rarimo.PoseidonSMT.Proof
import com.distributedLab.rarime.data.enums.PassportStatus
import com.distributedLab.rarime.data.ProofTx
import com.distributedLab.rarime.store.SecureSharedPrefsManager
import com.distributedLab.rarime.manager.RarimoContractManager
import com.distributedLab.rarime.manager.IdentityManager
import com.distributedLab.rarime.manager.PassportManager
import com.distributedLab.rarime.modules.passportScan.models.EDocument
import com.distributedLab.rarime.modules.passportScan.nfc.SODFileOwn
import com.distributedLab.rarime.util.Constants
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
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.jmrtd.lds.icao.DG15File
import java.io.IOException
import javax.inject.Inject


@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class ProofViewModel @Inject constructor(
    private val application: Application,
    private val dataStoreManager: SecureSharedPrefsManager,
    private val passportManager: PassportManager,
    private val registrationManager: RegistrationManager,
    private val rarimoContractManager: RarimoContractManager,
    identityManager: IdentityManager,
) : AndroidViewModel(application) {
    private val privateKeyBytes = identityManager.privateKeyBytes

    private val TAG = ProofViewModel::class.java.simpleName

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
            val registrationContract = rarimoContractManager.getRegistration()
            registrationContract.certificatesSmt().send()
        }

        val x509Util = X509Util()


        val proof = withContext(Dispatchers.IO) {
            val icao = readICAO(context = application.applicationContext)
            val slaveCertificateIndex =
                x509Util.getSlaveCertificateIndex(certificate.toByteArray(), icao)
            val indexHex = slaveCertificateIndex.toHexString()
            val poseidonSMT = rarimoContractManager.getPoseidonSMT(certificatesSMTAddress)
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
            registrationManager.register(callData)
        }

        if (response == null) {
            return
        }

        Log.i(TAG, "Passport certificate EVM Tx Hash " + response.data.attributes.tx_hash)


        val res = rarimoContractManager.checkIsTransactionSuccessful(response.data.attributes.tx_hash)
        if (!res) {
            Log.e(TAG, "Transaction failed" + response.data.attributes.tx_hash)
        }
    }

    private suspend fun generateRegisterIdentityProof(eDocument: EDocument): ZkProof {
        val inputs = buildRegistrationCircuits(eDocument)

        Log.i("INPUTS", inputs.decodeToString())
        val assetContext: Context =
            (application as Context).createPackageContext("com.distributedLab.rarime", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(application as Context, assetManager)

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
        try {
            _state.value = PassportProofState.READING_DATA

            passportManager.setPassport(eDocument)

            if (Constants.NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.issuerAuthority)) {
                passportManager.updatePassportStatus(PassportStatus.NOT_ALLOWED)
            }

            registerCertificate(eDocument)

            _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE
            val proof = generateRegisterIdentityProof(eDocument)

            dataStoreManager.saveRegistrationProof(proof)

            _state.value = PassportProofState.CREATING_CONFIDENTIAL_PROFILE
            register(proof, eDocument)


            _state.value = PassportProofState.FINALIZING

            passportManager.updatePassportStatus(PassportStatus.ALLOWED)
            
            delay(second * 1)
        } catch (e: Exception) {
            if (passportManager.passportStatus.value != PassportStatus.NOT_ALLOWED) {
                passportManager.updatePassportStatus(PassportStatus.WAITLIST)
            }
            throw e
        }


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
            masterCertProof.root,
            false
        )

        withContext(Dispatchers.IO) {
            val response = registrationManager.register(callData)
            rarimoContractManager.checkIsTransactionSuccessful(response!!.data.attributes.tx_hash)
        }
    }

    private suspend fun buildRegistrationCircuits(eDocument: EDocument): ByteArray {
        val sodStream = eDocument.sod!!.decodeHexString().inputStream()
        val sodFile = SODFileOwn(sodStream)
        val dg15 = eDocument.dg15!!.decodeHexString()
        val dG15File = DG15File(dg15.inputStream())

        val cert = sodFile.docSigningCertificate
        val certPem = SecurityUtil.convertToPEM(cert)

        val registrationContract = rarimoContractManager.getRegistration()

        val certificatesSMTAddress = withContext(Dispatchers.IO) {
            registrationContract.certificatesSmt().send()
        }

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

        val dg15PublicKey = dG15File.publicKey
        Log.i("DG15File", dg15PublicKey::class.java.name)


        val isEcdsaActiveAuthentication = (dg15PublicKey is BCECPublicKey)


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
            publicKeyPem.toByteArray(Charsets.UTF_8),
            signature,
            isEcdsaActiveAuthentication,
            proofJson.toByteArray(Charsets.UTF_8)
        )


        this.masterCertProof = proof!!

        return inputs
    }


    private fun readICAO(context: Context): ByteArray? {
        return try {
            val assetContext: Context = context.createPackageContext("com.distributedLab.rarime", 0)
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


}
