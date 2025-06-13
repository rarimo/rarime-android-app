package com.rarilabs.rarime.modules.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.DriveState
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.recoveryMethod.RecoveryMethodDetailScreen
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import kotlinx.coroutines.launch

@Composable
fun ExportKeysScreen(
    onBack: () -> Unit, viewModel: ExportKeysViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mainViewModel = LocalMainViewModel.current
    val privateKey by viewModel.privateKey.collectAsState()
    val driveState by viewModel.driveState.collectAsState()
    val isDriveButtonEnabled by viewModel.isDriveButtonEnabled.collectAsState()
    val isInit by viewModel.isInit.collectAsState()


    val scope = rememberCoroutineScope()

    val signInErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error,
        message = stringResource(R.string.drive_error_cant_sign_in_google_identity_account)
    )
    val backupErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error,
        message = stringResource(R.string.drive_error_cant_back_up_your_private_key)
    )
    val deleteErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error, message = "Cannot delete backup"
    )

    val googleSignInClient = remember(context) {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA)).build()
        )
    }

    val signInResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleSignInResult(task) { error ->
                scope.launch { mainViewModel.showSnackbar(signInErrorOptions) }
            }
        }


    val signIn: () -> Unit = remember(googleSignInClient, signInResultLauncher) {
        { signInResultLauncher.launch(googleSignInClient.signInIntent) }
    }

    LaunchedEffect(Unit) {
        viewModel.userRecoverableAuthException.collect { exception ->
            signInResultLauncher.launch(exception.intent)
        }
    }

    val backUp: () -> Unit = remember {
        {
            scope.launch {
                try {
                    viewModel.backupPrivateKey()
                } catch (e: Exception) {
                    mainViewModel.showSnackbar(backupErrorOptions)
                }
            }
        }
    }

    val delete: () -> Unit = remember {
        {
            scope.launch {
                try {
                    viewModel.deleteBackup()
                } catch (e: Exception) {
                    mainViewModel.showSnackbar(deleteErrorOptions)
                }
            }
        }
    }

    ExportKeysContent(
        onBack = onBack,
        privateKey = privateKey!!,
        driveState = driveState,
        isDriveButtonEnabled = isDriveButtonEnabled,
        isInit = isInit,
        signIn = signIn,
        backUp = backUp,
        delete = delete
    )
}


@Composable
fun ExportKeysContent(
    onBack: () -> Unit,
    privateKey: String,
    driveState: DriveState,
    isDriveButtonEnabled: Boolean,
    isInit: Boolean,
    signIn: () -> Unit,
    backUp: () -> Unit,
    delete: () -> Unit
) {

    ProfileRouteLayout(
        title = stringResource(R.string.recovery_method_details_screen_title),
        paddingHorizontal = 0.dp,
        onBack = onBack
    ) {

        RecoveryMethodDetailScreen(
            driveState = driveState,
            onClose = { onBack.invoke() },
            privateKey = privateKey,
            backupPrivateKey = backUp,
            isSwitchEnabled = isDriveButtonEnabled,
            deleteBackup = delete,
            signIn = {
                signIn.invoke()
            },
            isInit = isInit,
            isHeaderEnabled = false
        )
    }
}


@Preview
@Composable
private fun ExportKeysScreenPreview() {
    Surface {
        ExportKeysContent(
            onBack = {},
            privateKey = "adasdladalkawl;dklawkadakdl;wdl;,al;wd,law,l;d,awl;d,dl;aw",
            driveState = DriveState.NOT_SIGNED_IN,
            isDriveButtonEnabled = false,
            isInit = true,
            {},
            {},
            {})
    }

}
