package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.domain.manager.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    var passcodeState = mutableStateOf(SecurityCheckState.UNSET)
        private set
    var biometricsState = mutableStateOf(SecurityCheckState.UNSET)
        private set

    // TODO: Get passcode from secure storage
    var passcode = mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            passcodeState.value = dataStoreManager.readPasscodeState().first()
            biometricsState.value = dataStoreManager.readBiometricsState().first()
        }
    }

    fun updatePasscodeState(state: SecurityCheckState) {
        passcodeState.value = state
        viewModelScope.launch {
            dataStoreManager.savePasscodeState(state)
        }
    }

    fun setPasscode(newPasscode: String) {
        passcode.value = newPasscode
    }

    fun updateBiometricsState(state: SecurityCheckState) {
        biometricsState.value = state
        viewModelScope.launch {
            dataStoreManager.saveBiometricsState(state)
        }
    }
}