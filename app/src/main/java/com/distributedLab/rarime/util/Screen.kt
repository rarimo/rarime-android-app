package com.distributedLab.rarime.util

sealed class Screen(val route: String) {
    data object Intro : Screen("intro")
    data object ScanPassport : Screen("scan_passport")  {

        data object ScanPassportUkr : Screen("scan_passport_ukr")
        data object ScanPassportReserve: Screen("scan_passport_reserve")
    }
    data object Lock : Screen("lock")

    data object Register : Screen("register") {
        data object NewIdentity : Screen("new_identity")
    }

    data object Claim : Screen("calim") {
        data object Reserve : Screen("reserve")
        data object Ukr : Screen("rarimo")
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
}
