package com.rarilabs.rarime.modules.profile

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.DriveState
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject


@HiltViewModel
class ExportKeysViewModel @Inject constructor(
    private val app: Application,
    private val identityManager: IdentityManager
) : AndroidViewModel(app) {

    val privateKey = identityManager.privateKey

    private val _signedInAccount = MutableStateFlow<GoogleSignInAccount?>(null)

    private val _userRecoverableAuthException = MutableSharedFlow<UserRecoverableAuthIOException>()
    val userRecoverableAuthException = _userRecoverableAuthException.asSharedFlow()


    private val _driveState = MutableStateFlow<DriveState>(DriveState.NOT_SIGNED_IN)
    val driveState: StateFlow<DriveState> = _driveState.asStateFlow()

    private val _isDriveButtonEnabled = MutableStateFlow(true)
    val isDriveButtonEnabled: StateFlow<Boolean> = _isDriveButtonEnabled.asStateFlow()

    private val _isInit = MutableStateFlow(false)

    val isInit: StateFlow<Boolean> = _isInit.asStateFlow()

    init {
        // Check if the user is already signed in
        viewModelScope.launch {
            _isInit.value = false
            checkIfUserIsSignedIn()
            _isInit.value = true
        }
    }

    private suspend fun checkIfUserIsSignedIn() {
        val context = app as Context
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (lastSignedInAccount != null) {
            _signedInAccount.value = lastSignedInAccount
            updateDriveState(context)
        }
    }

    private fun setSignedInAccount(account: GoogleSignInAccount, context: Context) {
        _signedInAccount.value = account
        viewModelScope.launch {
            updateDriveState(context)
        }
    }

    private suspend fun updateDriveState(context: Context) {
        val account = _signedInAccount.value
        if (account != null) {
            val driveService = getDriveService(account, context)
            val restoredKey = restorePrivateKey(driveService)
            _driveState.value = when (restoredKey) {
                null -> DriveState.NOT_BACKED_UP
                "" -> DriveState.NOT_BACKED_UP
                privateKey.value -> DriveState.BACKED_UP
                else -> DriveState.PKS_ARE_NOT_EQUAL
            }
        } else {
            _driveState.value = DriveState.NOT_SIGNED_IN
        }
    }

    private fun getDriveService(account: GoogleSignInAccount, context: Context): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_APPDATA)
        ).setSelectedAccount(account.account)

        val transport = GoogleNetHttpTransport.newTrustedTransport()

        return Drive.Builder(
            transport,
            GsonFactory(),
            credential
        ).setApplicationName(context.getString(R.string.app_name)).build()
    }

    suspend fun backupPrivateKey(context: Context) {
        val account = _signedInAccount.value ?: return
        val driveService = getDriveService(account, context)
        withContext(Dispatchers.IO) {
            try {
                _isDriveButtonEnabled.value = false
                val fileMetadata = File().apply {
                    name = "private_key.txt"
                    parents = listOf("appDataFolder")
                }
                val contentStream = ByteArrayContent(
                    "text/plain",
                    privateKey.value!!.toByteArray()
                )
                driveService.files().create(fileMetadata, contentStream)
                    .setFields("id")
                    .execute()
                _driveState.value = DriveState.BACKED_UP
            } catch (e: UserRecoverableAuthIOException) {
                ErrorHandler.logDebug("backupPrivateKey", "trying to recover google sign in")
                _userRecoverableAuthException.emit(e)
            } catch (e: IOException) {
                ErrorHandler.logError("backupPrivateKey", "Cannot back up private key", e)
                throw e
            } finally {
                _isDriveButtonEnabled.value = true
            }
        }
    }

    suspend fun retryBackupPrivateKey(context: Context) {
        backupPrivateKey(context)
    }

    suspend fun deleteBackup(context: Context) {
        val account = _signedInAccount.value ?: return
        val driveService = getDriveService(account, context)
        withContext(Dispatchers.IO) {
            try {
                _isDriveButtonEnabled.value = false
                val result = driveService.files().list()
                    .setQ("name = 'private_key.txt'")
                    .setSpaces("appDataFolder")
                    .setFields("files(id)")
                    .execute()

                for (file in result.files) {
                    driveService.files().delete(file.id).execute()
                }
                _driveState.value = DriveState.NOT_BACKED_UP
            } catch (e: IOException) {
                ErrorHandler.logError("deleteBackup", "Cannot delete backup", e)
                throw e
            } finally {
                _isDriveButtonEnabled.value = true
            }
        }
    }

    private suspend fun restorePrivateKey(driveService: Drive): String? {
        return withContext(Dispatchers.IO) {
            try {
                val result = driveService.files().list()
                    .setQ("name = 'private_key.txt'")
                    .setSpaces("appDataFolder")
                    .setFields("files(id, name)")
                    .execute()

                val file = result.files.firstOrNull()
                file?.let {
                    val inputStream = driveService.files().get(it.id).executeMediaAsInputStream()
                    InputStreamReader(inputStream).use { reader -> reader.readText() }
                }

            } catch (e: IOException) {
                ErrorHandler.logError("restorePrivateKey", "An error occurred", e)
                null
            }
        }
    }

    fun handleSignInResult(
        task: Task<GoogleSignInAccount>,
        context: Context,
        onError: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    setSignedInAccount(account, context)
                }
            } catch (e: ApiException) {
                onError(e)
                ErrorHandler.logError("handleSignInResult", "Failed to sign in", e)
            }
        }
    }
}