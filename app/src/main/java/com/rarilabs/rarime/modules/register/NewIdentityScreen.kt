package com.rarilabs.rarime.modules.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppTextField
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.InfoAlert
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.components.rememberAppTextFieldState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.isKeyValid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.web3j.utils.Numeric
import java.math.BigInteger
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

    val savedPrivateKey = newIdentityViewModel.savedPrivateKey.collectAsState()

    var isSubmitting by remember { mutableStateOf(false) }

    val privateKey by remember {
        mutableStateOf(newIdentityViewModel.genPrivateKey())
    }

    val invitationCodeState = rememberAppTextFieldState(initialText = "")

    fun savePrivateKey(pk: String) {
        newIdentityViewModel.identityManager.savePrivateKey(pk);
    }

    fun finishOnboarding(code: String) {
        scope.launch {
            try {
//                newIdentityViewModel.createBalance(code)

                onNext.invoke()
            } catch (e: Exception) {
                ErrorHandler.logError("finishOnboarding", e.toString(), e)

//                if (e is InvitationNotExistException) {
//                    invitationCodeState.updateErrorMessage(
//                        context.getString(R.string.create_identity_referral_code_not_exist_msg)
//                    )
//                    isSubmitting = false
//                } else if (e is InvitationUsedException) {
//                    onNext.invoke()
//                } else {
//                    invitationCodeState.updateErrorMessage(
//                        context.getString(R.string.create_identity_referral_code_invalid_msg)
//                    )
//                    isSubmitting = false
//                }
            }
        }
    }

    suspend fun handleInitPK(pk: String) {
        isSubmitting = true



        if (savedPrivateKey.value.isNullOrEmpty()) {
            savePrivateKey(pk)
        }else {
            savePrivateKey(savedPrivateKey.value!!)
        }

        if (invitationCodeState.text.isEmpty()) {
            finishOnboarding("")
        } else {
            finishOnboarding(invitationCodeState.text)
        }

        delay(1000)
        mainViewModel.tryLogin()
        delay(1000)


    }

    NewIdentityScreenContent(
        onBack = onBack,
        privateKey = privateKey,
        isImporting = isImporting,
        handleInitPK = { scope.launch { handleInitPK(it) } },
        isSubmitting = isSubmitting,
    )
}


@Composable
fun NewIdentityScreenContent(
    onBack: () -> Unit,
    isImporting: Boolean = false,
    isSubmitting: Boolean = false,
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
        if (!isKeyValid(BigInteger(Numeric.hexStringToByteArray(pk)))){
            return false
        }
        return (pk.length == 32 || pk.length == 64)
    }

    fun initPrivateKey() {
        val pkToSave = if (isImporting) privateKeyFieldState.text else privateKey

        if (!isPKValid(pkToSave)) {
            privateKeyFieldState.updateErrorMessage("Invalid private key format")

            return
        }

        handleInitPK(pkToSave)
    }

    IdentityStepLayout(
        onBack = onBack,
        title = stringResource(R.string.new_identity_title),
        nextButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                PrimaryButton(modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.Large,
                    text = stringResource(
                        if (isImporting) R.string.create_identity_import_btn
                        else R.string.create_identity_continue_btn
                    ),
                    enabled = !isSubmitting,
                    onClick = { initPrivateKey() }
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CardContainer {
                Column {
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
                                    size = ButtonSize.Large,
                                    leftIcon = if (isCopied) R.drawable.ic_check else R.drawable.ic_copy_simple,
                                    text = (if (isCopied) {
                                        stringResource(R.string.create_identity_copied_msg)
                                    } else {
                                        stringResource(R.string.create_identity_copy_btn)
                                    }).uppercase(),
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(privateKey))
                                        isCopied = true
                                    })
                            }
                        }
                    }

                    if (!isImporting) {
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(20.dp))
                        InfoAlert(text = stringResource(R.string.create_identity_warning))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun NewIdentityScreenContentPreview() {
    NewIdentityScreenContent(
        onBack = {},
        privateKey = "324523h423grewadisabudbawiudawwafa",
        handleInitPK = {},
    )
}

@Preview
@Composable
private fun NewIdentityScreenContentImportingPreview() {
    NewIdentityScreenContent(
        onBack = {},
        isImporting = true,
        privateKey = "324523h423grewadisabudbawiudawwafa",
        handleInitPK = {},
    )
}