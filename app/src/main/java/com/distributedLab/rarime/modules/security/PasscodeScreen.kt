package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.ui.components.AppAlertDialog

@Composable
fun PasscodeScreen(
    passcode: String = "",
    passcodeState: SecurityCheckState,
    onPasscodeChange: (String) -> Unit = {},
    onPasscodeStateChange: (SecurityCheckState) -> Unit,
    onClose: () -> Unit
) {
    var isAlertVisible by remember { mutableStateOf(false) }
    var isRepeatingPasscode by remember { mutableStateOf(false) }
    var newPasscode by remember { mutableStateOf("") }

    fun verifyPasscode() {
        if (newPasscode != passcode) {
            isAlertVisible = true
        } else {
            onPasscodeStateChange(SecurityCheckState.DISABLED)
        }
    }

    fun handleEnterPasscode(value: String) {
        newPasscode = value
        if (passcodeState == SecurityCheckState.ENABLED) {
            verifyPasscode()
        } else {
            isRepeatingPasscode = true
        }
    }

    if (isAlertVisible) {
        AppAlertDialog(
            title = stringResource(R.string.invalid_passcode),
            text = stringResource(R.string.try_again_msg),
            confirmText = stringResource(R.string.try_again),
            onConfirm = {
                isAlertVisible = false
                newPasscode = ""
            },
            onDismiss = onClose
        )
    }

    if (isRepeatingPasscode) {
        RepeatPasscodeScreen(
            passcode = newPasscode,
            onNext = {
                onPasscodeStateChange(SecurityCheckState.ENABLED)
                onPasscodeChange(newPasscode)
            },
            onBack = {
                isRepeatingPasscode = false
                newPasscode = ""
            },
            onClose = { onClose() }
        )
    } else {
        EnterPasscodeScreen(
            passcode = newPasscode,
            onNext = { handleEnterPasscode(it) },
            onBack = { onClose() }
        )
    }
}

@Preview
@Composable
private fun PasscodeScreenPreview() {
    PasscodeScreen(
        passcode = "1234",
        passcodeState = SecurityCheckState.DISABLED,
        onPasscodeChange = {},
        onPasscodeStateChange = {},
        onClose = {}
    )
}
