package com.distributedLab.rarime.modules.main

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Screen

@Composable
fun ScreenBarsColor(route: String) {
    val view = LocalView.current

    val navPureBgRoutes = listOf(
        Screen.Register.NewIdentity.route,
        Screen.Main.Wallet.Send.route,
    )
    val statusPureBgRoutes = listOf(
        Screen.Main.Wallet.route,
    )

    val navColor = if (route in navPureBgRoutes) {
        RarimeTheme.colors.backgroundPure
    } else {
        RarimeTheme.colors.backgroundPrimary
    }
    val statusColor = if (route in statusPureBgRoutes) {
        RarimeTheme.colors.backgroundPure
    } else {
        RarimeTheme.colors.backgroundPrimary
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = navColor.toArgb()
            window.statusBarColor = statusColor.toArgb()
        }
    }
}