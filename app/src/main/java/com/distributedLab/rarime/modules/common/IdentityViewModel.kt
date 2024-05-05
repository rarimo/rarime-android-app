package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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

    var privateKey: MutableState<String> = run {
        newPrivateKey()
        mutableStateOf(
            bytePrivateKey!!.toHexString()
        )
    }
        private set


    @OptIn(ExperimentalStdlibApi::class)
    fun newPrivateKey() {
        bytePrivateKey = Identity.newBJJSecretKey()
    }

    fun savePrivateKey() {
        dataStoreManager.savePrivateKey(bytePrivateKey!!.toHexString())
    }

    val did: String
        get() {
            return Profile().newProfile(bytePrivateKey).rarimoAddress
        }
}