package com.rarilabs.rarime.modules.passportScan.models

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.NfcManager
import com.rarilabs.rarime.manager.RarimoContractManager
import com.rarilabs.rarime.modules.passportScan.nfc.NfcUseCase
import com.rarilabs.rarime.util.data.ZkProof
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jmrtd.BACKey
import org.jmrtd.lds.icao.MRZInfo
import javax.inject.Inject

@HiltViewModel
class RevocationStepViewModel @Inject constructor(
    private val nfcManager: NfcManager,
    private val identityManager: IdentityManager,
    private val rarimoContractManager: RarimoContractManager,
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

    val state = nfcManager.state

    val resetState = nfcManager::resetState

    suspend fun getRevocationChallenge(): ByteArray? {
        return withContext(Dispatchers.IO) {
            val registerContract = rarimoContractManager.getRegistration()

            Log.i("pub_signals[0]", registrationProof.pub_signals[0].toByteArray().size.toString())

            val passportInfo = withContext(Dispatchers.IO) {
                registerContract.getPassportInfo(Identity.bigIntToBytes(registrationProof.pub_signals[0]))
                    .send()
            }

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

        scanNfcUseCase.revokePassport(this.revocationChallenge, eDocument!!)
    }

    fun onError(e: Exception) {
        Log.e("ReadNFCStepViewModel", "Error: $e")
    }

    suspend fun startScanning(
        mrzData: MRZInfo,
        eDocument: EDocument,
        registrationProof: ZkProof,
    ) {
        this.mrzInfo = mrzData
        this.eDocument = eDocument
        this.registrationProof = registrationProof

        val revChallenge = getRevocationChallenge()

        revChallenge?.let {
            this.revocationChallenge = it
        }

        nfcManager.startScanning(::handleScan, onError = { onError(it) })
    }
}