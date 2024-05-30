package com.distributedLab.rarime.modules.profile

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.modules.common.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AuthMethodViewModel @Inject constructor(
    private val securityManager: SecurityManager
) : ViewModel() {

    val passcode = securityManager.passcode
    val biometricsState = securityManager.biometricsState
    val passcodeState = securityManager.passcodeState

    fun updateBiometricsState(state: SecurityCheckState) =
        securityManager.updateBiometricsState(state)

    fun updatePasscodeState(state: SecurityCheckState) = securityManager.updatePasscodeState(state)

    fun setPasscode(passcode: String) = securityManager.setPasscode(passcode)
}