package com.rarilabs.rarime.earn

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.modules.manageWidgets.ManageWidgetsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

    @HiltViewModel
    class EarnViewModel @Inject constructor(
        private val settingsManager: SettingsManager,

        ) : ViewModel() {
            val colorScheme = settingsManager.colorScheme
}