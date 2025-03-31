package com.rarilabs.rarime.manager

import androidx.compose.runtime.mutableStateOf
import com.rarilabs.rarime.api.registration.models.LightRegistrationData
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.data.ZkProof
import identity.Identity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.web3j.utils.Numeric
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PassportManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    private val identityManager: IdentityManager
) {
    private var _passport = MutableStateFlow(dataStoreManager.readEDocument())

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

    private var _passportStatus = MutableStateFlow(dataStoreManager.readPassportStatus())
    val passportStatus: StateFlow<PassportStatus>
        get() = _passportStatus.asStateFlow()

    fun updatePassportCardLook(look: PassportCardLook) {
        passportCardLook.value = look
        dataStoreManager.savePassportCardLook(look)
    }

    fun getIsoCode(): String? {
        return _passport.value?.personDetails?.nationality
    }

    fun updatePassportStatus(status: PassportStatus) {
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

    suspend fun loadPassportStatus() {
        if (passport.value == null) {
            return
        }

        if (dataStoreManager.readPassportStatus() == PassportStatus.ALREADY_REGISTERED_BY_OTHER_PK)
            return

        if (dataStoreManager.readRegistrationProof() == null) {
            updatePassportStatus(PassportStatus.UNREGISTERED)
            return
        }

        _isShowPassport.value = true

        val isInWaitlist = dataStoreManager.readIsInWaitlist()

        val isUnsupported =
            Constants.NOT_ALLOWED_COUNTRIES.contains(passport.value!!.personDetails?.nationality)

        var isIdentityCreated = false

        try {
            val activeIdentity = identityManager.getPassportActiveIdentity(passport.value!!)

            isIdentityCreated = !activeIdentity.isNullOrEmpty()
        } catch (e: Exception) {

        }

        if (isInWaitlist) {
            updatePassportStatus(PassportStatus.WAITLIST)
            return
        }

        if (isIdentityCreated) {
            updatePassportStatus(if (isUnsupported) PassportStatus.NOT_ALLOWED else PassportStatus.ALLOWED)
            return
        }
        updatePassportStatus(if (isUnsupported) PassportStatus.WAITLIST_NOT_ALLOWED else PassportStatus.WAITLIST)
    }

    fun getPassportInfoKey(
        eDocument: EDocument,
        zkProof: ZkProof,
        lightProofData: LightRegistrationData? = null
    ): ByteArray {

        var _lightProofData: LightRegistrationData? = lightProofData

        if (_lightProofData == null) {
            _lightProofData = dataStoreManager.getLightRegistrationData()
        }

        val passportInfoKey: String =

            if (_lightProofData != null) {
                if (eDocument.dg15.isNullOrEmpty()) {
                    BigInteger(Numeric.hexStringToByteArray(_lightProofData.passport_hash)).toString()
                } else {
                    BigInteger(Numeric.hexStringToByteArray(_lightProofData.public_key)).toString()
                }
            } else {
                if (eDocument.dg15.isNullOrEmpty()) {
                    zkProof.pub_signals[1] //lightProofData.passport_hash
                } else {
                    zkProof.pub_signals[0] //lightProofData.public_key
                }
            }


        var passportInfoKeyBytes = Identity.bigIntToBytes(passportInfoKey)

        if (passportInfoKeyBytes.size > 32) {
            passportInfoKeyBytes = ByteArray(32 - passportInfoKeyBytes.size) + passportInfoKeyBytes
        } else if (passportInfoKeyBytes.size < 32) {
            val len = 32 - passportInfoKeyBytes.size
            var tempByteArray = ByteArray(len) { 0 }
            tempByteArray += passportInfoKeyBytes
            passportInfoKeyBytes = tempByteArray
        }

        return passportInfoKeyBytes
    }
}
