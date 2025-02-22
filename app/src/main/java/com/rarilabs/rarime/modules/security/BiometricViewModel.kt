package com.rarilabs.rarime.modules.security

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.manager.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel @Inject constructor(
    val securityManager: SecurityManager
) : ViewModel() {

    fun enableBiometric() {
        securityManager.updateBiometricsState(SecurityCheckState.ENABLED)
    }

    fun skipBiometric() {
        securityManager.updateBiometricsState(SecurityCheckState.DISABLED)
    }
}