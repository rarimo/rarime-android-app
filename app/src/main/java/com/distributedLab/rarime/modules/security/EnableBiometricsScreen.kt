package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.R

@Composable
fun EnableBiometricsScreen(onNext: () -> Unit) {
    EnableScreenLayout(
        title = "Enable\nBiometrics",
        text = "Enable Fingerprint Authentication",
        icon = R.drawable.ic_fingerprint,
        // TODO: Enable fingerprint authentication
        onEnable = onNext,
        onSkip = onNext
    )
}

@Preview
@Composable
private fun EnableBiometricsScreenPreview() {
    EnableBiometricsScreen(onNext = {})
}