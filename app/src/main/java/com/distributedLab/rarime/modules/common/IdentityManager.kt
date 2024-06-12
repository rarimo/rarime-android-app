package com.distributedLab.rarime.modules.common

import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.util.decodeHexString
import identity.Identity
import identity.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.web3j.crypto.Credentials
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalStdlibApi::class)
class IdentityManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) {
    private val _privateKey = MutableStateFlow(dataStoreManager.readPrivateKey())

    val registrationProof = MutableStateFlow(dataStoreManager.readRegistrationProof())

    val privateKey: StateFlow<String?>
        get() = _privateKey.asStateFlow()

    val privateKeyBytes: ByteArray?
        get() = _privateKey.value?.decodeHexString()

    private val _profiler = MutableStateFlow(Profile().newProfile(privateKeyBytes))

    val profiler: StateFlow<Profile>
        get() {
            if (_profiler == null) {
                // FIXME: setting in getter
                _profiler.value = Profile().newProfile(privateKeyBytes)
            }
            return _profiler.asStateFlow()
        }

    fun rarimoAddress(): String {

        return profiler.value.rarimoAddress
    }

    val evmAddress: String by lazy {
        _privateKey.value?.let {
            Credentials.create(privateKey.value)?.address
        } ?: ""
    }

    fun getPassportNullifier(): String {
        return registrationProof.value?.let {
            it.pub_signals.get(0)
        } ?: ""
    }

    fun getUserNullifier(): String {
        // TODO: rename, not for airdrop only
        return profiler.value.calculateAirdropEventNullifier(BaseConfig.POINTS_SVC_ID)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun newPrivateKey(): String {
        _privateKey.value = Identity.newBJJSecretKey().toHexString()
        return privateKey.value!!
    }

    fun savePrivateKey() {
        privateKey.value?.let { dataStoreManager.savePrivateKey(it) }
    }
}