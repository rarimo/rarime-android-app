package com.distributedLab.rarime.modules.security

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.manager.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupPasscodeViewModel @Inject constructor(
    val securityManager: SecurityManager
) : ViewModel() {
    fun onPasscodeChange(passcode: String) {
        securityManager.setPasscode(passcode)
    }

    fun updatePasscodeState(securityCheckState: SecurityCheckState) {
        securityManager.updatePasscodeState(securityCheckState)
    }
}