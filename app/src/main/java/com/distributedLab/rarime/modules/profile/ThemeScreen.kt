package com.distributedLab.rarime.modules.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppRadioButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

enum class AppColorScheme {
    LIGHT,
    DARK,
    SYSTEM
}

@Composable
fun ThemeScreen(onBack: () -> Unit) {
    var selectedColorScheme by remember { mutableStateOf(AppColorScheme.SYSTEM) }

    ProfileRouteLayout(title = "Theme", onBack = onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppColorScheme.entries.forEach { colorScheme ->
                SchemeItem(
                    scheme = colorScheme,
                    isSelected = selectedColorScheme == colorScheme,
                    onClick = { selectedColorScheme = colorScheme }
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

    val label = when (scheme) {
        AppColorScheme.LIGHT -> "Light Mode"
        AppColorScheme.DARK -> "Dark Mode"
        AppColorScheme.SYSTEM -> "System"
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
                text = label,
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
