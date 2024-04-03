package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState

@Composable
fun EnterPasscodeScreen(onNext: () -> Unit, onBack: () -> Unit) {
    val passcodeState = rememberAppTextFieldState("")

    PasscodeScreenLayout(
        title = stringResource(R.string.enter_passcode_title),
        passcodeState = passcodeState,
        onPasscodeFilled = onNext,
        onClose = onBack
    )
}

@Preview
@Composable
private fun EnterPasscodeScreenPreview() {
    EnterPasscodeScreen(onNext = {}, onBack = {})
}