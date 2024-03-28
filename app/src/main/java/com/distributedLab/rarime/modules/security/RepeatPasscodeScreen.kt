package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState

@Composable
fun RepeatPasscodeScreen(onNext: () -> Unit) {
    val repeatPasscodeState = rememberAppTextFieldState("")
    val passcodeToCheck = "1234"

    PasscodeScreenLayout(
        title = "Repeat Passcode",
        passcodeState = repeatPasscodeState,
        onPasscodeFilled = {
            if (repeatPasscodeState.text == passcodeToCheck) {
                onNext()
            } else {
                repeatPasscodeState.updateErrorMessage("Passcodes do not match")
            }
        }
    )
}

@Preview
@Composable
private fun RepeatPasscodeScreenPreview() {
    RepeatPasscodeScreen(onNext = {})
}