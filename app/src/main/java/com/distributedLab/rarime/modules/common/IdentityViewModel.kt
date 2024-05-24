package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import identity.Identity
import identity.Profile
import javax.inject.Inject

@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class IdentityViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) : ViewModel() {

    private var bytePrivateKey: ByteArray? = null

    var privateKey by mutableStateOf("")
        private set

    init {
        newPrivateKey()
        privateKey = bytePrivateKey!!.toHexString()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun newPrivateKey() {
        bytePrivateKey = Identity.newBJJSecretKey()
        privateKey = bytePrivateKey!!.toHexString()
    }

    fun savePrivateKey() {
        dataStoreManager.savePrivateKey(privateKey)
    }

    val did: String
        get() = Profile().newProfile(bytePrivateKey).rarimoAddress
}