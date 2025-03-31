package com.rarilabs.rarime.util

sealed class Screen(val route: String) {
    data object Intro : Screen("intro")
    data object ScanPassport : Screen("scan_passport")  {
        data object ScanPassportSpecific : Screen("scan_passport_specific")
        data object ScanPassportPoints: Screen("scan_passport_points")
    }
    data object Lock : Screen("lock")

    data object Register : Screen("register") {
        data object NewIdentity : Screen("new_identity")
        data object ImportIdentity : Screen("import_identity")
    }

    data object Claim : Screen("claim") {
        data object Reserve : Screen("reserve")
        data object Specific : Screen("specific")
    }

    data object Passcode : Screen("security") {
        data object EnablePasscode : Screen("enable_passcode")
        data object AddPasscode : Screen("add_passcode")
    }

    data object EnableBiometrics : Screen("enable_biometrics")

    data object NotificationsList: Screen("notifications_list")

    data object Main : Screen("main") {
        data object Home : Screen("home")
        data object Vote : Screen("vote/{vote_id}")
        data object Wallet : Screen("wallet") {
            data object Receive : Screen("receive")
            data object Send : Screen("send")
        }
        data object Identity : Screen("identity")
        data object QrScan : Screen("qr_scan")
        data object Rewards : Screen("rewards") {
            data object RewardsMain : Screen("rewards_main")

            data object RewardsEventsItem : Screen("rewards_events_item/{item_id}")

            data object RewardsClaim : Screen("rewards_claim")
        }
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

    data object Invitation : Screen("invitation/{code}")
    data object ExtIntegrator : Screen("external")
}
