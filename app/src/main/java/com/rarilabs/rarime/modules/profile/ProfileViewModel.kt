package com.rarilabs.rarime.modules.profile

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.SendErrorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val settingsManager: SettingsManager,
    val walletManager: WalletManager,
    val identityManager: IdentityManager,
    val passportManager: PassportManager,
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

    suspend fun sendFeedback(context: Context): File {

        val nullifier = identityManager.getUserPointsNullifier()

        val feedbackDetails = JSONObject()
        feedbackDetails.put("nullifier", nullifier)
        val activeIdentity = withContext(Dispatchers.IO) {
            passportManager.getPassportActiveIdentity()
        }
        feedbackDetails.put("activeIdentity", activeIdentity)
        return SendErrorUtil.saveErrorDetailsToFile("feedback.json",feedbackDetails.toString(), context)


    }
}