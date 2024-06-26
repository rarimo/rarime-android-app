package com.distributedLab.rarime.manager

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.PassportIdentifier
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.util.LocaleUtil
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.util.data.ZkProof
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        "PASSPORT_IDENTIFIERS" to "PASSPORT_IDENTIFIERS",
        "COLOR_SCHEME" to "COLOR_SCHEME",
        "LANGUAGE" to "LANGUAGE",
        "WALLET_BALANCE" to "WALLET_BALANCE",
        "PASSCODE" to "PASSCODE",
        "LOCK_TIMESTAMP" to "LOCK_TIMESTAMP",
        "WALLET_BALANCE" to "WALLET_BALANCE",
        "E_DOCUMENT" to "E_DOCUMENT",
        "PRIVATE_KEY" to "PRIVATE_KEY",
        "REGISTRATION_PROOF" to "REGISTRATION_PROOF",
        "TX" to "TX"
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

    override fun savePrivateKey(privateKey: String) {
        val editor = getEditor()
        editor.putString(accessTokens["PRIVATE_KEY"], privateKey)
        editor.apply()
    }

    override fun readPrivateKey(): String? {
        return getSharedPreferences().getString(accessTokens["PRIVATE_KEY"], null)
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
            accessTokens["IS_PASSPORT_INCOGNITO_MODE"], true
        )
    }

    override fun saveIsPassportIncognitoMode(isIncognito: Boolean) {
        val editor = getEditor()
        editor.putBoolean(accessTokens["IS_PASSPORT_INCOGNITO_MODE"], isIncognito)
        editor.apply()
    }

    override fun readPassportIdentifiers(): List<PassportIdentifier> {
        val valuesList = getSharedPreferences().getStringSet(
            accessTokens["PASSPORT_IDENTIFIERS"], null
        )?.toList()

        return valuesList?.map { PassportIdentifier.fromString(it) }
            ?: listOf(PassportIdentifier.NATIONALITY, PassportIdentifier.DOCUMENT_ID)
    }

    override fun savePassportIdentifiers(identifiers: List<PassportIdentifier>) {
        val editor = getEditor()
        editor.putStringSet(
            accessTokens["PASSPORT_IDENTIFIERS"],
            identifiers.map { it.value }.toSet()
        )
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
        val systemLocale = LocaleUtil.getSystemLocale(application)
        val defaultLanguage = try {
            AppLanguage.fromLocaleTag(systemLocale)
        } catch (e: NoSuchElementException) {
            AppLanguage.ENGLISH
        }
        return AppLanguage.fromLocaleTag(
            getSharedPreferences().getString(
                accessTokens["LANGUAGE"], defaultLanguage.localeTag
            ) ?: defaultLanguage.localeTag
        )
    }

    override fun saveLanguage(language: AppLanguage) {
        val editor = getEditor()
        editor.putString(accessTokens["LANGUAGE"], language.localeTag)
        editor.apply()
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

    override fun readLockTimestamp(): Long {
        return getSharedPreferences().getLong(accessTokens["LOCK_TIMESTAMP"], 0L)
    }

    override fun saveLockTimestamp(timestamp: Long) {
        val editor = getEditor()
        editor.putLong(accessTokens["LOCK_TIMESTAMP"], timestamp)
        editor.apply()
    }

    override fun saveEDocument(eDocument: EDocument) {
        val jsonEDocument = Gson().toJson(eDocument)
        val editor = getEditor()
        editor.putString(accessTokens["EDocument"], jsonEDocument)
        editor.apply()
    }

    override fun readEDocument(): EDocument? {
        val jsonEDocument =
            getSharedPreferences().getString(accessTokens["EDocument"], null) ?: return null
        return Gson().fromJson(jsonEDocument, EDocument::class.java)
    }

    override fun saveRegistrationProof(proof: ZkProof) {
        val jsonProof = Gson().toJson(proof)
        val editor = getEditor()
        editor.putString(accessTokens["REGISTRATION_PROOF"], jsonProof)
        editor.apply()
    }

    override fun readRegistrationProof(): ZkProof? {
        val jsonProof = getSharedPreferences().getString(accessTokens["REGISTRATION_PROOF"], null)
            ?: return null
        return Gson().fromJson(jsonProof, ZkProof::class.java)
    }

    override fun readTransactions(): List<Transaction> {
        val jsonTx =
            getSharedPreferences().getString(accessTokens["TX"], null) ?: return emptyList()
        val listType = object : TypeToken<List<Transaction?>?>() {}.type

        val txList = Gson().fromJson<List<Transaction>>(jsonTx, listType)
        return txList
    }

    override fun addTransaction(transaction: Transaction) {
        val allTransactions = readTransactions().toMutableList()
        allTransactions.add(transaction)
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        val editor = getEditor()
        val jsonTx = Gson().toJson(transactions)
        editor.putString(accessTokens["TX"], jsonTx)
        editor.apply()
    }


}