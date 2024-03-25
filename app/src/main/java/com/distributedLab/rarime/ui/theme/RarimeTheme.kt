package com.distributedLab.rarime.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object RarimeTheme {
    val colors: RarimeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: RarimeTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}