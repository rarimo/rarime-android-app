package com.rarilabs.rarime.manager

import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.data.enums.AppLanguage
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) {
    private var _colorScheme = MutableStateFlow(dataStoreManager.readColorScheme())

    val colorScheme: StateFlow<AppColorScheme>
        get() = _colorScheme.asStateFlow()

    private val _language = MutableStateFlow(dataStoreManager.readLanguage())

    val language: StateFlow<AppLanguage>
        get() = _language.asStateFlow()


    fun updateColorScheme(newScheme: AppColorScheme) {
        _colorScheme.value = newScheme
        dataStoreManager.saveColorScheme(newScheme)
    }

    fun updateLanguage(language: AppLanguage) {
        this._language.value = language
        dataStoreManager.saveLanguage(language)
    }
}