package com.distributedLab.rarime.modules.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppCheckboxState
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSwitch
import com.distributedLab.rarime.ui.components.rememberAppCheckboxState
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun AuthMethodScreen(onBack: () -> Unit) {
    val biometricsState = rememberAppCheckboxState()
    val passcodeState = rememberAppCheckboxState()

    ProfileRouteLayout(
        title = stringResource(R.string.auth_method),
        onBack = onBack
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            AuthMethodRow(
                iconId = R.drawable.ic_fingerprint,
                label = stringResource(R.string.biometrics),
                state = biometricsState
            )
            AuthMethodRow(
                iconId = R.drawable.ic_password,
                label = stringResource(R.string.passcode),
                state = passcodeState
            )
        }
    }
}

@Composable
private fun AuthMethodRow(
    @DrawableRes iconId: Int,
    label: String,
    state: AppCheckboxState
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
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
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary
            )
        }
        AppSwitch(state = state)
    }
}

@Preview
@Composable
private fun AuthMethodScreenPreview() {
    AuthMethodScreen {}
}
