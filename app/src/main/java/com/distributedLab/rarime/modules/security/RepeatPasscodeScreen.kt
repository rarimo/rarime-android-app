package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppAlertDialog
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState

@Composable
fun RepeatPasscodeScreen(
    passcode: String,
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
            if (repeatPasscodeState.text == passcode) {
                onNext()
            } else {
                isAlertVisible = true
            }
        },
        onClose = onClose
    )

    if (isAlertVisible) {
        AppAlertDialog(
            title = stringResource(R.string.invalid_passcode),
            text = stringResource(R.string.passcodes_mismatch_error),
            confirmText = stringResource(R.string.try_again),
            onConfirm = { handleAlertDismiss() },
            onDismiss = { handleAlertDismiss() },
        )
    }
}

@Preview
@Composable
private fun RepeatPasscodeScreenPreview() {
    RepeatPasscodeScreen(passcode = "1234", onNext = {}, onBack = {}, onClose = {})
}