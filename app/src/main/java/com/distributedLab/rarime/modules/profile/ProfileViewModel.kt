package com.distributedLab.rarime.modules.profile

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.SettingsManager
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val settingsManager: SettingsManager, val walletManager: WalletManager
) : ViewModel() {
    val address = walletManager.rarimoAddress


    val language = settingsManager.language
    val colorScheme = settingsManager.colorScheme


}