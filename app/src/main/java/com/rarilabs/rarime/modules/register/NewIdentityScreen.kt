package com.rarilabs.rarime.modules.register

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.services.drive.DriveScopes
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppTextField
import com.rarilabs.rarime.ui.components.AppTextFieldState
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.InfoAlert
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import com.rarilabs.rarime.ui.components.rememberAppTextFieldState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.isKeyValid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.web3j.utils.Numeric
import kotlin.time.Duration.Companion.seconds

@Composable
fun NewIdentityScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    isImporting: Boolean = false,
    newIdentityViewModel: NewIdentityViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val mainViewModel = LocalMainViewModel.current
    val savedPrivateKey by newIdentityViewModel.savedPrivateKey.collectAsState()
    var isSubmitting by remember { mutableStateOf(false) }

    val privateKey by remember {
        mutableStateOf(newIdentityViewModel.genPrivateKey())
    }

    var isDriveState by remember { mutableStateOf(isImporting) }
    var isDriveButtonEnabled by remember { mutableStateOf(true) }

    val signInErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error, message = stringResource(
            R.string.drive_error_cant_sign_in_google_identity_account
        )
    )
    val restoreErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error, message = stringResource(
            R.string.drive_error_you_dont_have_restored_private_key
        )
    )
    val backUpErrorOptions = getSnackbarDefaultShowOptions(
        severity = SnackbarSeverity.Error, message = stringResource(
            R.string.drive_error_cant_back_up_your_private_key
        )
    )


    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(com.google.android.gms.common.api.Scope(DriveScopes.DRIVE_APPDATA))
                .build()
        )
    }

    val signInResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            scope.launch {
                handleSignInResult(task, newIdentityViewModel) {
                    mainViewModel.showSnackbar(signInErrorOptions)
                }
            }

        }

    val signedInAccount by newIdentityViewModel.signedInAccount.collectAsState()

    val invitationCodeState = rememberAppTextFieldState(initialText = "")


    fun savePrivateKey(pk: String) {
        newIdentityViewModel.identityManager.savePrivateKey(pk)
    }

    fun finishOnboarding(code: String) {
        onNext.invoke()
    }


    suspend fun restorePrivateKey() {
        scope.launch {
            try {
                isDriveButtonEnabled = false
                val driveService = newIdentityViewModel.getDriveService(signedInAccount!!, context)
                val pk = newIdentityViewModel.restorePrivateKey(driveService)
                    ?: throw IllegalStateException("No private key found")

                savePrivateKey(pk)
                mainViewModel.tryLogin()

                if (invitationCodeState.text.isEmpty()) {
                    finishOnboarding("")
                } else {
                    finishOnboarding(invitationCodeState.text)
                }

            } catch (e: Exception) {
                isDriveButtonEnabled = true
                ErrorHandler.logError("restorePrivateKey", "Cant restore private key", e)
                mainViewModel.showSnackbar(restoreErrorOptions)
            }
        }

    }

    suspend fun backUpPrivateKey() {
        scope.launch {
            try {
                isDriveButtonEnabled = false
                val pk = if (savedPrivateKey == null) {
                    val pk = newIdentityViewModel.genPrivateKey()
                    savePrivateKey(pk)
                    pk
                } else {
                    savedPrivateKey!!
                }

                val driveService = newIdentityViewModel.getDriveService(signedInAccount!!, context)
                newIdentityViewModel.backupPrivateKey(driveService, pk)


                mainViewModel.tryLogin()

                if (invitationCodeState.text.isEmpty()) {
                    finishOnboarding("")
                } else {
                    finishOnboarding(invitationCodeState.text)
                }
            } catch (e: Exception) {
                isDriveButtonEnabled = true
                ErrorHandler.logError("backUpPrivateKey", "Cant back up private key", e)
                mainViewModel.showSnackbar(backUpErrorOptions)
            }

        }
    }

    suspend fun handleInitPK(pk: String) {
        isSubmitting = true

        if (savedPrivateKey.isNullOrEmpty()) {
            savePrivateKey(pk)
        }

        //mainViewModel.tryLogin()

        if (invitationCodeState.text.isEmpty()) {
            finishOnboarding("")
        } else {
            finishOnboarding(invitationCodeState.text)
        }
    }

    if (isDriveState && isImporting) {
        RestoreScreen(
            onDriveRestore = { scope.launch { restorePrivateKey() } },
            signInAccount = signedInAccount,
            signIn = {
                signInResultLauncher.launch(
                    googleSignInClient.signInIntent
                )
            },
            isDriveButtonEnabled = isDriveButtonEnabled,
            onBack = onBack
        ) {
            isDriveState = false
        }
    } else {
        NewIdentityScreenContent(
            onBack = {
                // Allow user to go back to Drive only if recovering
                if (isImporting) isDriveState = true else onBack()
            },
            privateKey = privateKey,
            isImporting = isImporting,
            handleInitPK = { scope.launch { handleInitPK(it) } },
            isSubmitting = isSubmitting,
            invitationCodeState = invitationCodeState
        )
    }
}

private suspend fun handleSignInResult(
    completedTask: Task<GoogleSignInAccount>,
    viewModel: NewIdentityViewModel,
    onError: suspend () -> Unit
) {
    try {
        val account = completedTask.getResult(ApiException::class.java)
        if (account != null) {
            viewModel.setSignedInAccount(account)
        }
    } catch (e: ApiException) {
        onError()
        ErrorHandler.logError("handleSignInResult", "Failed to sign in", e)
    }
}

@Composable
fun NewIdentityScreenContent(
    onBack: () -> Unit,
    isImporting: Boolean = false,
    isSubmitting: Boolean = false,
    invitationCodeState: AppTextFieldState,
    privateKey: String,
    handleInitPK: (pk: String) -> Unit,
) {
    val privateKeyFieldState = rememberAppTextFieldState(initialText = "")

    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    if (isCopied) {
        LaunchedEffect(Unit) {
            delay(3.seconds)
            isCopied = false
        }
    }

    fun isPKValid(pk: String): Boolean {
        return pk.length == 32 || pk.length == 64
    }

    fun initPrivateKey() {
        val pkToSave = if (isImporting) privateKeyFieldState.text else privateKey


        if (!isPKValid(pkToSave)) {
            privateKeyFieldState.updateErrorMessage("Invalid private key format")
            Log.i("Here", "isPKValid(pkToSave)")
            return
        }

        if (!isKeyValid(Numeric.toBigInt(Numeric.hexStringToByteArray(pkToSave)))) {
            privateKeyFieldState.updateErrorMessage("Invalid private key format")
            Log.i("Here", "isKeyValid()")
            return
        }

        handleInitPK(pkToSave)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        IdentityStepLayout(
            onBack = onBack,
            title = if (isImporting) {
                stringResource(R.string.create_identity_import_title)
            } else {
                stringResource(R.string.create_identity_title)
            },
            nextButton = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    PrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        size = ButtonSize.Large,
                        text = if (isSubmitting) {
                            stringResource(R.string.create_identity_creating_btn)
                        } else {
                            stringResource(R.string.create_identity_continue_btn)
                        },
                        rightIcon = if (isSubmitting) null else R.drawable.ic_arrow_right,
                        enabled = !isSubmitting,
                        onClick = { initPrivateKey() }
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier.absolutePadding(top = 24.dp, left = 16.dp, right = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (isImporting) {
                        AppTextField(
                            enabled = !isSubmitting,
                            state = privateKeyFieldState,
                            placeholder = stringResource(R.string.create_identity_import_placeholder)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text(
                                text = privateKey,
                                style = RarimeTheme.typography.body3,
                                color = RarimeTheme.colors.textPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        RarimeTheme.colors.componentPrimary,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 14.dp, horizontal = 16.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                PrimaryTextButton(
                                    size = ButtonSize.Medium,
                                    leftIcon = if (isCopied) R.drawable.ic_check else R.drawable.ic_copy_simple,
                                    text = (if (isCopied) {
                                        stringResource(R.string.create_identity_copied_msg)
                                    } else {
                                        stringResource(R.string.create_identity_copy_btn)
                                    }),
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(privateKey))
                                        isCopied = true
                                    })
                            }
                        }
                    }

                    if (!isImporting) {
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(24.dp))

                        InfoAlert(text = stringResource(R.string.create_identity_warning))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewIdentityScreenContentPreview() {
    NewIdentityScreenContent(
        onBack = {},
        privateKey = "324523h423grewadisabudbawiudawwafa",
        handleInitPK = {},
        invitationCodeState = rememberAppTextFieldState(initialText = "")
    )
}

@Preview(showBackground = true)
@Composable
private fun NewIdentityScreenContentImportingPreview() {
    NewIdentityScreenContent(
        onBack = {},
        isImporting = true,
        privateKey = "324523h423grewadisabudbawiudawwafa",
        handleInitPK = {},
        invitationCodeState = rememberAppTextFieldState(initialText = "")
    )
}