package com.distributedLab.rarime.util

sealed class Screen(val route: String) {
    data object Intro : Screen("intro")
    data object ScanPassport : Screen("scan_passport")

    data object Register : Screen("register") {
        data object NewIdentity : Screen("new_identity")
        data object ImportIdentity : Screen("import_identity")
    }

    data object Passcode : Screen("security") {
        data object EnablePasscode : Screen("enable_passcode")
        data object EnterPasscode : Screen("enter_passcode")
        data object RepeatPasscode : Screen("repeat_passcode")
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
            data object Terms : Screen("terms")
            data object Privacy : Screen("privacy")
        }
    }
}
