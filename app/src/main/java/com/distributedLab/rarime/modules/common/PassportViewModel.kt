package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.models.PersonDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PassportViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) : ViewModel() {
    // TODO: Get passport from secure storage
    var passport = mutableStateOf<EDocument?>(
        null
    )
        private set
    var passportCardLook = mutableStateOf(dataStoreManager.readPassportCardLook())
        private set
    var isIncognitoMode = mutableStateOf(dataStoreManager.readIsPassportIncognitoMode())
        private set

    fun updatePassportCardLook(look: PassportCardLook) {
        passportCardLook.value = look
        dataStoreManager.savePassportCardLook(look)
    }

    fun updateIsIncognitoMode(isIncognitoMode: Boolean) {
        this.isIncognitoMode.value = isIncognitoMode
        dataStoreManager.saveIsPassportIncognitoMode(isIncognitoMode)
    }

    fun setPassport(passport: EDocument?) {
        this.passport.value = passport
    }
}