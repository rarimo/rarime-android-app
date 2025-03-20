package com.rarilabs.rarime.modules.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.register.DriveBackup
import com.rarilabs.rarime.modules.register.DriveBackupSkeleton
import com.rarilabs.rarime.modules.register.DriveState
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.InfoAlert
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun ExportKeysScreen(
    onBack: () -> Unit, viewModel: ExportKeysViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mainViewModel = LocalMainViewModel.current
    val privateKey by viewModel.privateKey.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val driveState by viewModel.driveState.collectAsState()
    val isDriveButtonEnabled by viewModel.isDriveButtonEnabled.collectAsState()
    var isCopied by remember { mutableStateOf(false) }
    val isInit by viewModel.isInit.collectAsState()

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(3.seconds)
            isCopied = false
        }
    }

    val scope = rememberCoroutineScope()

    val signInErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error, message = stringResource(
            R.string.drive_error_cant_sign_in_google_identity_account
        )
    )
    val backupErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error, message = stringResource(
            R.string.drive_error_cant_back_up_your_private_key
        )
    )
    val deleteErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error, message = "Cannot delete backup"
    )

    val googleSignInClient = remember(context) {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
                //.requestIdToken(BaseConfig.GOOGLE_WEB_KEY)
                .build()
        )
    }


    val signInResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleSignInResult(task, context) {
                scope.launch {
                    mainViewModel.showSnackbar(signInErrorOptions)
                }
            }
        }

    val setIsCopied: (Boolean) -> Unit = remember { { isCopied = it } }
    val setTextToClipBoard: (AnnotatedString) -> Unit = remember(clipboardManager) {
        { clipboardManager.setText(it) }
    }

    val signIn: () -> Unit = remember(googleSignInClient, signInResultLauncher) {
        { signInResultLauncher.launch(googleSignInClient.signInIntent) }
    }

    LaunchedEffect(Unit) {
        viewModel.userRecoverableAuthException.collect { exception ->
            signInResultLauncher.launch(exception.intent)
        }
    }

    val backUp: () -> Unit = remember(context, viewModel, mainViewModel) {
        {
            scope.launch {
                try {
                    viewModel.backupPrivateKey(context)
                } catch (e: Exception) {
                    mainViewModel.showSnackbar(backupErrorOptions)
                }
            }
        }
    }

    val delete: () -> Unit = remember(context, viewModel, mainViewModel) {
        {
            scope.launch {
                try {
                    viewModel.deleteBackup(context)
                } catch (e: Exception) {
                    mainViewModel.showSnackbar(deleteErrorOptions)
                }
            }
        }
    }


    ExportKeysContent(
        onBack = onBack,
        privateKey = privateKey!!,
        isCopied = isCopied,
        setIsCopied = setIsCopied,
        setTextToClipBoard = setTextToClipBoard,
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
    isCopied: Boolean = false,
    setIsCopied: (Boolean) -> Unit,
    setTextToClipBoard: (pk: AnnotatedString) -> Unit,
    driveState: DriveState,
    isDriveButtonEnabled: Boolean,
    isInit: Boolean = false,
    signIn: () -> Unit,
    backUp: () -> Unit,
    delete: () -> Unit
) {
    ProfileRouteLayout(
        title = stringResource(R.string.export_keys), onBack = onBack
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    privateKey.let {
                        Text(
                            text = it,
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textPrimary,
                            modifier = Modifier
                                .background(
                                    RarimeTheme.colors.componentPrimary, RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 14.dp, horizontal = 16.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PrimaryTextButton(leftIcon = if (isCopied) R.drawable.ic_check else R.drawable.ic_copy_simple,
                                text = if (isCopied) {
                                    stringResource(R.string.copied_text)
                                } else {
                                    stringResource(R.string.copy_to_clipboard_btn)
                                },
                                onClick = {
                                    setTextToClipBoard(AnnotatedString(it))
                                    setIsCopied(true)
                                })
                        }
                        HorizontalDivider()
                        InfoAlert(text = stringResource(R.string.create_identity_warning))
                    }
                }
            }

            if (isInit) {
                DriveBackup(
                    state = driveState,
                    backUp = backUp,
                    delete = delete,
                    sigIn = signIn,
                    isDriveButtonEnabled = isDriveButtonEnabled
                )
            } else {
                DriveBackupSkeleton()
            }
        }
    }
}


@Preview
@Composable
private fun ExportKeysScreenPreview() {
    ExportKeysContent(onBack = {},
        privateKey = "adasdladalkawl;dklawkadakdl;wdl;,al;wd,law,l;d,awl;d,dl;aw",
        isCopied = false,
        setIsCopied = {},
        setTextToClipBoard = {},
        driveState = DriveState.NOT_SIGNED_IN,
        isDriveButtonEnabled = false,
        isInit = true,
        {},
        {},
        {})
}
