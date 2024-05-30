package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.util.Constants
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
    var lockTimestamp = mutableLongStateOf(dataStoreManager.readLockTimestamp())
        private set
    var isScreenLocked =
        mutableStateOf(
            passcodeState.value == SecurityCheckState.ENABLED
                    || biometricsState.value == SecurityCheckState.ENABLED
        )
        private set


    fun updatePasscodeState(state: SecurityCheckState) {
        passcodeState.value = state
        dataStoreManager.savePasscodeState(state)
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
        lockTimestamp.longValue = timestamp
        dataStoreManager.saveLockTimestamp(timestamp)
    }

    fun unlockScreen() {
        isScreenLocked.value = false
    }
}