package com.rarilabs.rarime.modules.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.rarilabs.rarime.manager.DriveBackupManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.store.room.notifications.NotificationsRepository
import com.rarilabs.rarime.store.room.transactons.TransactionRepository
import com.rarilabs.rarime.store.room.voting.VotingRepository
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val settingsManager: SettingsManager,
    val walletManager: WalletManager,
    val identityManager: IdentityManager,
    val passportManager: PassportManager,
    val dataStoreManager: SecureSharedPrefsManager,
    private val driveBackupManager: DriveBackupManager,
    private val notificationsRepository: NotificationsRepository,
    private val votingRepository: VotingRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {


    val evmAddress = identityManager.evmAddress()
    val privateKey = identityManager.privateKey

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
        transactionRepository.deleteAll()

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


    private val signedInAccount = driveBackupManager.signedInAccount

    private val _isInit = MutableStateFlow(false)
    val isInit: StateFlow<Boolean> = _isInit.asStateFlow()


    private val _isDriveButtonEnabled = MutableStateFlow(true)
    val isDriveButtonEnabled: StateFlow<Boolean> = _isDriveButtonEnabled.asStateFlow()


    init {
        viewModelScope.launch {
            _isInit.value = false
            driveBackupManager.checkIfUserIsSignedIn()
            _isInit.value = true
        }
    }


    fun handleSignInResult(
        task: Task<GoogleSignInAccount>, onError: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    driveBackupManager.setSignedInAccount(account)
                }
            } catch (e: ApiException) {
                onError(e)
                ErrorHandler.logError("handleSignInResult", "Failed to sign in", e)
            }
        }
    }

    fun backupPrivateKey() {
        val account = signedInAccount.value ?: return
        val pk = privateKey.value ?: return

        viewModelScope.launch {
            _isDriveButtonEnabled.value = false
            try {
                driveBackupManager.backupPrivateKey(account, pk)
            } catch (e: UserRecoverableAuthIOException) {

            } catch (e: IOException) {
                ErrorHandler.logError("backupPrivateKey", "Cannot back up private key", e)
            } finally {
                _isDriveButtonEnabled.value = true
            }
        }
    }

    fun deleteBackup() {
        val account = signedInAccount.value ?: return

        viewModelScope.launch {
            _isDriveButtonEnabled.value = false
            try {
                driveBackupManager.deleteBackup(account)
            } catch (e: IOException) {
                ErrorHandler.logError("deleteBackup", "Cannot delete backup", e)
            } finally {
                _isDriveButtonEnabled.value = true
            }
        }
    }
}