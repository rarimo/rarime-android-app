package com.rarilabs.rarime.api.registration

import android.util.Log
import com.google.gson.Gson
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
            val stateKeeperContract = rarimoContractManager.getStateKeeper()

            Log.i("pub_signals[0]", registrationProof.value!!.pub_signals[0].toByteArray().size.toString())

            val passportInfo = withContext(Dispatchers.IO) {
                stateKeeperContract.getPassportInfo(
                    Identity.bigIntToBytes(registrationProof.value!!.pub_signals[0])
                ).send()
            }

            _activeIdentity.value = passportInfo.component1().activeIdentity

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
        val dG15File = DG15File(eDocument.value!!.dg15!!.decodeHexString().inputStream())

        val pubKeyPem = dG15File.publicKey.publicKeyToPem()

        val callDataBuilder = CallDataBuilder()

        val callData = callDataBuilder.buildRevoceCalldata(
            activeIdentity.value,
            eDocument.value!!.aaSignature,
            pubKeyPem.toByteArray(),
        )

        _revocationChallenge.value = callData
    }

    suspend fun revoke() {
        val isUnsupported = Constants.NOT_ALLOWED_COUNTRIES.contains(eDocument.value!!.personDetails?.nationality)

        try {
            try {
                registrationAPIManager.register(revocationCallData.value!!)
            } catch (e: Exception) {
                Log.e("RevocationStepViewModel", "Error: $e")

                if (e.message?.contains("User already revoked") == false) {
                    throw e
                }
            }

            register(
                registrationProof.value!!,
                eDocument.value!!,
                masterCertProof.value!!,
                certificatePubKeySize.value,
                true
            )

            if (isUnsupported) {
                passportManager.updatePassportStatus(PassportStatus.NOT_ALLOWED)
            } else {
                passportManager.updatePassportStatus(PassportStatus.ALLOWED)
            }
        } catch (e: Exception) {
            // TODO: check if user already revoked
            if (isUnsupported) {
                passportManager.updatePassportStatus(PassportStatus.WAITLIST_NOT_ALLOWED)
            } else {
                passportManager.updatePassportStatus(PassportStatus.WAITLIST)
            }

            Log.e("RevocationStepViewModel", "Error: $e")
            throw e
        }
    }
}