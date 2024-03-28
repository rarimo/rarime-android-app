package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState

@Composable
fun RepeatPasscodeScreen(onNext: () -> Unit) {
    val repeatPasscodeState = rememberAppTextFieldState("")
    val passcodesMismatchError = stringResource(R.string.passcodes_mismatch_error)

    PasscodeScreenLayout(
        title = stringResource(R.string.repeat_passcode_title),
        passcodeState = repeatPasscodeState,
        onPasscodeFilled = {
            // TODO: Check if passcodes match
            if (repeatPasscodeState.text == "1234") {
                onNext()
            } else {
                repeatPasscodeState.updateErrorMessage(passcodesMismatchError)
            }
        }
    )
}

@Preview
@Composable
private fun RepeatPasscodeScreenPreview() {
    RepeatPasscodeScreen(onNext = {})
}