package com.rarilabs.rarime.modules.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.store.room.notifications.NotificationsRepository
import com.rarilabs.rarime.store.room.voting.VotingRepository
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val settingsManager: SettingsManager,
    val walletManager: WalletManager,
    val identityManager: IdentityManager,
    val passportManager: PassportManager,
    val dataStoreManager: SecureSharedPrefsManager,
    val notificationsRepository: NotificationsRepository,
    val votingRepository: VotingRepository
) : ViewModel() {
    val rarimoAddress = identityManager.rarimoAddress()

    val language = settingsManager.language
    val colorScheme = settingsManager.colorScheme

    fun getImage(): Bitmap? {
        val passport = passportManager.passport.value

        return passport?.personDetails?.getPortraitImage()
    }

    suspend fun clearAllData(context: Context) {
        dataStoreManager.clearAllData()

        notificationsRepository.deleteAllNotifications()
        votingRepository.deleteAllVoting()

        delay(1000L)

        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component

        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    fun getDecryptedFeedbackFile(): File {
        val pointsNullifier = identityManager.getUserPointsNullifier()

        ErrorHandler.logDebug("sendFeedback", "pointsNullifier: $pointsNullifier")

        val logFile = ErrorHandler.getLogFile()

        return logFile
    }
}