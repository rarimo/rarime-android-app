package com.rarilabs.rarime.data.enums

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rarilabs.rarime.R

enum class AppColorScheme(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}

@Composable
fun AppColorScheme.isDark(): Boolean {
    return when (this) {
        AppColorScheme.SYSTEM -> isSystemInDarkTheme()
        AppColorScheme.LIGHT -> false
        AppColorScheme.DARK -> true
    }
}

@Composable
fun AppColorScheme.toLocalizedString(): String {
    return when (this) {
        AppColorScheme.LIGHT -> stringResource(R.string.light_mode)
        AppColorScheme.DARK -> stringResource(R.string.dark_mode)
        AppColorScheme.SYSTEM -> stringResource(R.string.system)
    }
}
