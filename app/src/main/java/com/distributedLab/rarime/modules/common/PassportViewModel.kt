package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.domain.manager.DataStoreManager
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.models.PersonDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PassportViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    // TODO: Get passport from secure storage
    var passport = mutableStateOf<EDocument?>(
        EDocument(
            personDetails = PersonDetails(
                name = "John",
                surname = "Doe",
                birthDate = "01.01.1990",
                nationality = "USA",
                serialNumber = "123456789",
                faceImageInfo = null
            )
        )
    )
        private set
    var passportCardLook = mutableStateOf(PassportCardLook.BLACK)
        private set
    var isIncognitoMode = mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            passportCardLook.value = dataStoreManager.readPassportCardLook().first()
            isIncognitoMode.value = dataStoreManager.readIsPassportIncognitoMode().first()
        }
    }

    fun updatePassportCardLook(look: PassportCardLook) {
        passportCardLook.value = look
        viewModelScope.launch {
            dataStoreManager.savePassportCardLook(look)
        }
    }

    fun updateIsIncognitoMode(isIncognitoMode: Boolean) {
        this.isIncognitoMode.value = isIncognitoMode
        viewModelScope.launch {
            dataStoreManager.saveIsPassportIncognitoMode(isIncognitoMode)
        }
    }

    fun setPassport(passport: EDocument?) {
        this.passport.value = passport
    }
}