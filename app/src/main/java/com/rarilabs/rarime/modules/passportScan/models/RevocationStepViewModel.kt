package com.rarilabs.rarime.modules.passportScan.models

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.contracts.rarimo.PoseidonSMT.Proof
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.NfcManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.passportScan.nfc.NfcUseCase
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.decodeHexString
import com.rarilabs.rarime.util.publicKeyToPem
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.CallDataBuilder
import identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.jmrtd.BACKey
import org.jmrtd.lds.icao.DG15File
import org.jmrtd.lds.icao.MRZInfo
import javax.inject.Inject

@HiltViewModel
class RevocationStepViewModel @Inject constructor(
    private val nfcManager: NfcManager,
    private val identityManager: IdentityManager,
    private val rarimoContractManager: RarimoContractManager,
    private val registrationManager: RegistrationManager,
    private val passportManager: PassportManager,
) : ViewModel() {
    private lateinit var mrzInfo: MRZInfo
    private lateinit var bacKey: BACKey
    private lateinit var scanNfcUseCase: NfcUseCase
    lateinit var eDocument: EDocument
        private set
    lateinit var registrationProof: ZkProof
        private set
    lateinit var revocationChallenge: ByteArray
        private set
    lateinit var masterCertProof: Proof
        private set
    var certificateSize = mutableStateOf(0L)

    val state = nfcManager.state

    val resetState = nfcManager::resetState

    lateinit var activeIdentity: ByteArray

    var _revocationCallData = MutableStateFlow(null as ByteArray?)

    val revocationCallData: StateFlow<ByteArray?>
        get() = _revocationCallData.asStateFlow()

    suspend fun getRevocationChallenge(): ByteArray? {
        return withContext(Dispatchers.IO) {
            val stateKeeperContract = rarimoContractManager.getStateKeeper()

            Log.i("pub_signals[0]", registrationProof.pub_signals[0].toByteArray().size.toString())

            val passportInfo = withContext(Dispatchers.IO) {
                stateKeeperContract.getPassportInfo(Identity.bigIntToBytes(registrationProof.pub_signals[0]))
                    .send()
            }

            activeIdentity = passportInfo.component1().activeIdentity

            val ZERO_BYTES32 = ByteArray(32) { 0 }
            val isUserRevoking =
                !passportInfo.component1().activeIdentity.contentEquals(ZERO_BYTES32)

            if (isUserRevoking) {
                Log.i("Revoke", "Passport is registered, revoking")
            } else {
                Log.i("Revoke", "Passport is not registered")
            }

            if (isUserRevoking) {
                val revokationChallenge =
                    passportInfo.component1().activeIdentity.copyOfRange(24, 32)

                return@withContext revokationChallenge
            }

            return@withContext null
        }
    }

    private fun handleScan(tag: Tag) {
        val birthDate = mrzInfo.dateOfBirth
        val expirationDate = mrzInfo.dateOfExpiry
        val passportNumber = mrzInfo.documentNumber
        if (
            passportNumber == null ||
            passportNumber.isEmpty() ||
            expirationDate == null ||
            expirationDate.isEmpty() ||
            birthDate == null ||
            birthDate.isEmpty()
        ) {
            throw Exception("ReadNFCStepViewModel: Invalid Passport mrzInfo: $passportNumber $expirationDate $birthDate")
        }

        val isoDep = IsoDep.get(tag)
        isoDep.timeout = 5000

        val privateKeyBytes = identityManager.privateKeyBytes

        bacKey = BACKey(passportNumber, birthDate, expirationDate)
        scanNfcUseCase = NfcUseCase(isoDep, bacKey, privateKeyBytes!!)

        scanNfcUseCase.revokePassport(this.revocationChallenge, eDocument)

        val dG15File = DG15File(eDocument.dg15!!.decodeHexString().inputStream())

        val pubKeyPem = dG15File.publicKey.publicKeyToPem()

        val callDataBuilder = CallDataBuilder()

        val callData = callDataBuilder.buildRevoceCalldata(
            activeIdentity,
            eDocument.aaSignature,
            pubKeyPem.toByteArray(),
        )

        _revocationCallData.value = callData
    }

    private suspend fun register(
        zkProof: ZkProof,
        eDocument: EDocument,
        masterCertProof: Proof,
        certificateSize: Long,
        isUserRevoking: Boolean,
    ) {
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
            val response = registrationManager.register(callData)
            rarimoContractManager.checkIsTransactionSuccessful(response!!.data.attributes.tx_hash)
        }
    }

    suspend fun invokeRevocation() {
        val isUnsupported = Constants.NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.nationality)

        try {
            val response = registrationManager.register(revocationCallData.value!!)

            if (response == null) {
                // FIXME: rewrite register function to throw exception
                throw Exception("Passport is not registered")
            }

            register(
                registrationProof,
                eDocument,
                masterCertProof,
                certificateSize.value,
                isUnsupported
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

    fun onError(e: Exception) {
        Log.e("ReadNFCStepViewModel", "Error: $e")
    }

    suspend fun startScanning(
        mrzData: MRZInfo,
        eDocument: EDocument,
        registrationProof: ZkProof,
        masterCertProof: Proof,
        certificateSize: Long,
    ) {
        this.mrzInfo = mrzData
        this.eDocument = eDocument
        this.registrationProof = registrationProof
        this.masterCertProof = masterCertProof
        this.certificateSize.value = certificateSize

        val revChallenge = getRevocationChallenge()

        revChallenge?.let {
            this.revocationChallenge = it

            nfcManager.startScanning(::handleScan, onError = { onError(it) })
        } ?: run {
            throw IllegalStateException("Passport is not registered")
        }
    }
}