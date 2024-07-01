package com.rarilabs.rarime.manager

import androidx.compose.runtime.mutableStateOf
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.Constants
import identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PassportManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    private val rarimoContractManager: RarimoContractManager,
    private val identityManager: IdentityManager,
) {
    var _passport = MutableStateFlow(dataStoreManager.readEDocument())
        private set
    val passport: StateFlow<EDocument?>
        get() = _passport.asStateFlow()

    private var _isShowPassport = MutableStateFlow(false)
    val isShowPassport: StateFlow<Boolean>
        get() = _isShowPassport.asStateFlow()


    var passportCardLook = mutableStateOf(dataStoreManager.readPassportCardLook())
        private set
    var isIncognitoMode = mutableStateOf(dataStoreManager.readIsPassportIncognitoMode())
        private set
    var passportIdentifiers = mutableStateOf(dataStoreManager.readPassportIdentifiers())
        private set

    private var _passportStatus = MutableStateFlow(PassportStatus.UNSCANNED)
    val passportStatus: StateFlow<PassportStatus>
        get() = _passportStatus.asStateFlow()

    fun updatePassportCardLook(look: PassportCardLook) {
        passportCardLook.value = look
        dataStoreManager.savePassportCardLook(look)
    }

    fun getIsoCode(): String? {
        return _passport.value?.personDetails?.nationality
    }

    private fun updatePassportStatus(status: PassportStatus) {
        _passportStatus.value = status
        dataStoreManager.savePassportStatus(status)
    }

    fun updateIsIncognitoMode(isIncognitoMode: Boolean) {
        this.isIncognitoMode.value = isIncognitoMode
        dataStoreManager.saveIsPassportIncognitoMode(isIncognitoMode)
    }

    fun updatePassportIdentifiers(identifiers: List<PassportIdentifier>) {
        passportIdentifiers.value = identifiers
        dataStoreManager.savePassportIdentifiers(identifiers)
    }

    fun deletePassport() {
        dataStoreManager.deletePassport()
        _passport.value = null
    }

    fun setPassport(passport: EDocument?) {
        if (passport != null) {
            dataStoreManager.saveEDocument(passport)
        }
        _passport.value = passport
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun getPassportActiveIdentity(): String? {

        try {
            val passportInfoKey: String? = if (passport.value?.dg15?.isEmpty() ?: false) {
                identityManager.registrationProof.value?.pub_signals?.get(1)
            } else {
                identityManager.registrationProof.value?.pub_signals?.get(0)
            }

            var passportInfoKeyBytes = Identity.bigIntToBytes(passportInfoKey)

            if (passportInfoKeyBytes.size != 32) {
                passportInfoKeyBytes = passportInfoKeyBytes.copyOf(32)
            }


            val stateKeeperContract = rarimoContractManager.getStateKeeper()

            val passportInfoRaw = withContext(Dispatchers.IO) {
                stateKeeperContract.getPassportInfo(passportInfoKeyBytes).send()
            }

            return passportInfoRaw.component1().activeIdentity.toHexString()
        } catch (e: Exception) {
            return null
        }

    }

    suspend fun loadPassportStatus() {
        if (passport.value == null) {
            return
        }

        val isInWaitlist = dataStoreManager.readIsInWaitlist()

        val isUnsupported = Constants.NOT_ALLOWED_COUNTRIES.contains(passport.value!!.personDetails?.nationality)
        var isIdentityCreated = false

        try {
            val activeIdentity = getPassportActiveIdentity()

            isIdentityCreated = activeIdentity != null && activeIdentity.isNotEmpty()
        } catch (e: Exception) {}

        if (isInWaitlist) {
            updatePassportStatus(PassportStatus.WAITLIST)

            return
        }

        if (isIdentityCreated) {
            updatePassportStatus(if (isUnsupported) PassportStatus.NOT_ALLOWED else PassportStatus.ALLOWED)

            return
        }

        updatePassportStatus(if (isUnsupported) PassportStatus.WAITLIST_NOT_ALLOWED else PassportStatus.WAITLIST)


        when (passportStatus.value) {
            PassportStatus.UNSCANNED -> {
                _isShowPassport.value = false
            }
            else -> {
                _isShowPassport.value = true
            }
        }
    }
}
