package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.AppLanguage
import com.distributedLab.rarime.domain.manager.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    var colorScheme = mutableStateOf(AppColorScheme.SYSTEM)
        private set
    var language = mutableStateOf(AppLanguage.ENGLISH)
        private set

    init {
        viewModelScope.launch {
            colorScheme.value = dataStoreManager.readColorScheme().first()
            language.value = dataStoreManager.readLanguage().first()
        }
    }

    fun updateColorScheme(newScheme: AppColorScheme) {
        colorScheme.value = newScheme
        viewModelScope.launch {
            dataStoreManager.saveColorScheme(newScheme)
        }
    }

    fun updateLanguage(language: AppLanguage) {
        this.language.value = language
        viewModelScope.launch {
            dataStoreManager.saveLanguage(language)
        }
    }
}