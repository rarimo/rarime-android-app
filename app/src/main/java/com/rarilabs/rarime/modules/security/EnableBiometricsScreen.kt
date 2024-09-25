package com.rarilabs.rarime.modules.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.util.BiometricUtil

@Composable
fun EnableBiometricsScreen(onNext: () -> Unit, onSkip: () -> Unit, viewModel: BiometricViewModel = hiltViewModel()) {
    val context = LocalContext.current

    EnableScreenLayout(title = stringResource(R.string.enable_biometrics_title),
        text = stringResource(R.string.enable_biometrics_text),
        icon = R.drawable.ic_fingerprint,
        onEnable = {
            BiometricUtil.authenticate(context = context,
                title = context.getString(R.string.biometric_authentication_title),
                subtitle = context.getString(R.string.biometric_authentication_subtitle),
                negativeButtonText = context.getString(R.string.cancel_btn),
                onSuccess = { viewModel.enableBiometric();onNext.invoke() },
                onError = {})
        },
        onSkip = { viewModel.skipBiometric(); onSkip.invoke() })
}

@Preview
@Composable
private fun EnableBiometricsScreenPreview() {
    EnableBiometricsScreen(onNext = {}, onSkip = {})
}