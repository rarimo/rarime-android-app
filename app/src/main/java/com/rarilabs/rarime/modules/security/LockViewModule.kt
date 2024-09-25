package com.rarilabs.rarime.modules.security

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.manager.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LockViewModule @Inject constructor(
    private val securityManager: SecurityManager
) : ViewModel() {
    val lockTimestamp = securityManager.lockTimestamp

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