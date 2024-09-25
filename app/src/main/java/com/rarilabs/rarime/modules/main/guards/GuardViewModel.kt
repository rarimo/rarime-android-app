package com.rarilabs.rarime.modules.main.guards

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.auth.AuthManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GuardViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val securityManager: SecurityManager,
    private val identityManager: IdentityManager,
): ViewModel() {
    val privateKey = identityManager.privateKey

    val isScreenLocked = securityManager.isScreenLocked
    val biometricsState = securityManager.biometricsState
    var passcodeState = securityManager.passcodeState
}