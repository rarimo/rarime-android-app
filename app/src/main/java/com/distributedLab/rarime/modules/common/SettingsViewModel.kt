package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) : ViewModel() {
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