package com.distributedLab.rarime.modules.profile

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.manager.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {
    fun onColorSchemeChange(scheme: AppColorScheme) {
        settingsManager.updateColorScheme(scheme)
    }

    val colorScheme = settingsManager.colorScheme
}