package com.rarilabs.rarime.modules.profile

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val settingsManager: SettingsManager,
    val walletManager: WalletManager,
    val identityManager: IdentityManager,
    val dataStoreManager: SecureSharedPrefsManager,
) : ViewModel() {
    val rarimoAddress = identityManager.rarimoAddress()


    val language = settingsManager.language
    val colorScheme = settingsManager.colorScheme

    suspend fun clearAllData(context: Context) {
        dataStoreManager.clearAllData()

        delay(1000)

        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component

        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}