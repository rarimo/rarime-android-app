package com.rarilabs.rarime.modules.passportScan.models

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WaitlistPassportScreenViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
): ViewModel() {
    fun joinWaitlist() {
        dataStoreManager.saveIsInWaitlist(true)
    }
}