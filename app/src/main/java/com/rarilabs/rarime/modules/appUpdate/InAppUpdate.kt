package com.rarilabs.rarime.modules.appUpdate

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.rarilabs.rarime.util.ErrorHandler

@Composable
fun InAppUpdate(activity: ComponentActivity) {
    val appUpdateManager = remember { AppUpdateManagerFactory.create(activity) }
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    val updateResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Activity.RESULT_CANCELED
            ErrorHandler.logDebug("Update", "Ok")
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            activity.finish()
        }
    }

    LaunchedEffect(Unit) {
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE || appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS || appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                        .setAllowAssetPackDeletion(true).build()
                )
            }
        }

        appUpdateInfoTask.addOnCanceledListener {
            activity.recreate()
        }
    }
}