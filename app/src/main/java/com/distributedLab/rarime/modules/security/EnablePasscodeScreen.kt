package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.R

@Composable
fun EnablePasscodeScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    EnableScreenLayout(
        title = "Enable\nPasscode",
        text = "Enable Passcode Authentication",
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