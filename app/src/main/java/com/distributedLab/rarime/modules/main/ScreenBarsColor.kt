package com.distributedLab.rarime.modules.main

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.distributedLab.rarime.data.enums.AppColorScheme
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Screen

private val navPureBgRoutes = listOf(
    Screen.Register.NewIdentity.route,
    Screen.Main.Wallet.Send.route,
)

private val statusPureBgRoutes = listOf(
    Screen.Main.Wallet.route,
)

@Composable
fun ScreenBarsColor(colorScheme: AppColorScheme, route: String) {
    val view = LocalView.current
    val isSystemDark = isSystemInDarkTheme()

    val navColor =
        if (route in navPureBgRoutes) RarimeTheme.colors.backgroundPure
        else RarimeTheme.colors.backgroundPrimary
    val statusColor =
        if (route in statusPureBgRoutes) RarimeTheme.colors.backgroundPure
        else RarimeTheme.colors.backgroundPrimary

    val isLightStyle = when (colorScheme) {
        AppColorScheme.LIGHT -> true
        AppColorScheme.DARK -> false
        AppColorScheme.SYSTEM -> !isSystemDark
    }

    fun changeBarsColor() {
        val window = (view.context as Activity).window
        window.navigationBarColor = navColor.toArgb()
        window.statusBarColor = statusColor.toArgb()
    }

    fun changeBarsStyle() {
        val insetsController =
            WindowCompat.getInsetsController((view.context as Activity).window, view)
        insetsController.isAppearanceLightStatusBars = isLightStyle
        insetsController.isAppearanceLightNavigationBars = isLightStyle
    }

    if (!view.isInEditMode) {
        SideEffect {
            changeBarsColor()
            changeBarsStyle()
        }
    }
}