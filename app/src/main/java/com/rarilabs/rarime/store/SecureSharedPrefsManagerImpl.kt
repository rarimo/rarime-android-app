package com.rarilabs.rarime.store

import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rarilabs.rarime.api.registration.models.LightRegistrationData
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.data.enums.AppLanguage
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.manager.LikenessRule
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.manager.WalletAssetJSON
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.LocaleUtil
import com.rarilabs.rarime.util.data.GrothProof
import com.rarilabs.rarime.util.data.UniversalProof
import com.rarilabs.rarime.util.data.UniversalProofWrapper
import com.rarilabs.rarime.util.data.toProof
import com.rarilabs.rarime.util.data.toWrapper
import java.io.ByteArrayOutputStream
import javax.crypto.AEADBadTagException
import javax.inject.Inject


class SecureSharedPrefsManagerImpl @Inject constructor(
    private val application: Application
) : SecureSharedPrefsManager {

    private val accessTokens = mapOf(
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
        "ACCESS_TOKEN" to "ACCESS_TOKEN",
        "REFRESH_TOKEN" to "REFRESH_TOKEN",
        "IS_IN_WAITLIST" to "IS_IN_WAITLIST",
        "IS_LOGS_DELETED" to "IS_LOGS_DELETED",
        "DEFERRED_REFERRAL_CODE" to "DEFERRED_REFERRAL_CODE",
        "GUESS_REFERRAL_CODE" to "GUESS_REFERRAL_CODE",
        "LIGHT_REGISTRATION_DATA" to "LIGHT_REGISTRATION_DATA",
        "ALREADY_RESERVED" to "ALREADY_RESERVED",
        "PASSPORT_STATUS" to "PASSPORT_STATUS",
        "SELECTED_LIKENESS_OPTION" to "SELECTED_LIKENESS_OPTION",
        "LIKENESS_DATA" to "LIKENESS_DATA",
        "LIKENESS_FACE" to "LIKENESS_FACE"
    )

    private val PREFS_FILE_NAME = "sharedPrefFile12"
    private var sharedPref: SharedPreferences? = null


    private fun getSharedPreferences(): SharedPreferences {
        return try {
            if (sharedPref == null) {
                val masterKey = MasterKey.Builder(application)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                sharedPref = EncryptedSharedPreferences.create(
                    application,
                    PREFS_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            }
            sharedPref!!
        } catch (e: AEADBadTagException) {
            // Handle corrupted data, e.g., reset shared preferences
            application.deleteSharedPreferences(PREFS_FILE_NAME)
            getSharedPreferences() // Retry after reset
        }
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

    override fun readPasscodeState(): SecurityCheckState {
        return SecurityCheckState.fromInt(
            getSharedPreferences().getInt(
                accessTokens["PASSCODE_STATE"], SecurityCheckState.UNSET.value
            )
        )
    }

    override fun savePasscodeState(state: SecurityCheckState) {
        val editor = getEditor()
        editor.putInt(accessTokens["PASSCODE_STATE"], state.value)
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
            ?: listOf(PassportIdentifier.DOCUMENT_ID, PassportIdentifier.NATIONALITY)
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
                    it.transactions = walletAsset.transactions
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

            if (walletAsset == null) {
                return walletAssets.first()
            }

            return walletAsset
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
        editor.putString(accessTokens["E_DOCUMENT"], jsonEDocument)
        editor.apply()
    }

    override fun readEDocument(): EDocument? {
        try {
            val jsonEDocument =
                getSharedPreferences().getString(accessTokens["E_DOCUMENT"], null) ?: return null

            val resp = Gson().fromJson(jsonEDocument, EDocument::class.java)

            ErrorHandler.logDebug("Read EDoc", "EDocument is available")


            return resp
        } catch (e: Exception) {
            ErrorHandler.logDebug("Read EDoc", "EDocument is null or not available")
            return null
        }
    }


    override fun saveRegistrationProof(proof: GrothProof) {
        val jsonProof = Gson().toJson(proof)
        val editor = getEditor()
        editor.putString(accessTokens["REGISTRATION_PROOF"], jsonProof)
        editor.apply()
    }

    override fun readRegistrationProof(): GrothProof? {
        val jsonProof = getSharedPreferences().getString(accessTokens["REGISTRATION_PROOF"], null)
            ?: return null

        return Gson().fromJson(jsonProof, GrothProof::class.java)
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

    override fun readIsInWaitlist(): Boolean {
        return getSharedPreferences().getBoolean(accessTokens["IS_IN_WAITLIST"], false)
    }

    override fun saveIsInWaitlist(isInWaitlist: Boolean) {
        val editor = getEditor()
        editor.putBoolean(accessTokens["IS_IN_WAITLIST"], isInWaitlist)
        editor.apply()
    }

    override fun deletePassport() {
        val editor = getEditor()
        editor.remove(accessTokens["E_DOCUMENT"])
        editor.apply()
    }

    override fun readIsLogsDeleted(): Boolean {
        return getSharedPreferences().getBoolean(accessTokens["IS_LOGS_DELETED"], false)
    }

    override fun saveIsLogsDeleted(isLogsDeleted: Boolean) {
        val editor = getEditor()
        editor.putBoolean(accessTokens["IS_LOGS_DELETED"], isLogsDeleted)
        editor.apply()
    }

    override fun saveDeferredReferralCode(referralCode: String) {
        val editor = getEditor()
        editor.putString(accessTokens["DEFERRED_REFERRAL_CODE"], referralCode)
        editor.apply()
    }

    override fun getDeferredReferralCode(): String? {
        return getSharedPreferences().getString(accessTokens["DEFERRED_REFERRAL_CODE"], null)
    }


    override fun saveGuessReferralCode(referralCode: String) {
        val editor = getEditor()
        editor.putString(accessTokens["GUESS_REFERRAL_CODE"], referralCode)
        editor.apply()
    }

    override fun getGuessReferralCode(): String? {
        return getSharedPreferences().getString(accessTokens["GUESS_REFERRAL_CODE"], null)
    }

    override fun saveLightRegistrationData(lightRegistrationData: LightRegistrationData) {
        val editor = getEditor()
        val lightRegistrationDataJson = Gson().toJson(lightRegistrationData)
        editor.putString(accessTokens["LIGHT_REGISTRATION_DATA"], lightRegistrationDataJson)
        editor.apply()
    }

    override fun getLightRegistrationData(): LightRegistrationData? {
        val lightRegistrationDataJson =
            getSharedPreferences().getString(accessTokens["LIGHT_REGISTRATION_DATA"], null)

        val lightRegistrationData = Gson().fromJson(
            lightRegistrationDataJson,
            LightRegistrationData::class.java
        )

        return lightRegistrationData
    }

    override fun saveIsAlreadyReserved(isAlreadyReserved: Boolean) {
        val editor = getEditor()
        editor.putBoolean(accessTokens["ALREADY_RESERVED"], isAlreadyReserved)
        editor.apply()
    }

    override fun getIsAlreadyReserved(): Boolean {
        return getSharedPreferences().getBoolean(accessTokens["ALREADY_RESERVED"], false)
    }


    override fun saveSelectedLikenessRule(likenessRule: LikenessRule) {
        val editor = getEditor()
        editor.putInt(accessTokens["SELECTED_LIKENESS_OPTION"], likenessRule.value)
        editor.apply()
    }

    override fun getSelectedLikenessRule(): LikenessRule? {

        val enumValue = getSharedPreferences().getInt(
            accessTokens["SELECTED_LIKENESS_OPTION"],
            -1
        )

        if (enumValue == -1)
            return null

        return LikenessRule.fromInt(
            enumValue
        )
    }


    override fun saveLivenessProof(proof: GrothProof) {
        val editor = getEditor()

        val proofjson = Gson().toJson(proof)
        editor.putString(accessTokens["LIKENESS_DATA"], proofjson)
        editor.apply()
    }

    override fun getLivenessProof(): GrothProof? {
        val proofJson = getSharedPreferences().getString(accessTokens["LIKENESS_DATA"], null)
            ?: return null

        return Gson().fromJson<GrothProof>(proofJson, GrothProof::class.java)
    }

    override fun saveLikenessFace(face: Bitmap) {
        val baos = ByteArrayOutputStream().apply {
            face.compress(Bitmap.CompressFormat.PNG, 100, this)
        }
        val bytes = baos.toByteArray()

        val encoded = Base64.encodeToString(bytes, Base64.DEFAULT)

        getEditor()
            .putString(accessTokens["LIKENESS_FACE"], encoded)
            .apply()
    }

    override fun getLikenessFace(): Bitmap? {
        val encoded = getSharedPreferences()
            .getString(accessTokens["LIKENESS_FACE"], null)

        if (encoded.isNullOrEmpty()) {
            return null
        }

        val bytes = Base64.decode(encoded, Base64.DEFAULT)

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun saveUniversalProof(proof: UniversalProof) {
        val wrapper = proof.toWrapper()
        val jsonProof = Gson().toJson(wrapper)
        val editor = getEditor()
        editor.putString(accessTokens["REGISTRATION_PROOF"], jsonProof)
        editor.apply()
    }

    override fun readUniversalProof(): UniversalProof? {
        // 1. Try LightProof (LightRegistrationData + GrothProof, old format)
        val jsonLight =
            getSharedPreferences().getString(accessTokens["LIGHT_REGISTRATION_DATA"], null)
        val jsonGroth = getSharedPreferences().getString(accessTokens["REGISTRATION_PROOF"], null)
        if (jsonLight != null && jsonGroth != null) {
            try {
                val light = Gson().fromJson(jsonLight, LightRegistrationData::class.java)
                val groth = Gson().fromJson(jsonGroth, GrothProof::class.java)
                if (light != null && groth != null) return UniversalProof.fromLight(light, groth)
            } catch (_: Exception) {
            }
        }

        // 2. Try UniversalProofWrapper (new format)
        if (jsonGroth != null) {
            try {
                val wrapper = Gson().fromJson(jsonGroth, UniversalProofWrapper::class.java)
                if (wrapper?.type != null) return wrapper.toProof()
            } catch (_: Exception) {
            }

            // 3. Try GrothProof (legacy format)
            try {
                val groth = Gson().fromJson(jsonGroth, GrothProof::class.java)
                if (groth?.pub_signals != null) return UniversalProof.fromGroth(groth)
            } catch (_: Exception) {
            }
        }

        return null
    }
}