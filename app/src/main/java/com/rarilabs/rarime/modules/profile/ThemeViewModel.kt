package com.rarilabs.rarime.modules.profile

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.manager.SettingsManager
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