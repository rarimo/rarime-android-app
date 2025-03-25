package com.rarilabs.rarime.modules.you

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.ProofGenerationManager
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ZkIdentityScreenViewModel @Inject constructor(
    private val app: Application,
    private val passportManager: PassportManager,
    private val registrationManager: RegistrationManager,
    private val sharedPrefsManager: SecureSharedPrefsManager,
    private val proofGenerationManager: ProofGenerationManager,
) : AndroidViewModel(app) {

    val isShowPassport = passportManager.isShowPassport
    var passport = passportManager.passport
    var passportCardLook = passportManager.passportCardLook
    var passportIdentifiers = passportManager.passportIdentifiers
    var isIncognito = passportManager.isIncognitoMode

    val passportStatus = passportManager.passportStatus

    val performRegistration = proofGenerationManager::performRegistration

    fun onPassportCardLookChange(passportCardLook: PassportCardLook) {
        passportManager.updatePassportCardLook(passportCardLook)
    }

    fun onIncognitoChange(isIncognito: Boolean) {
        passportManager.updateIsIncognitoMode(isIncognito)
    }

    fun setTempEDocument(eDocument: EDocument) {
        registrationManager.setEDocument(eDocument)
    }

    fun onPassportIdentifiersChange(passportIdentifiers: List<PassportIdentifier>) {
        passportManager.updatePassportIdentifiers(passportIdentifiers)
    }

    fun getIsAlreadyReserved(): Boolean {
        return sharedPrefsManager.getIsAlreadyReserved()
    }
}