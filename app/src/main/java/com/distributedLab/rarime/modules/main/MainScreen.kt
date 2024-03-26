package com.distributedLab.rarime.modules.main

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.credentials.CredentialsScreen
import com.distributedLab.rarime.modules.home.HomeScreen
import com.distributedLab.rarime.modules.rewards.RewardsScreen
import com.distributedLab.rarime.modules.settings.SettingsScreen
import com.distributedLab.rarime.modules.wallet.WalletScreen

sealed class MainTab(
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val activeIcon: Int,
) {
    data object Home : MainTab(
        route = "home",
        icon = R.drawable.ic_house_simple,
        activeIcon = R.drawable.ic_house_simple_fill
    )

    data object Rewards : MainTab(
        route = "rewards",
        icon = R.drawable.ic_gift,
        activeIcon = R.drawable.ic_gift_fill
    )

    data object Wallet : MainTab(
        route = "wallet",
        icon = R.drawable.ic_wallet,
        activeIcon = R.drawable.ic_wallet_filled
    )

    data object Credentials : MainTab(
        route = "credentials",
        icon = R.drawable.ic_identification_card,
        activeIcon = R.drawable.ic_identification_card_fill
    )

    data object Settings : MainTab(
        route = "settings",
        icon = R.drawable.ic_dots_three_outline,
        activeIcon = R.drawable.ic_dots_three_outline
    )
}

val mainTabs = listOf(
    MainTab.Home,
    MainTab.Rewards,
    MainTab.Wallet,
    MainTab.Credentials,
    MainTab.Settings
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedTab = mainTabs.find { it.route == currentRoute } ?: mainTabs.first()

    Scaffold(
        bottomBar = {
            BottomTabBar(tabs = mainTabs, selectedTab = selectedTab) {
                navController.navigate(it.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
    ) {
        NavHost(
            navController,
            startDestination = MainTab.Home.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            composable(MainTab.Home.route) { HomeScreen() }
            composable(MainTab.Rewards.route) { RewardsScreen() }
            composable(MainTab.Wallet.route) { WalletScreen() }
            composable(MainTab.Credentials.route) { CredentialsScreen() }
            composable(MainTab.Settings.route) { SettingsScreen() }
        }
    }
}

@Preview
@Composable
private fun MainViewPreview() {
    MainScreen()
}
