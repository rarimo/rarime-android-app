package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.R

@Composable
fun EnablePasscodeScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    EnableScreenLayout(
        title = stringResource(R.string.enable_passcode_title),
        text = stringResource(R.string.enable_passcode_text),
        icon = R.drawable.ic_password,
        onEnable = onNext,
        onSkip = onSkip
    )
}

@Preview
@Composable
private fun EnablePasscodeScreenPreview() {
    EnablePasscodeScreen(onNext = {}, onSkip = {})
}