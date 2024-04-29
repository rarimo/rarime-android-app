package com.distributedLab.rarime.data.manager

import android.app.Application
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
}

class DataStoreManagerImpl @Inject constructor(
    private val application: Application
) : DataStoreManager {

    override fun readIsIntroFinished(): Flow<Boolean> {
        return application.dataStore.data.map {
            it[PreferenceKeys.IS_INTRO_FINISHED] ?: false
        }
    }

    override suspend fun saveIsIntroFinished(isFinished: Boolean) {
        application.dataStore.edit {
            it[PreferenceKeys.IS_INTRO_FINISHED] = isFinished
        }
    }

    override fun readPasscodeState(): Flow<SecurityCheckState> {
        return application.dataStore.data.map {
            SecurityCheckState.fromInt(
                it[PreferenceKeys.PASSCODE_STATE] ?: SecurityCheckState.UNSET.value
            )
        }
    }

    override suspend fun savePasscodeState(state: SecurityCheckState) {
        application.dataStore.edit {
            it[PreferenceKeys.PASSCODE_STATE] = state.value
        }
    }

    override fun readBiometricsState(): Flow<SecurityCheckState> {
        return application.dataStore.data.map {
            SecurityCheckState.fromInt(
                it[PreferenceKeys.BIOMETRICS_STATE] ?: SecurityCheckState.UNSET.value
            )
        }
    }

    override suspend fun saveBiometricsState(state: SecurityCheckState) {
        application.dataStore.edit {
            it[PreferenceKeys.BIOMETRICS_STATE] = state.value
        }
    }

    override fun readPassportCardLook(): Flow<PassportCardLook> {
        return application.dataStore.data.map {
            PassportCardLook.fromInt(
                it[PreferenceKeys.PASSPORT_CARD_LOOK] ?: PassportCardLook.WHITE.value
            )
        }
    }

    override suspend fun savePassportCardLook(look: PassportCardLook) {
        application.dataStore.edit {
            it[PreferenceKeys.PASSPORT_CARD_LOOK] = look.value
        }
    }

    override fun readIsPassportIncognitoMode(): Flow<Boolean> {
        return application.dataStore.data.map {
            it[PreferenceKeys.IS_PASSPORT_INCOGNITO_MODE] ?: true
        }
    }

    override suspend fun saveIsPassportIncognitoMode(isIncognito: Boolean) {
        application.dataStore.edit {
            it[PreferenceKeys.IS_PASSPORT_INCOGNITO_MODE] = isIncognito
        }
    }

    override fun readColorScheme(): Flow<AppColorScheme> {
        return application.dataStore.data.map {
            AppColorScheme.fromInt(
                it[PreferenceKeys.COLOR_SCHEME] ?: AppColorScheme.SYSTEM.value
            )
        }
    }

    override suspend fun saveColorScheme(scheme: AppColorScheme) {
        application.dataStore.edit {
            it[PreferenceKeys.COLOR_SCHEME] = scheme.value
        }
    }

    override fun readLanguage(): Flow<AppLanguage> {
        return application.dataStore.data.map {
            AppLanguage.fromString(
                it[PreferenceKeys.LANGUAGE] ?: AppLanguage.ENGLISH.value
            )
        }
    }

    override suspend fun saveLanguage(language: AppLanguage) {
        application.dataStore.edit {
            it[PreferenceKeys.LANGUAGE] = language.value
        }
    }

    override fun readWalletBalance(): Flow<Double> {
        return application.dataStore.data.map {
            it[PreferenceKeys.WALLET_BALANCE] ?: 0.0
        }
    }

    override suspend fun saveWalletBalance(balance: Double) {
        application.dataStore.edit {
            it[PreferenceKeys.WALLET_BALANCE] = balance
        }
    }

}