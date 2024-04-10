package com.distributedLab.rarime.modules.passport.nfc

import android.nfc.tech.IsoDep
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.jmrtd.BACKey
import org.jmrtd.lds.icao.MRZInfo

class NfcViewModel : ViewModel() {
    private val _isScanning = MutableStateFlow(false)
    private lateinit var mrzInfo: MRZInfo
    private lateinit var bacKey: BACKey
    private lateinit var scanNfcUseCase: NfcUseCase

    lateinit var enableNFC: () -> Unit
    lateinit var disableNFC: () -> Unit
    val isScanning: MutableStateFlow<Boolean>
        get() = _isScanning

    fun setParams(isoDep: IsoDep) {
        val birthDate = mrzInfo.dateOfBirth
        val expirationDate = mrzInfo.dateOfExpiry
        val passportNumber = mrzInfo.documentNumber
        if (passportNumber == null || passportNumber.isEmpty() || expirationDate == null || expirationDate.isEmpty() || birthDate == null || birthDate.isEmpty()) {
            throw Exception("recheck mrz")
        }

        bacKey = BACKey(passportNumber, birthDate, expirationDate)
        scanNfcUseCase = NfcUseCase(isoDep, bacKey)
    }

    fun enableScanning() {
        enableNFC()
    }

    fun disableScanning() {
        disableNFC()
    }

    suspend fun startScanning() {
        scanNfcUseCase.scanPassport()
        Log.w("end", "End of scanning")
        setIsScanning(false)
    }


    fun setMRZ(mrzInfo: MRZInfo) {
        this.mrzInfo = mrzInfo

    }


    fun setIsScanning(scanning: Boolean) {
        _isScanning.value = scanning

    }
}