package com.rarilabs.rarime.modules.manageWidgets

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.HiddenPrizeManager
import com.rarilabs.rarime.manager.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ManageWidgetsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {
    val colorScheme = settingsManager.colorScheme
}
