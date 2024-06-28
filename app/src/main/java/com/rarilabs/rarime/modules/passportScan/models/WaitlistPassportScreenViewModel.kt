package com.rarilabs.rarime.modules.passportScan.models

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WaitlistPassportScreenViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    private val identityManager: IdentityManager
) : ViewModel() {
    fun joinWaitlist() {
        dataStoreManager.saveIsInWaitlist(true)
    }

    fun getNullifierHex(): String {
        return identityManager.getUserPointsNullifierHex()
    }
}