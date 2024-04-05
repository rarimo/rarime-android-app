package com.distributedLab.rarime.modules.security

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun RepeatPasscodeScreen(
    onNext: () -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    val repeatPasscodeState = rememberAppTextFieldState("")
    var isAlertVisible by remember { mutableStateOf(false) }

    fun handleAlertDismiss() {
        isAlertVisible = false
        onBack()
    }

    PasscodeScreenLayout(
        title = stringResource(R.string.repeat_passcode_title),
        passcodeState = repeatPasscodeState,
        onPasscodeFilled = {
            // TODO: Check if passcodes match
            if (repeatPasscodeState.text == "1234") {
                onNext()
            } else {
                isAlertVisible = true
            }
        },
        onClose = onClose
    )

    if (isAlertVisible) {
        AlertDialog(
            containerColor = RarimeTheme.colors.backgroundPure,
            onDismissRequest = { handleAlertDismiss() },
            title = {
                Text(
                    text = stringResource(R.string.invalid_passcode),
                    style = RarimeTheme.typography.subtitle2,
                    color = RarimeTheme.colors.textPrimary
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.passcodes_mismatch_error),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textPrimary
                )
            },
            confirmButton = {
                PrimaryTextButton(
                    text = stringResource(R.string.try_again),
                    onClick = { handleAlertDismiss() }
                )
            }
        )
    }
}

@Preview
@Composable
private fun RepeatPasscodeScreenPreview() {
    RepeatPasscodeScreen(onNext = {}, onBack = {}, onClose = {})
}