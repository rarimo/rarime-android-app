package com.rarilabs.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.rememberAppTextFieldState

@Composable
fun EnterPasscodeScreen(
    passcode: String = "",
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    val passcodeState = rememberAppTextFieldState("")

    LaunchedEffect(passcode) {
        passcodeState.updateText(passcode)
    }

    PasscodeScreenLayout(
        title = stringResource(R.string.enter_passcode_title),
        passcodeState = passcodeState,
        onPasscodeFilled = { onNext(passcodeState.text) },
        onClose = onBack
    )
}

@Preview
@Composable
private fun EnterPasscodeScreenPreview() {
    EnterPasscodeScreen(onNext = {}, onBack = {})
}