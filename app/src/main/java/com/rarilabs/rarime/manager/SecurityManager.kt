package com.rarilabs.rarime.manager

import androidx.compose.runtime.mutableStateOf
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) {
    var passcodeState = mutableStateOf(dataStoreManager.readPasscodeState())
        private set
    var biometricsState = mutableStateOf(dataStoreManager.readBiometricsState())
        private set

    var passcode = mutableStateOf(dataStoreManager.readPasscode())
        private set

    var _lockTimestamp = MutableStateFlow(dataStoreManager.readLockTimestamp())
        private set

    val lockTimestamp: StateFlow<Long>
        get() = _lockTimestamp.asStateFlow()

    var isScreenLocked =
        mutableStateOf(
            passcodeState.value == SecurityCheckState.ENABLED
                    || biometricsState.value == SecurityCheckState.ENABLED
        )
        private set


    fun updatePasscodeState(state: SecurityCheckState) {
        passcodeState.value = state
        dataStoreManager.savePasscodeState(state)

        if (state == SecurityCheckState.DISABLED) {
            updateBiometricsState(state)
        }
    }

    fun setPasscode(newPasscode: String) {
        passcode.value = newPasscode
        dataStoreManager.savePasscode(newPasscode)
    }

    fun updateBiometricsState(state: SecurityCheckState) {
        biometricsState.value = state
        dataStoreManager.saveBiometricsState(state)
    }

    fun lockPasscode() {
        val timestamp =
            System.currentTimeMillis() + Constants.PASSCODE_LOCK_PERIOD.inWholeMilliseconds
        _lockTimestamp.value = timestamp
        dataStoreManager.saveLockTimestamp(timestamp)
    }

    fun unlockScreen() {
        isScreenLocked.value = false
    }
}