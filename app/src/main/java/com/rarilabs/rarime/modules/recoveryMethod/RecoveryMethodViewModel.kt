package com.rarilabs.rarime.modules.recoveryMethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.rarilabs.rarime.manager.DriveBackupManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RecoveryMethodViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val identityManager: IdentityManager,
    private val driveBackupManager: DriveBackupManager,
) : ViewModel() {
    val colorScheme = settingsManager.colorScheme


    val privateKey = identityManager.privateKey

    val driveState = driveBackupManager.driveState

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
        task: Task<GoogleSignInAccount>,
        onError: (e: Exception) -> Unit
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
                throw e
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
                throw e
            } finally {
                _isDriveButtonEnabled.value = true
            }
        }
    }
}