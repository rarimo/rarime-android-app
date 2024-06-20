package com.rarilabs.rarime.manager

import androidx.compose.runtime.mutableStateOf
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PassportManager @Inject constructor(
    private val secureSharedPrefsManager: SecureSharedPrefsManager
) {

    var passport = mutableStateOf<EDocument?>(
        secureSharedPrefsManager.readEDocument()
    )
        private set
    var passportCardLook = mutableStateOf(secureSharedPrefsManager.readPassportCardLook())
        private set
    var isIncognitoMode = mutableStateOf(secureSharedPrefsManager.readIsPassportIncognitoMode())
        private set
    var passportIdentifiers = mutableStateOf(secureSharedPrefsManager.readPassportIdentifiers())
        private set

    private var _passportStatus = MutableStateFlow(secureSharedPrefsManager.readPassportStatus())

    val passportStatus: StateFlow<PassportStatus>
        get() = _passportStatus.asStateFlow()

    fun updatePassportCardLook(look: PassportCardLook) {
        passportCardLook.value = look
        secureSharedPrefsManager.savePassportCardLook(look)
    }

    fun getIsoCode(): String? {
        return passport.value?.personDetails?.issuerAuthority
    }

    fun updatePassportStatus(status: PassportStatus) {
        _passportStatus.value = status
        secureSharedPrefsManager.savePassportStatus(status)
    }

    fun updateIsIncognitoMode(isIncognitoMode: Boolean) {
        this.isIncognitoMode.value = isIncognitoMode
        secureSharedPrefsManager.saveIsPassportIncognitoMode(isIncognitoMode)
    }

    fun updatePassportIdentifiers(identifiers: List<PassportIdentifier>) {
        passportIdentifiers.value = identifiers
        secureSharedPrefsManager.savePassportIdentifiers(identifiers)
    }

    fun setPassport(passport: EDocument?) {
        if (passport != null) {
            secureSharedPrefsManager.saveEDocument(passport)
        }
        this.passport.value = passport
    }
}
