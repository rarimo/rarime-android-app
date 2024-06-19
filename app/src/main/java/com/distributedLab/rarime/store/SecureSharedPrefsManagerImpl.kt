package com.distributedLab.rarime.store

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.PassportIdentifier
import com.distributedLab.rarime.data.enums.PassportStatus
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.manager.WalletAsset
import com.distributedLab.rarime.manager.WalletAssetJSON
import com.distributedLab.rarime.modules.passportScan.models.EDocument
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.util.LocaleUtil
import com.distributedLab.rarime.util.data.ZkProof
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigInteger
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
        "PASSCODE" to "PASSCODE",
        "LOCK_TIMESTAMP" to "LOCK_TIMESTAMP",
        "WALLET_ASSETS" to "WALLET_ASSETS",
        "SELECTED_WALLET_ASSET" to "SELECTED_WALLET_ASSET",
        "E_DOCUMENT" to "E_DOCUMENT",
        "PRIVATE_KEY" to "PRIVATE_KEY",
        "REGISTRATION_PROOF" to "REGISTRATION_PROOF",
        "TX" to "TX",
        "PASSPORT_STATUS" to "PASSPORT_STATUS",
        "ACCESS_TOKEN" to "ACCESS_TOKEN",
        "REFRESH_TOKEN" to "REFRESH_TOKEN"
    )

    private val PREFS_FILE_NAME = "sharedPrefFile12"
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
            accessTokens["PASSPORT_IDENTIFIERS"], identifiers.map { it.value }.toSet()
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

    override fun readWalletAssets(assetsToPopulate: List<WalletAsset>): List<WalletAsset> {
        val jsonWalletAssets = getSharedPreferences().getString(accessTokens["WALLET_ASSETS"], null)
            ?: return assetsToPopulate
        val listType = object : TypeToken<List<WalletAsset?>?>() {}.type

        try {
            val parsedWalletAssets =
                Gson().fromJson<List<WalletAssetJSON>>(jsonWalletAssets, listType)

            return assetsToPopulate.map {
                val walletAsset =
                    parsedWalletAssets.find { asset -> asset.tokenSymbol == it.token.symbol }

                if (walletAsset != null) {
                    it.transactions.value = walletAsset.transactions
                }
                it
            }
        } catch (e: Exception) {
            return assetsToPopulate
        }
    }

    override fun saveWalletAssets(walletAssets: List<WalletAsset>) {
        val editor = getEditor()
        val jsonBalances = Gson().toJson(walletAssets.map { it.toJSON() })
        editor.putString(accessTokens["WALLET_ASSETS"], jsonBalances)
        editor.apply()
    }

    override fun readSelectedWalletAsset(walletAssets: List<WalletAsset>): WalletAsset {
        val jsonWalletAsset = getSharedPreferences().getString(
            accessTokens["SELECTED_WALLET_ASSET"], walletAssets.first().toJSON()
        )

        val walletAssetType = object : TypeToken<WalletAsset?>() {}.type

        try {
            val parsedWalletAsset =
                Gson().fromJson<WalletAssetJSON>(jsonWalletAsset, walletAssetType)

            val walletAsset = walletAssets.find { it.token.symbol == parsedWalletAsset.tokenSymbol }

            return walletAsset ?: walletAssets.first()
        } catch (error: Exception) {
            return walletAssets.first()
        }
    }

    override fun saveSelectedWalletAsset(walletAsset: WalletAsset) {
        val editor = getEditor()
        editor.putString(accessTokens["SELECTED_WALLET_ASSET"], walletAsset.toJSON())
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

    override fun savePassportStatus(passportStatus: PassportStatus) {
        val editor = getEditor()
        editor.putInt(accessTokens["PASSPORT_STATUS"], passportStatus.value)
        editor.apply()
    }

    override fun readPassportStatus(): PassportStatus {
        return PassportStatus.fromInt(
            getSharedPreferences().getInt(
                accessTokens["PASSPORT_STATUS"], 1
            )
        )
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
        saveTransactions(allTransactions)
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        val editor = getEditor()
        val jsonTx = Gson().toJson(transactions)
        editor.putString(accessTokens["TX"], jsonTx)
        editor.apply()
    }

    override fun saveAccessToken(accessToken: String) {
        val editor = getEditor()
        editor.putString(accessTokens["ACCESS_TOKEN"], accessToken)
        editor.apply()
    }

    override fun readAccessToken(): String? {
        return getSharedPreferences().getString(accessTokens["ACCESS_TOKEN"], null)
    }

    override fun saveRefreshToken(refreshToken: String) {
        val editor = getEditor()
        editor.putString(accessTokens["REFRESH_TOKEN"], refreshToken)
        editor.apply()
    }

    override fun readRefreshToken(): String? {
        return getSharedPreferences().getString(accessTokens["REFRESH_TOKEN"], null)
    }

    override fun clearAllData() {
        val editor = getEditor()
        for (entry in accessTokens.entries.iterator()) {
            editor.remove(entry.value)
        }
        editor.clear()
        editor.apply()
    }
}