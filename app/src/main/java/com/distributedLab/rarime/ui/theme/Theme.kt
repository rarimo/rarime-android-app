package com.distributedLab.rarime.ui.theme

import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.data.enums.isDark

@Composable
fun AppTheme(
    colorScheme: AppColorScheme = AppColorScheme.SYSTEM,
    content: @Composable () -> Unit,
) {
    val currentColors = if (colorScheme.isDark()) darkColors() else lightColors()
    val rememberedColors =
        remember { currentColors.copy() }.apply { updateColorsFrom(currentColors) }
    CompositionLocalProvider(
        LocalColors provides rememberedColors,
        LocalTypography provides RarimeTypography(),
    ) {
        ProvideTextStyle(
            value = RarimeTheme.typography.body3.copy(color = RarimeTheme.colors.textPrimary),
            content = content
        )
    }
}