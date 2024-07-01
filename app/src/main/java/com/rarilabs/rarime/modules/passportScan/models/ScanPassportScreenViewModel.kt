package com.rarilabs.rarime.modules.passportScan.models

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.data.ZkProof
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanPassportScreenViewModel @Inject constructor(
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager,
    private val registrationManager: RegistrationManager,
): ViewModel() {
    val eDocument = registrationManager.eDocument

    fun rejectRevocation() {
        ErrorHandler.logDebug("ScanPassportScreenViewModel", "rejectRevocation")
        resetPassportState()
    }

    fun resetPassportState() {
        ErrorHandler.logDebug("ScanPassportScreenViewModel", "resetPassportState")
        passportManager.deletePassport()
    }

    fun finishRevocation() {
        ErrorHandler.logDebug("ScanPassportScreenViewModel", "finishRevocation")
        savePassport(registrationManager.eDocument.value!!)
        saveRegistrationProof(registrationManager.registrationProof.value!!)
    }

    fun savePassport(eDocument: EDocument) {
        ErrorHandler.logDebug("ScanPassportScreenViewModel", "savePassport")
        registrationManager.setEDocument(eDocument)

        // for interrupt cases
        passportManager.setPassport(eDocument)
    }

    fun saveRegistrationProof(registrationProof: ZkProof) {
        ErrorHandler.logDebug("ScanPassportScreenViewModel", "saveRegistrationProof")
        registrationManager.setRegistrationProof(registrationProof)

        // for interrupt cases
        identityManager.setRegistrationProof(registrationProof)
    }
}