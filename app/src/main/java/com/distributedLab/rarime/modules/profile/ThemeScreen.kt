package com.distributedLab.rarime.modules.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.toLocalizedString
import com.distributedLab.rarime.ui.components.AppRadioButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun ThemeScreen(
    viewModel: ThemeViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val colorScheme by viewModel.colorScheme
    ProfileRouteLayout(title = stringResource(R.string.theme), onBack = onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppColorScheme.entries.forEach { scheme ->
                SchemeItem(
                    scheme = scheme,
                    isSelected = scheme == colorScheme,
                    onClick = { viewModel.onColorSchemeChange(scheme) }
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
                style = RarimeTheme.typography.buttonMedium,
                color = RarimeTheme.colors.textPrimary
            )
        }
    }
}

@Preview
@Composable
private fun ThemeScreenPreview() {
    ThemeScreen(
        onBack = {}
    )
}
