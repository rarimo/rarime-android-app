package com.rarilabs.rarime.modules.register

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.IdentityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewIdentityViewModel @Inject constructor(
    val identityManager: IdentityManager
) : ViewModel() {
    val savedPrivateKey = identityManager.privateKey

    fun genPrivateKey(): String {
        return identityManager.genPrivateKey()
    }
}