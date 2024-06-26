package com.rarilabs.rarime.modules.passportScan.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.util.data.ZkProof
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanPassportScreenViewModel @Inject constructor(
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager,
): ViewModel() {
    fun rejectRevocation() {
        Log.i("ScanPassportScreenViewModel", "rejectRevocation")
        resetPassportState()
    }

    fun resetPassportState() {
        Log.i("ScanPassportScreenViewModel", "resetPassportState")
        passportManager.updatePassportStatus(PassportStatus.UNSCANNED)
        passportManager.deletePassport()
    }

    fun finishRevocation(eDocument: EDocument, registrationProof: ZkProof) {
        Log.i("ScanPassportScreenViewModel", "finishRevocation")
        savePassport(eDocument)
        saveRegistrationProof(registrationProof)
    }

    fun savePassport(eDocument: EDocument) {
        Log.i("ScanPassportScreenViewModel", "savePassport")
        passportManager.setPassport(eDocument)
    }

    fun saveRegistrationProof(registrationProof: ZkProof) {
        Log.i("ScanPassportScreenViewModel", "saveRegistrationProof")
        identityManager.setRegistrationProof(registrationProof)
    }
}