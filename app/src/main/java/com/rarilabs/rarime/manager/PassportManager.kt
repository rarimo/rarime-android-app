package com.rarilabs.rarime.manager

import android.util.Log
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
    private val dataStoreManager: SecureSharedPrefsManager
) {
    var _passport = MutableStateFlow(dataStoreManager.readEDocument())
        private set
    val passport: StateFlow<EDocument?>
        get() = _passport.asStateFlow()

    var passportCardLook = mutableStateOf(dataStoreManager.readPassportCardLook())
        private set
    var isIncognitoMode = mutableStateOf(dataStoreManager.readIsPassportIncognitoMode())
        private set
    var passportIdentifiers = mutableStateOf(dataStoreManager.readPassportIdentifiers())
        private set

    private var _passportStatus = MutableStateFlow(dataStoreManager.readPassportStatus())

    val passportStatus: StateFlow<PassportStatus>
        get() = _passportStatus.asStateFlow()

    fun reloadPassport() {
        Log.i("PassportManager", "Reloading passport")
        _passport.value = dataStoreManager.readEDocument()
    }

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
}
