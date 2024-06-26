package com.distributedLab.rarime.util

sealed class Screen(val route: String) {
    data object Intro : Screen("intro")
    data object ScanPassport : Screen("scan_passport")
    data object Lock : Screen("lock")

    data object Register : Screen("register") {
        data object NewIdentity : Screen("new_identity")
    }

    data object Passcode : Screen("security") {
        data object EnablePasscode : Screen("enable_passcode")
        data object AddPasscode : Screen("add_passcode")
    }

    data object EnableBiometrics : Screen("enable_biometrics")

    data object Main : Screen("main") {
        data object Home : Screen("home")
        data object Wallet : Screen("wallet") {
            data object Receive : Screen("receive")
            data object Send : Screen("send")
        }

        data object Rewards : Screen("rewards")
        data object Profile : Screen("profile") {
            data object AuthMethod : Screen("auth_method")
            data object ExportKeys : Screen("export_keys")
            data object Language : Screen("language")
            data object Theme : Screen("theme")
            data object AppIcon : Screen("app_icon")
            data object Terms : Screen("terms")
            data object Privacy : Screen("privacy")
        }
    }
}
