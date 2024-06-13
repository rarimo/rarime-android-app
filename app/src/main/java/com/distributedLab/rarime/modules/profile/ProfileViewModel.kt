package com.distributedLab.rarime.modules.profile

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.manager.IdentityManager
import com.distributedLab.rarime.manager.SettingsManager
import com.distributedLab.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val settingsManager: SettingsManager,
    val walletManager: WalletManager,
    val identityManager: IdentityManager,
) : ViewModel() {
    val rarimoAddress = identityManager.rarimoAddress()


    val language = settingsManager.language
    val colorScheme = settingsManager.colorScheme


}