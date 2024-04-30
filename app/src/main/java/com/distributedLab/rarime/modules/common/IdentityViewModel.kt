package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IdentityViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) : ViewModel() {
    // TODO: Get the private key from secure storage
    var privateKey =
        mutableStateOf("d4f1dc5332e5f0263746a31d3563e42ad8bef24a8989d8b0a5ad71f8d5de28a6")
        private set

    val did: String
        get() {
            // TODO: Get the DID from the private key
            return "did:iden3:readonly:tQR6mhrf6jJyYxmc9YZZS6xiyxjG4b4yQh92diTme"
        }
}