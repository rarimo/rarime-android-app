package com.distributedLab.rarime.manager

import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.store.SecureSharedPrefsManager
import com.distributedLab.rarime.util.decodeHexString
import identity.Identity
import identity.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.web3j.crypto.Credentials
import java.math.BigInteger
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

    fun getUserAirDropNullifier(): String {
        return profiler.value.calculateEventNullifierInt(BaseConfig.AIRDROP_SVC_ID)
    }

    fun getUserAirDropNullifierHex(): String {
        return "0x" + BigInteger(this.getUserAirDropNullifier())
            .toByteArray()
            .toHexString()
    }

    fun getUserPointsNullifier(): String {
        return profiler.value.calculateEventNullifierInt(BaseConfig.POINTS_SVC_ID)
    }

    fun getUserPointsNullifierHex(): String {
        return "0x" + BigInteger(this.getUserPointsNullifier())
            .toByteArray()
            .toHexString()
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