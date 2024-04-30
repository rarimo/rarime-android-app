package com.distributedLab.rarime.data.manager

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import javax.inject.Inject


class SecureSharedPrefsManagerImpl @Inject constructor(
    private val application: Application
) : SecureSharedPrefsManager {

    private val accessTokens = mapOf(
        "IS_INTRO_FINISHED" to "IS_INTRO_FINISHED",
        "PASSCODE_STATE" to "PASSCODE_STATE",
        "BIOMETRICS_STATE" to "BIOMETRICS_STATE",
        "PASSPORT_CARD_LOOK" to "PASSPORT_CARD_LOOK",
        "IS_PASSPORT_INCOGNITO_MODE" to "IS_PASSPORT_INCOGNITO_MODE",
        "COLOR_SCHEME" to "COLOR_SCHEME",
        "LANGUAGE" to "LANGUAGE",
        "WALLET_BALANCE" to "WALLET_BALANCE",
        "PASSCODE" to "PASSCODE"
    )

    private val PREFS_FILE_NAME = "sharedPrefFile"
    private var sharedPref: SharedPreferences? = null


    private fun getSharedPreferences(): SharedPreferences {
        if (sharedPref == null) {
            val masterKey =
                MasterKey.Builder(application).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
            sharedPref = EncryptedSharedPreferences.create(
                application,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return sharedPref!!
        }

        return sharedPref!!

    }

    private fun getEditor(): SharedPreferences.Editor {
        return getSharedPreferences().edit()
    }

    override fun readIsIntroFinished(): Boolean {
        return getSharedPreferences().getBoolean(accessTokens["IS_INTRO_FINISHED"], false)
    }

    override fun saveIsIntroFinished(isFinished: Boolean) {
        val editor = getEditor()
        editor.putBoolean(accessTokens["IS_INTRO_FINISHED"], isFinished)
        editor.apply()
    }

    override fun readPasscodeState(): SecurityCheckState {
        return SecurityCheckState.fromInt(
            getSharedPreferences().getInt(
                accessTokens["IS_INTRO_FINISHED"], SecurityCheckState.UNSET.value
            )
        )
    }

    override fun savePasscodeState(state: SecurityCheckState) {
        val editor = getEditor()
        editor.putInt(accessTokens["IS_INTRO_FINISHED"], state.value)
        editor.apply()
    }

    override fun readBiometricsState(): SecurityCheckState {
        return SecurityCheckState.fromInt(
            getSharedPreferences().getInt(
                accessTokens["BIOMETRICS_STATE"], SecurityCheckState.UNSET.value
            )
        )
    }

    override fun saveBiometricsState(state: SecurityCheckState) {
        val editor = getEditor()
        editor.putInt(accessTokens["BIOMETRICS_STATE"], state.value)
        editor.apply()
    }

    override fun readPassportCardLook(): PassportCardLook {
        return PassportCardLook.fromInt(
            getSharedPreferences().getInt(
                accessTokens["PASSPORT_CARD_LOOK"], PassportCardLook.WHITE.value
            )
        )
    }

    override fun savePassportCardLook(look: PassportCardLook) {
        val editor = getEditor()
        editor.putInt(accessTokens["PASSPORT_CARD_LOOK"], look.value)
        editor.apply()
    }

    override fun readIsPassportIncognitoMode(): Boolean {
        return getSharedPreferences().getBoolean(
            accessTokens["IS_PASSPORT_INCOGNITO_MODE"], false
        )
    }

    override fun saveIsPassportIncognitoMode(isIncognito: Boolean) {
        val editor = getEditor()
        editor.putBoolean(accessTokens["IS_PASSPORT_INCOGNITO_MODE"], isIncognito)
        editor.apply()
    }

    override fun readColorScheme(): AppColorScheme {
        return AppColorScheme.fromInt(
            getSharedPreferences().getInt(
                accessTokens["COLOR_SCHEME"], AppColorScheme.SYSTEM.value
            )
        )
    }

    override fun saveColorScheme(scheme: AppColorScheme) {
        val editor = getEditor()
        editor.putInt(accessTokens["COLOR_SCHEME"], scheme.value)
        editor.apply()
    }

    override fun readLanguage(): AppLanguage {
        return AppLanguage.fromString(
            getSharedPreferences().getString(
                accessTokens["LANGUAGE"], AppLanguage.ENGLISH.value
            ) ?: AppLanguage.ENGLISH.value
        )
    }

    override fun saveLanguage(language: AppLanguage) {
        val editor = getEditor()
        editor.putString(accessTokens["LANGUAGE"], language.value)
    }

    override fun readWalletBalance(): Double {
        return getSharedPreferences().getString(accessTokens["WALLET_BALANCE"], "0.0")?.toDouble()
            ?: 0.0
    }

    override fun saveWalletBalance(balance: Double) {
        val editor = getEditor()
        editor.putString(accessTokens["WALLET_BALANCE"], balance.toString())
        editor.apply()
    }

    override fun readPasscode(): String {
        return getSharedPreferences().getString(accessTokens["PASSCODE"], "") ?: ""
    }

    override fun savePasscode(passcode: String) {
        val editor = getEditor()
        editor.putString(accessTokens["PASSCODE"], passcode)
        editor.apply()
    }


}