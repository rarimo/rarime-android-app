package com.rarilabs.rarime.modules.recoveryMethod

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecoveryMethodViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {
    val colorScheme = settingsManager.colorScheme
}