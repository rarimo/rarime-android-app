package com.distributedLab.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.R

@Composable
fun EnableBiometricsScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    EnableScreenLayout(
        title = stringResource(R.string.enable_biometrics_title),
        text = stringResource(R.string.enable_biometrics_text),
        icon = R.drawable.ic_fingerprint,
        // TODO: Enable fingerprint authentication
        onEnable = onNext,
        onSkip = onSkip
    )
}

@Preview
@Composable
private fun EnableBiometricsScreenPreview() {
    EnableBiometricsScreen(onNext = {}, onSkip = {})
}