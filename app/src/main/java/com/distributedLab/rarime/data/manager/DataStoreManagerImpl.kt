package com.distributedLab.rarime.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.domain.manager.DataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val readOnlyProperty = preferencesDataStore(name = "rarime_datastore")
val Context.dataStore: DataStore<Preferences> by readOnlyProperty

private object PreferenceKeys {
    val IS_INTRO_FINISHED = booleanPreferencesKey("is_intro_finished")
    val PASSCODE_STATE = intPreferencesKey("passcode_state")
    val BIOMETRICS_STATE = intPreferencesKey("biometrics_state")
    val PASSPORT_CARD_LOOK = intPreferencesKey("passport_card_look")
    val IS_PASSPORT_INCOGNITO_MODE = booleanPreferencesKey("is_passport_incognito_mode")
    val COLOR_SCHEME = intPreferencesKey("color_scheme")
    val LANGUAGE = stringPreferencesKey("language")
    val WALLET_BALANCE = doublePreferencesKey("wallet_balance")
    val WALLET_TRANSACTIONS = stringPreferencesKey("wallet_transactions")
    val IS_AIRDROP_CLAIMED = booleanPreferencesKey("is_airdrop_claimed")
}

class DataStoreManagerImpl @Inject constructor(
    private val context: Context
) : DataStoreManager {

    override fun readIsIntroFinished(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.IS_INTRO_FINISHED] ?: false
        }
    }

    override suspend fun saveIsIntroFinished(isFinished: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.IS_INTRO_FINISHED] = isFinished
        }
    }

    override fun readPasscodeState(): Flow<SecurityCheckState> {
        return context.dataStore.data.map { preferences ->
            SecurityCheckState.fromInt(
                preferences[PreferenceKeys.PASSCODE_STATE] ?: SecurityCheckState.UNSET.value
            )
        }
    }

    override suspend fun savePasscodeState(state: SecurityCheckState) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.PASSCODE_STATE] = state.value
        }
    }

    override fun readBiometricsState(): Flow<SecurityCheckState> {
        return context.dataStore.data.map { preferences ->
            SecurityCheckState.fromInt(
                preferences[PreferenceKeys.BIOMETRICS_STATE] ?: SecurityCheckState.UNSET.value
            )
        }
    }

    override suspend fun saveBiometricsState(state: SecurityCheckState) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.BIOMETRICS_STATE] = state.value
        }
    }

    override fun readPassportCardLook(): Flow<PassportCardLook> {
        return context.dataStore.data.map { preferences ->
            PassportCardLook.fromInt(
                preferences[PreferenceKeys.PASSPORT_CARD_LOOK] ?: PassportCardLook.WHITE.value
            )
        }
    }

    override suspend fun savePassportCardLook(look: PassportCardLook) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.PASSPORT_CARD_LOOK] = look.value
        }
    }

    override fun readIsPassportIncognitoMode(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.IS_PASSPORT_INCOGNITO_MODE] ?: true
        }
    }

    override suspend fun saveIsPassportIncognitoMode(isIncognito: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.IS_PASSPORT_INCOGNITO_MODE] = isIncognito
        }
    }

    override fun readColorScheme(): Flow<AppColorScheme> {
        return context.dataStore.data.map { preferences ->
            AppColorScheme.fromInt(
                preferences[PreferenceKeys.COLOR_SCHEME] ?: AppColorScheme.SYSTEM.value
            )
        }
    }

    override suspend fun saveColorScheme(scheme: AppColorScheme) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.COLOR_SCHEME] = scheme.value
        }
    }

    override fun readLanguage(): Flow<AppLanguage> {
        return context.dataStore.data.map { preferences ->
            AppLanguage.fromString(
                preferences[PreferenceKeys.LANGUAGE] ?: AppLanguage.ENGLISH.value
            )
        }
    }

    override suspend fun saveLanguage(language: AppLanguage) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.LANGUAGE] = language.value
        }
    }

    override fun readWalletBalance(): Flow<Double> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.WALLET_BALANCE] ?: 0.0
        }
    }

    override suspend fun saveWalletBalance(balance: Double) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.WALLET_BALANCE] = balance
        }
    }

    override fun readWalletTransactions(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.WALLET_TRANSACTIONS] ?: ""
        }
    }

    override suspend fun saveWalletTransactions(transactions: String) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.WALLET_TRANSACTIONS] = transactions
        }
    }

    override fun readIsAirdropClaimed(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.IS_AIRDROP_CLAIMED] ?: false
        }
    }

    override suspend fun saveIsAirdropClaimed(isClaimed: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.IS_AIRDROP_CLAIMED] = isClaimed
        }
    }

}