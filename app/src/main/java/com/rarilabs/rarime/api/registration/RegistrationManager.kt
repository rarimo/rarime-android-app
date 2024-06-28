package com.rarilabs.rarime.api.registration

import android.util.Log
import com.google.gson.Gson
import com.rarilabs.rarime.api.auth.AuthManager
import com.rarilabs.rarime.contracts.rarimo.PoseidonSMT.Proof
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.publicKeyToPem
import identity.CallDataBuilder
import identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.jmrtd.lds.icao.DG15File
import javax.inject.Inject

class RegistrationManager @Inject constructor(
    private val registrationAPIManager: RegistrationAPIManager,
    private val rarimoContractManager: RarimoContractManager,
    private val passportManager: PassportManager,
    private val authManager: AuthManager,
) {
    private var _masterCertProof = MutableStateFlow<Proof?>(null)
        private set
    val masterCertProof: StateFlow<Proof?>
        get() = _masterCertProof.asStateFlow()

    private var _certificatePubKeySize = MutableStateFlow(0L)
        private set
    val certificatePubKeySize: StateFlow<Long>
        get() = _certificatePubKeySize.asStateFlow()

    private var _activeIdentity = MutableStateFlow(ByteArray(0))
        private set
    val activeIdentity: StateFlow<ByteArray>
        get() = _activeIdentity.asStateFlow()

    private var _registrationProof = MutableStateFlow<ZkProof?>(null)
        private set
    val registrationProof: StateFlow<ZkProof?>
        get() = _registrationProof.asStateFlow()

    var _revocationChallenge = MutableStateFlow<ByteArray>(ByteArray(0))
        private set
    val revocationChallenge: StateFlow<ByteArray?>
        get() = _revocationChallenge.asStateFlow()

    var _revocationCallData = MutableStateFlow(null as ByteArray?)
    val revocationCallData: StateFlow<ByteArray?>
        get() = _revocationCallData.asStateFlow()

    private var _eDocument = MutableStateFlow(null as EDocument?)
    val eDocument: StateFlow<EDocument?>
        get() = _eDocument.asStateFlow()

    private var _revEDocument = MutableStateFlow(null as EDocument?)
    val revEDocument: StateFlow<EDocument?>
        get() = _revEDocument.asStateFlow()

    fun setRevEDocument(eDocument: EDocument) {
        _revEDocument.value = eDocument
    }
    fun setRegistrationProof(proof: ZkProof) {
        _registrationProof.value = proof
    }
    fun setEDocument(eDocument: EDocument) {
        _eDocument.value = eDocument
    }
    fun setMasterCertProof(proof: Proof) {
        _masterCertProof.value = proof
    }
    fun setCertSize(size: Long) {
        _certificatePubKeySize.value = size
    }

    suspend fun relayerRegister(callData: ByteArray) = registrationAPIManager.register(callData)

    /**
     * if isUserRevoking is true, then this method is re-issuance for revoked passport
     * else it is registration for new passport
     */
    suspend fun register(
        zkProof: ZkProof,
        eDocument: EDocument,
        masterCertProof: Proof,
        certificateSize: Long,
        isUserRevoking: Boolean
    ) {
        if (authManager.isAccessTokenExpired()) {
            authManager.refresh()
        }

        _eDocument.value = eDocument

        val jsonProof = Gson().toJson(zkProof)

        val dG15File = DG15File(eDocument.dg15!!.decodeHexString().inputStream())

        val pubKeyPem = dG15File.publicKey.publicKeyToPem()

        val callDataBuilder = CallDataBuilder()
        val callData = callDataBuilder.buildRegisterCalldata(
            jsonProof.toByteArray(),
            eDocument.aaSignature,
            pubKeyPem.toByteArray(),
            masterCertProof.root,
            certificateSize,
            isUserRevoking
        )

        withContext(Dispatchers.IO) {
            val response = registrationAPIManager.register(callData)

            response.data.attributes.tx_hash.let {
                rarimoContractManager.checkIsTransactionSuccessful(it)
            }
        }
    }

    suspend fun getRevocationChallenge(): ByteArray? {
        return withContext(Dispatchers.IO) {
            if (authManager.isAccessTokenExpired()) {
                authManager.refresh()
            }

            val stateKeeperContract = rarimoContractManager.getStateKeeper()

            val passportInfoKey: String =
                if (eDocument.value!!.dg15?.isEmpty() ?: false) {
                    registrationProof.value!!.pub_signals[1]
                } else {
                    registrationProof.value!!.pub_signals[0]
                }

            var passportInfoKeyBytes = Identity.bigIntToBytes(passportInfoKey)

            if (passportInfoKeyBytes.size != 32) {
                passportInfoKeyBytes = passportInfoKeyBytes.copyOf(32)
            }

            val passportInfo = withContext(Dispatchers.IO) {
                stateKeeperContract.getPassportInfo(passportInfoKeyBytes).send()
            }

            _activeIdentity.value = passportInfo.component1().activeIdentity

            Log.i("ActiveIdentity", _activeIdentity.value.toString())

            val ZERO_BYTES32 = ByteArray(32) { 0 }
            val isUserRevoking =
                !passportInfo.component1().activeIdentity.contentEquals(ZERO_BYTES32)

            if (isUserRevoking) {
                Log.i("Revoke", "Passport is registered, revoking")
            } else {
                Log.i("Revoke", "Passport is not registered")
            }

            if (isUserRevoking) {
                _revocationChallenge.value =
                    passportInfo.component1().activeIdentity.copyOfRange(24, 32)

                return@withContext _revocationChallenge.value
            }

            return@withContext null
        }
    }

    fun buildRevocationCallData() {
        val dG15File = DG15File(revEDocument.value!!.dg15!!.decodeHexString().inputStream())

        val pubKeyPem = dG15File.publicKey.publicKeyToPem()

        val callDataBuilder = CallDataBuilder()

        Log.i("buildRevocationCallData", revEDocument.value!!.aaSignature.toString())

        Log.i("buildRevocationCallData", pubKeyPem)

        val callData = callDataBuilder.buildRevoceCalldata(
            activeIdentity.value,
            revEDocument.value!!.aaSignature,
            pubKeyPem.toByteArray(),
        )

        Log.i("callData", callData.toString())
        _revocationCallData.value = callData
    }

    suspend fun revoke() {
        try {
            if (authManager.isAccessTokenExpired()) {
                authManager.refresh()
            }

            try {
                val txResponse = registrationAPIManager.register(revocationCallData.value!!)

                txResponse.data.attributes.tx_hash.let {
                    rarimoContractManager.checkIsTransactionSuccessful(it)
                }
            } catch (e: Exception) {
                Log.e("RegistrationManager:revoke:", "Error: $e")

                if (!(e is UserAlreadyRevoked)) {
                    throw e
                }
            }

            Log.i("registrationProof.value!!", registrationProof.value!!.toString())
            Log.i("eDocument.value!!", eDocument.value!!.toString())
            Log.i("masterCertProof.value!!", masterCertProof.value!!.toString())
            Log.i("certificatePubKeySize.value", certificatePubKeySize.value.toString())

            register(
                registrationProof.value!!,
                eDocument.value!!,
                masterCertProof.value!!,
                certificatePubKeySize.value,
                true
            )
        } catch (e: Exception) {
            Log.e("RevocationStepViewModel", "Error: $e")
            throw e
        }
    }
}