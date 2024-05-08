package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.PassportIdentifier
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
        EDocument(
            personDetails = PersonDetails(
                name = "John",
                surname = "Doe",
                birthDate = "01.01.1990",
                expiryDate = "01.01.2025",
                nationality = "USA",
                serialNumber = "123456789",
                faceImageInfo = null
            )
        )
    )
        private set
    var passportCardLook = mutableStateOf(dataStoreManager.readPassportCardLook())
        private set
    var isIncognitoMode = mutableStateOf(dataStoreManager.readIsPassportIncognitoMode())
        private set
    var passportIdentifiers = mutableStateOf(dataStoreManager.readPassportIdentifiers())
        private set

    fun updatePassportCardLook(look: PassportCardLook) {
        passportCardLook.value = look
        dataStoreManager.savePassportCardLook(look)
    }

    fun updateIsIncognitoMode(isIncognitoMode: Boolean) {
        this.isIncognitoMode.value = isIncognitoMode
        dataStoreManager.saveIsPassportIncognitoMode(isIncognitoMode)
    }

    fun updatePassportIdentifiers(identifiers: List<PassportIdentifier>) {
        passportIdentifiers.value = identifiers
        dataStoreManager.savePassportIdentifiers(identifiers)
    }

    fun setPassport(passport: EDocument?) {
        this.passport.value = passport
    }
}