package com.distributedLab.rarime.modules.main

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.credentials.CredentialsView
import com.distributedLab.rarime.modules.home.HomeView
import com.distributedLab.rarime.modules.rewards.RewardsView
import com.distributedLab.rarime.modules.settings.SettingsView
import com.distributedLab.rarime.modules.wallet.WalletView
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme

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
fun MainView(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedTab = mainTabs.find { it.route == currentRoute } ?: mainTabs.first()

    Scaffold(
        bottomBar = {
            BottomTabBar(tabs = mainTabs, selectedTab = selectedTab) {
                navController.navigate(it.route) {
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
            composable(MainTab.Home.route) { HomeView() }
            composable(MainTab.Rewards.route) { RewardsView() }
            composable(MainTab.Wallet.route) { WalletView() }
            composable(MainTab.Credentials.route) { CredentialsView() }
            composable(MainTab.Settings.route) { SettingsView() }
        }
    }
}

@Preview
@Composable
private fun MainViewPreview() {
    MainView()
}
