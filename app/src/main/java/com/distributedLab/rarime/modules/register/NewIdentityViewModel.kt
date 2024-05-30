package com.distributedLab.rarime.modules.register

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.IdentityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewIdentityViewModel @Inject constructor(
    val identityManager: IdentityManager
) : ViewModel() {
    fun getPrivateKey(): String {
        return identityManager.newPrivateKey()
    }
}