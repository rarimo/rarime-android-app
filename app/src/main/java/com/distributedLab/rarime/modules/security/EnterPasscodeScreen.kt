package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState

@Composable
fun EnterPasscodeScreen(onNext: () -> Unit) {
    val passcodeState = rememberAppTextFieldState("")

    PasscodeScreenLayout(
        title = "Enter Passcode",
        passcodeState = passcodeState,
        onPasscodeFilled = onNext
    )
}

@Preview
@Composable
private fun EnterPasscodeScreenPreview() {
    EnterPasscodeScreen(onNext = {})
}