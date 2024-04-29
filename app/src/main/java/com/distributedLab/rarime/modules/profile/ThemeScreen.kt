package com.distributedLab.rarime.modules.profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.toLocalizedString
import com.distributedLab.rarime.modules.common.SettingsViewModel
import com.distributedLab.rarime.ui.components.AppRadioButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun ThemeScreen(
    settingsViewModel: SettingsViewModel = viewModel(LocalContext.current as ComponentActivity),
    onBack: () -> Unit
) {
    ProfileRouteLayout(title = stringResource(R.string.theme), onBack = onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppColorScheme.entries.forEach { colorScheme ->
                SchemeItem(
                    scheme = colorScheme,
                    isSelected = settingsViewModel.colorScheme.value == colorScheme,
                    onClick = { settingsViewModel.updateColorScheme(colorScheme) }
                )
            }
        }
    }
}

@Composable
private fun SchemeItem(
    scheme: AppColorScheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val imageId = when (scheme) {
        AppColorScheme.LIGHT -> R.drawable.light_theme
        AppColorScheme.DARK -> R.drawable.dark_theme
        AppColorScheme.SYSTEM -> R.drawable.system_theme
    }

    AppRadioButton(isSelected = isSelected, onClick = onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier
                    .width(40.dp)
                    .height(48.dp)
            )
            Text(
                text = scheme.toLocalizedString(),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary
            )
        }
    }
}

@Preview
@Composable
private fun ThemeScreenPreview() {
    ThemeScreen {}
}
