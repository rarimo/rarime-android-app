package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) : ViewModel() {
    var passcodeState = mutableStateOf(dataStoreManager.readPasscodeState())
        private set
    var biometricsState = mutableStateOf(dataStoreManager.readBiometricsState())
        private set

    // TODO: Get passcode from secure storage
    var passcode = mutableStateOf("")
        private set

    fun updatePasscodeState(state: SecurityCheckState) {
        passcodeState.value = state
        dataStoreManager.savePasscodeState(state)
    }

    fun setPasscode(newPasscode: String) {
        passcode.value = newPasscode
    }

    fun updateBiometricsState(state: SecurityCheckState) {
        biometricsState.value = state
        dataStoreManager.saveBiometricsState(state)
    }
}