package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.modules.passport.models.EDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PassportViewModel @Inject constructor(
    private val secureSharedPrefsManager: SecureSharedPrefsManager
) : ViewModel() {

    var passport = mutableStateOf<EDocument?>(
        secureSharedPrefsManager.readEDocument()
    )
        private set
    var passportCardLook = mutableStateOf(secureSharedPrefsManager.readPassportCardLook())
        private set
    var isIncognitoMode = mutableStateOf(secureSharedPrefsManager.readIsPassportIncognitoMode())
        private set

    fun updatePassportCardLook(look: PassportCardLook) {
        passportCardLook.value = look
        secureSharedPrefsManager.savePassportCardLook(look)
    }

    fun updateIsIncognitoMode(isIncognitoMode: Boolean) {
        this.isIncognitoMode.value = isIncognitoMode
        secureSharedPrefsManager.saveIsPassportIncognitoMode(isIncognitoMode)
    }

    fun setPassport(passport: EDocument) {
        secureSharedPrefsManager.saveEDocument(passport)
        this.passport.value = passport
    }
}