package com.rarilabs.rarime.modules.passportScan.models

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.NfcManager
import com.rarilabs.rarime.modules.passportScan.nfc.NfcUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.jmrtd.BACKey
import org.jmrtd.lds.icao.MRZInfo
import javax.inject.Inject

@HiltViewModel
class RevocationStepViewModel @Inject constructor(
    private val nfcManager: NfcManager,
    private val identityManager: IdentityManager,
    private val registrationManager: RegistrationManager,
) : ViewModel() {
    val revocationCallData = registrationManager.revocationCallData

    private lateinit var mrzInfo: MRZInfo
    private lateinit var bacKey: BACKey
    private lateinit var scanNfcUseCase: NfcUseCase

    val state = nfcManager.state
    val resetState = nfcManager::resetState

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

        /* sign with passport */
        val updatedEDoc = scanNfcUseCase.signRevocationWithPassport(
            registrationManager.revocationChallenge.value!!,
            // TODO: is it modify eDocument through reference?
            registrationManager.eDocument.value!!
        )

        registrationManager.setEDocument(updatedEDoc)

        registrationManager.buildRevocationCallData()
    }

    suspend fun invokeRevocation() {
        try {
            registrationManager.revoke()
        } catch (e: Exception) {
            Log.e("RevocationStepViewModel", "Error: $e")

            throw e
        }
    }

    fun onError(e: Exception) {
        Log.e("ReadNFCStepViewModel", "Error: $e")
    }

    suspend fun startScanning(mrzData: MRZInfo) {
        this.mrzInfo = mrzData

        val revChallenge = registrationManager.getRevocationChallenge()

        revChallenge?.let {
            nfcManager.startScanning(::handleScan, onError = { onError(it) })
        } ?: run {
            throw Exception("Passport is not registered")
        }
    }
}