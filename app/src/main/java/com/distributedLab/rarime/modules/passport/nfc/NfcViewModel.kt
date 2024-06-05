package com.distributedLab.rarime.modules.passport.nfc

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.IdentityManager
import com.distributedLab.rarime.modules.passport.models.EDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jmrtd.BACKey
import org.jmrtd.lds.icao.MRZInfo
import javax.inject.Inject

enum class ScanNFCPassportState {
    NOT_SCANNING, SCANNING, SCANNED, ERROR
}

@HiltViewModel
class NfcViewModel @Inject constructor(
    private val identityManager: IdentityManager,
) : ViewModel() {
    private val TAG = "NfcViewModel"
    private lateinit var mrzInfo: MRZInfo
    private lateinit var bacKey: BACKey
    private lateinit var scanNfcUseCase: NfcUseCase

    lateinit var eDocument: EDocument
        private set

    private var _state = MutableStateFlow(ScanNFCPassportState.NOT_SCANNING)

    private var challenge: ByteArray? = null

    val state: StateFlow<ScanNFCPassportState>
        get() = _state.asStateFlow()

    lateinit var enableNFC: () -> Unit
    lateinit var disableNFC: () -> Unit

    fun setParams(tag: Tag) {
        val birthDate = mrzInfo.dateOfBirth
        val expirationDate = mrzInfo.dateOfExpiry
        val passportNumber = mrzInfo.documentNumber
        if (passportNumber == null || passportNumber.isEmpty() || expirationDate == null || expirationDate.isEmpty() || birthDate == null || birthDate.isEmpty()) {
            _state.value = ScanNFCPassportState.ERROR
            Log.e(TAG, "Invalid Passport mrzInfo: $passportNumber $expirationDate $birthDate")
            return
        }

        val isoDep = IsoDep.get(tag)
        isoDep.timeout = 5000
        val privateKeyBytes = identityManager.privateKeyBytes
        bacKey = BACKey(passportNumber, birthDate, expirationDate)
        scanNfcUseCase = NfcUseCase(isoDep, bacKey, privateKeyBytes!!)
    }

    fun resetState() {
        _state.value = ScanNFCPassportState.NOT_SCANNING
    }


    fun startRevokeNfc(challenge: ByteArray) {
        enableNFC.invoke()
        this.challenge = challenge
    }

    suspend fun startScanning() {
        try {
            _state.value = ScanNFCPassportState.SCANNING
            if (challenge == null) {
                eDocument = scanNfcUseCase.scanPassport()
            }else {
                scanNfcUseCase.revokePassport(this.challenge!!, eDocument)
            }
            _state.value = ScanNFCPassportState.SCANNED
        } catch (e: Exception) {
            _state.value = ScanNFCPassportState.ERROR
            Log.e("Error", "thm wrong with nfc", e)
        }
    }

    fun setMRZ(mrzInfo: MRZInfo) {
        this.mrzInfo = mrzInfo
    }
}