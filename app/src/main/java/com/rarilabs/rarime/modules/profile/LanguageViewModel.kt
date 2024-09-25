package com.rarilabs.rarime.modules.profile

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.enums.AppLanguage
import com.rarilabs.rarime.manager.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val settingsManager: SettingsManager
): ViewModel() {
    val language = settingsManager.language
    fun updateLanguage(appLanguage: AppLanguage) {
        settingsManager.updateLanguage(appLanguage)
    }

}