package com.rarilabs.rarime.modules.register

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class NewIdentityViewModel @Inject constructor(
    val identityManager: IdentityManager,
    val pointsManager: PointsManager,
    val walletManager: WalletManager
) : ViewModel() {
    val savedPrivateKey = identityManager.privateKey


    private var _signedInAccount = MutableStateFlow<GoogleSignInAccount?>(null)

    val signedInAccount = _signedInAccount.asStateFlow()


    fun setSignedInAccount(account: GoogleSignInAccount) {
        _signedInAccount.value = account
    }

    fun getDriveService(account: GoogleSignInAccount, context: Context): Drive {
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

    suspend fun backupPrivateKey(driveService: Drive, privateKey: String) {
        withContext(Dispatchers.IO) {
            try {
                val fileMetadata = File().apply {
                    name = "private_key.txt"
                    parents = listOf("appDataFolder") // Specify appDataFolder as the parent
                }
                val contentStream = ByteArrayContent(
                    "text/plain",
                    privateKey.toByteArray()
                )
                val file = driveService.files().create(fileMetadata, contentStream)
                    .setFields("id")
                    .execute()


                Log.d("DriveService", "File ID: ${file.id}")
            } catch (e: IOException) {
                ErrorHandler.logError("DriveService backupPrivateKey", "An error occurred:", e)
                throw e
            }
        }
    }

    suspend fun restorePrivateKey(driveService: Drive): String? {
        return withContext(Dispatchers.IO) {
            try {
                val result: FileList = driveService.files().list()
                    .setQ("name = 'private_key.txt'")
                    .setSpaces("appDataFolder") // Search within the appDataFolder
                    .setFields("files(id, name)")
                    .execute()

                val file = result.files.firstOrNull()
                file?.let {
                    val inputStream = driveService.files().get(it.id).executeMediaAsInputStream()
                    val privateKey = InputStreamReader(inputStream).use { it.readText() }
                    Log.d("DriveService", "Restored Private Key: $privateKey")
                    privateKey
                }
            } catch (e: GoogleJsonResponseException) {
                Log.e("DriveService", "GoogleJsonResponseException: $e")
                null
            } catch (e: IOException) {
                ErrorHandler.logError("DriveService restorePrivateKey", "An error occurred: $e", e)
                null
            }
        }
    }

    fun genPrivateKey(): String {
        return identityManager.genPrivateKey()
    }

    suspend fun setReferralCodeIfUserHasPointsBalance() {
        val balance = pointsManager.getPointsBalance()


    }
    suspend fun createBalance(referralCode: String?) {
        pointsManager.createPointsBalance(referralCode)
        walletManager.loadBalances()
    }
}