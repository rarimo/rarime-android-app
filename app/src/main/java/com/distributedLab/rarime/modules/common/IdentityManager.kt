package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.util.decodeHexString
import identity.Identity
import identity.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalStdlibApi::class)
class IdentityManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) {


    private var profiler: Profile? = null
    fun getProfiler(): Profile {
        if (profiler == null) {

            profiler = Profile().newProfile(privateKey!!.decodeHexString())
            return profiler!!
        }

        return profiler!!
    }

    var privateKey by mutableStateOf(dataStoreManager.readPrivateKey())
        private set


    @OptIn(ExperimentalStdlibApi::class)
    fun newPrivateKey() : String {
        privateKey = Identity.newBJJSecretKey().toHexString()
        return privateKey!!
    }

    fun savePrivateKey() {
        dataStoreManager.savePrivateKey(privateKey!!)
    }

    val did: String
        get() = getProfiler().rarimoAddress
}