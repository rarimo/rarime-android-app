package com.rarilabs.rarime.modules.passportScan.models

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.NfcManager
import com.rarilabs.rarime.modules.passportScan.nfc.NfcUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okio.IOException
import org.jmrtd.BACKey
import org.jmrtd.lds.icao.MRZInfo
import javax.inject.Inject

@HiltViewModel
class ReadEDocStepViewModel @Inject constructor(
    private val nfcManager: NfcManager,
    private val identityManager: IdentityManager
) : ViewModel() {
    private lateinit var mrzInfo: MRZInfo
    private lateinit var bacKey: BACKey
    private lateinit var scanNfcUseCase: NfcUseCase
    lateinit var eDocument: EDocument
        private set

    val state = nfcManager.state

    val resetState = nfcManager::resetState

    var _errorMessageId = MutableStateFlow(R.string.nfc_error_unknown)
    val errorMessageId: StateFlow<Int>
        get() = _errorMessageId.asStateFlow()

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

        try {

        } catch (e: Exception) {
            eDocument = scanNfcUseCase.scanPassport()
        }
    }

    fun onError(e: Exception) {
        Log.e("ReadNFCStepViewModel", "Error: $e")
        e.printStackTrace()

        if (e is IOException) {
            _errorMessageId.value = R.string.nfc_error_interrupt
        }
    }

    fun startScanning(mrzInfo: MRZInfo) {
        this.mrzInfo = mrzInfo

        nfcManager.startScanning(::handleScan, onError = { onError(it) })
    }
}