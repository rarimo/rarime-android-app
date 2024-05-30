package com.distributedLab.rarime.modules.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.modules.common.SecurityManager
import com.distributedLab.rarime.modules.common.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    securityManager: SecurityManager,
    settingsManager: SettingsManager,
) : ViewModel() {
    var isIntroFinished = mutableStateOf(dataStoreManager.readIsIntroFinished())
        private set

    var isScreenLocked = securityManager.isScreenLocked
    var biometricsState = securityManager.biometricsState
    var passcodeState = securityManager.passcodeState

    var colorScheme = settingsManager.colorScheme


    fun finishIntro() {
        isIntroFinished.value = true
        dataStoreManager.saveIsIntroFinished(true)
    }
}