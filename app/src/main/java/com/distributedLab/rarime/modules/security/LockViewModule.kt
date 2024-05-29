package com.distributedLab.rarime.modules.security

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.modules.common.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LockViewModule @Inject constructor(
    private val securityManager: SecurityManager
) : ViewModel() {

    var isScreenLocked = securityManager.isScreenLocked
    var lockTimestamp = securityManager.lockTimestamp.longValue

    var passcode = securityManager.passcode

    var isPasscodeEnabled = securityManager.passcodeState.value == SecurityCheckState.ENABLED
    var isBiometricEnabled = securityManager.biometricsState.value == SecurityCheckState.ENABLED

    fun lockPasscode() {
        securityManager.lockPasscode()
    }

    fun unlockScreen() {
        securityManager.unlockScreen()
    }

}