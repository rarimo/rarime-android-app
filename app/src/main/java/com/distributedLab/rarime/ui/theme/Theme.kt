package com.distributedLab.rarime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

@Composable
fun AppTheme(
    typography: RarimeTypography = RarimeTheme.typography,
    colors: RarimeColors = RarimeTheme.colors,
    darkColors: RarimeColors = darkColors(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val currentColors = remember { if (darkTheme) darkColors else colors }
    val rememberedColors = remember { currentColors.copy() }.apply { updateColorsFrom(currentColors) }
    CompositionLocalProvider(
        LocalColors provides rememberedColors,
        LocalTypography provides RarimeTypography(),
    ) {
        ProvideTextStyle(typography.body3.copy(color = RarimeTheme.colors.textPrimary), content = content)
    }
}