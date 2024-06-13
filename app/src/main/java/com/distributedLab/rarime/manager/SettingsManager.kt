package com.distributedLab.rarime.manager

import androidx.compose.runtime.mutableStateOf
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.store.SecureSharedPrefsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) {
    var colorScheme = mutableStateOf(dataStoreManager.readColorScheme())
        private set
    var language = mutableStateOf(dataStoreManager.readLanguage())
        private set

    fun updateColorScheme(newScheme: AppColorScheme) {
        colorScheme.value = newScheme
        dataStoreManager.saveColorScheme(newScheme)
    }

    fun updateLanguage(language: AppLanguage) {
        this.language.value = language
        dataStoreManager.saveLanguage(language)
    }
}