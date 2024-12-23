package com.rarilabs.rarime.manager

import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.registration.models.LightRegistrationData
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.data.ZkProof
import com.rarilabs.rarime.util.decodeHexString
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
    val privateKey: StateFlow<String?>
        get() = _privateKey.asStateFlow()

    var _registrationProof = MutableStateFlow(dataStoreManager.readRegistrationProof())
        private set
    val registrationProof: StateFlow<ZkProof?>
        get() = _registrationProof.asStateFlow()

    val privateKeyBytes: ByteArray?
        get() = _privateKey.value?.decodeHexString()

    private var _isLogsDeleted = MutableStateFlow(dataStoreManager.readIsLogsDeleted())
    val isLogsDeleted: StateFlow<Boolean>
        get() = _isLogsDeleted.asStateFlow()

    fun updateIsLogsDeleted(isLogsDeleted: Boolean) {
        _isLogsDeleted.value = isLogsDeleted
        dataStoreManager.saveIsLogsDeleted(isLogsDeleted)
    }

    fun setRegistrationProof(proof: ZkProof?) {
        _registrationProof.value = proof

        proof?.let {
            dataStoreManager.saveRegistrationProof(proof)
        }
    }

    fun setLightRegistrationData(data: LightRegistrationData?) {
        data?.let {
            dataStoreManager.saveLightRegistrationData(it)
        }

    }

    fun getProfiler(): Profile{
        return Profile().newProfile(privateKeyBytes)
    }

    fun rarimoAddress(): String {
        return getProfiler().rarimoAddress
    }

    fun evmAddress(): String {
        if (_privateKey.value != null)
            return Credentials.create(_privateKey.value).address
        return ""
    }

    fun getPassportNullifier(): String {
        return registrationProof.value?.let {
            it.pub_signals.get(0)
        } ?: ""
    }

    fun getUserAirDropNullifier(): String {
        return getProfiler().calculateEventNullifierInt(BaseConfig.AIRDROP_SVC_ID)
    }

    fun getUserAirDropNullifierHex(): String {
        return "0x" + BigInteger(this.getUserAirDropNullifier())
            .toByteArray()
            .toHexString()
    }

    fun getUserPointsNullifier(): String {
        return getProfiler().calculateEventNullifierInt(BaseConfig.POINTS_SVC_ID)
    }

    fun getUserPointsNullifierHex(): String {
        return "0x" + BigInteger(this.getUserPointsNullifier())
            .toByteArray()
            .toHexString()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun genPrivateKey(): String {
        return Identity.newBJJSecretKey().toHexString()
    }

    fun savePrivateKey(pk: String) {
        _privateKey.value = pk
        dataStoreManager.savePrivateKey(pk)
    }


}