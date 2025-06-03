package com.rarilabs.rarime.manager

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.rarilabs.rarime.R
import kotlinx.coroutines.CoroutineScope
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
import javax.inject.Singleton

enum class DriveState {
    BACKED_UP, NOT_BACKED_UP, PKS_ARE_NOT_EQUAL, NOT_SIGNED_IN,
}

@Singleton
class DriveBackupManager @Inject constructor(
    private val context: Context
) {
    private val _signedInAccount = MutableStateFlow<GoogleSignInAccount?>(null)
    val signedInAccount: StateFlow<GoogleSignInAccount?> = _signedInAccount.asStateFlow()

    private val _userRecoverableAuthException = MutableSharedFlow<UserRecoverableAuthIOException>()
    val userRecoverableAuthException = _userRecoverableAuthException.asSharedFlow()

    private val _driveState = MutableStateFlow<DriveState>(DriveState.NOT_SIGNED_IN)
    val driveState: StateFlow<DriveState> = _driveState.asStateFlow()

    suspend fun checkIfUserIsSignedIn(): Boolean {
        val lastAccount = GoogleSignIn.getLastSignedInAccount(context)
        setSignedInAccount(lastAccount)
        return lastAccount != null
    }

    fun setSignedInAccount(account: GoogleSignInAccount?) {
        _signedInAccount.value = account
        CoroutineScope(Dispatchers.IO).launch {
            updateDriveState()
        }
    }

    private suspend fun updateDriveState() {
        val account = _signedInAccount.value
        if (account == null) {
            _driveState.value = DriveState.NOT_SIGNED_IN
            return
        }

        val restoredKey: String? = try {
            restorePrivateKey(account)
        } catch (e: IOException) {
            null
        }

        _driveState.value = when {
            restoredKey.isNullOrEmpty() -> DriveState.NOT_BACKED_UP
            else -> {
                DriveState.PKS_ARE_NOT_EQUAL
            }
        }
    }

    private suspend fun restorePrivateKey(account: GoogleSignInAccount): String? =
        withContext(Dispatchers.IO) {
            try {
                val driveService = getDriveService(account)
                val result = driveService.files().list().setQ("name = 'private_key.txt'")
                    .setSpaces("appDataFolder").setFields("files(id, name)").execute()

                val file = result.files.firstOrNull() ?: return@withContext null
                val inputStream = driveService.files().get(file.id).executeMediaAsInputStream()

                InputStreamReader(inputStream).use { reader -> reader.readText() }
            } catch (e: IOException) {
                null
            }
        }

    suspend fun backupPrivateKey(account: GoogleSignInAccount, privateKey: String) {
        withContext(Dispatchers.IO) {
            try {
                val driveService = getDriveService(account)
                val fileMetadata = File().apply {
                    name = "private_key.txt"
                    parents = listOf("appDataFolder")
                }
                val contentStream = ByteArrayContent("text/plain", privateKey.toByteArray())

                driveService.files().create(fileMetadata, contentStream).setFields("id").execute()

                _driveState.value = DriveState.BACKED_UP
            } catch (e: UserRecoverableAuthIOException) {
                _userRecoverableAuthException.emit(e)
            } catch (e: IOException) {
                throw e
            }
        }
    }

    suspend fun deleteBackup(account: GoogleSignInAccount) {
        withContext(Dispatchers.IO) {
            val driveService = getDriveService(account)
            val result = driveService.files().list().setQ("name = 'private_key.txt'")
                .setSpaces("appDataFolder").setFields("files(id)").execute()

            for (file in result.files) {
                driveService.files().delete(file.id).execute()
            }
            _driveState.value = DriveState.NOT_BACKED_UP
        }
    }

    private fun getDriveService(account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_APPDATA)
        ).setSelectedAccount(account.account)

        val transport = GoogleNetHttpTransport.newTrustedTransport()
        return Drive.Builder(
            transport, GsonFactory(), credential
        ).setApplicationName(context.getString(R.string.app_name)).build()
    }
}