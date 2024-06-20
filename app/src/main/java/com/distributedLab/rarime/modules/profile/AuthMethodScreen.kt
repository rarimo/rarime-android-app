package com.distributedLab.rarime.modules.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.modules.security.PasscodeScreen
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSwitch
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.BiometricUtil

@Composable
fun AuthMethodScreen(
    viewModel: AuthMethodViewModel = hiltViewModel(), onBack: () -> Unit
) {
    var isPasscodeShown by remember { mutableStateOf(false) }
    val biometricsState by viewModel.biometricsState
    val passcodeState by viewModel.passcodeState
    val passcode by viewModel.passcode

    val context = LocalContext.current
    val isBiometricsAvailable = remember {
        BiometricUtil.isSupported(context)
    }

    fun handleBiometricsStateChange(isEnabled: Boolean) {
        BiometricUtil.authenticate(context,
            title = context.getString(R.string.biometric_authentication_title),
            subtitle = context.getString(R.string.biometric_authentication_subtitle),
            negativeButtonText = context.getString(R.string.cancel_btn),
            onSuccess = {
                viewModel.updateBiometricsState(
                    if (isEnabled) SecurityCheckState.ENABLED else SecurityCheckState.DISABLED
                )
            },
            onError = {})
    }

    if (isPasscodeShown) {
        PasscodeScreen(passcodeState = passcodeState,
            passcode = passcode,
            onPasscodeChange = viewModel::setPasscode,
            onPasscodeStateChange = {
                viewModel.updatePasscodeState(it)
                isPasscodeShown = false
            },
            onClose = { isPasscodeShown = false })
    } else {
        ProfileRouteLayout(
            title = stringResource(R.string.auth_method), onBack = onBack
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AuthMethodItem(iconId = R.drawable.ic_password,
                    label = stringResource(R.string.passcode),
                    checked = passcodeState == SecurityCheckState.ENABLED,
                    onCheckedChange = { isPasscodeShown = true })

                if (isBiometricsAvailable) {
                    AuthMethodItem(iconId = R.drawable.ic_fingerprint,
                        label = stringResource(R.string.biometrics),
                        checked = biometricsState == SecurityCheckState.ENABLED,
                        onCheckedChange = { handleBiometricsStateChange(it) },
                        enabled = passcodeState == SecurityCheckState.ENABLED)
                }
            }
        }
    }
}

@Composable
private fun AuthMethodItem(
    @DrawableRes iconId: Int, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean = true
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundOpacity, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(
                id = iconId,
                size = 20.dp,
                tint = RarimeTheme.colors.textPrimary,
                modifier = Modifier
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
                    .padding(6.dp)
            )
            Text(
                text = label,
                style = RarimeTheme.typography.buttonMedium,
                color = RarimeTheme.colors.textPrimary
            )
        }
        AppSwitch(
            checked = checked, onCheckedChange = onCheckedChange, enabled = enabled
        )
    }
}

@Preview
@Composable
private fun AuthMethodScreenPreview() {
    AuthMethodScreen(onBack = {})
}
