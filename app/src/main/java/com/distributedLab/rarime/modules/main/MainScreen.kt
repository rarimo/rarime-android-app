package com.distributedLab.rarime.modules.main

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.distributedLab.rarime.modules.credentials.CredentialsScreen
import com.distributedLab.rarime.modules.home.HomeScreen
import com.distributedLab.rarime.modules.intro.IntroScreen
import com.distributedLab.rarime.modules.register.ImportPhraseScreen
import com.distributedLab.rarime.modules.register.NewPhraseScreen
import com.distributedLab.rarime.modules.register.VerifyPhraseScreen
import com.distributedLab.rarime.modules.rewards.RewardsScreen
import com.distributedLab.rarime.modules.security.EnableBiometricsScreen
import com.distributedLab.rarime.modules.security.EnablePasscodeScreen
import com.distributedLab.rarime.modules.security.EnterPasscodeScreen
import com.distributedLab.rarime.modules.security.RepeatPasscodeScreen
import com.distributedLab.rarime.modules.settings.SettingsScreen
import com.distributedLab.rarime.modules.wallet.WalletScreen
import com.distributedLab.rarime.ui.theme.RarimeTheme

sealed class Screen(val route: String) {
    data object Intro : Screen("intro")
    data object Register : Screen("register") {
        data object NewPhrase : Screen("new_phrase")
        data object VerifyPhrase : Screen("verify_phrase")
        data object ImportPhrase : Screen("import_phrase")
    }

    data object Security : Screen("security") {
        data object EnablePasscode : Screen("enable_passcode")
        data object EnterPasscode : Screen("enter_passcode")
        data object RepeatPasscode : Screen("repeat_passcode")
        data object EnableBiometrics : Screen("enable_biometrics")
    }

    data object Main : Screen("main") {
        data object Home : Screen("home")
        data object Rewards : Screen("rewards")
        data object Wallet : Screen("wallet")
        data object Credentials : Screen("credentials")
        data object Settings : Screen("settings")
    }
}

val mainRoutes = listOf(
    Screen.Main.Home.route,
    Screen.Main.Rewards.route,
    Screen.Main.Wallet.route,
    Screen.Main.Credentials.route,
    Screen.Main.Settings.route
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isBottomBarVisible = currentRoute != null && currentRoute in mainRoutes

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                BottomTabBar(
                    currentRoute = currentRoute,
                    onRouteSelected = {
                        navController.navigate(it) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
    ) {
        NavigationBarColor(route = currentRoute ?: "")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.backgroundPrimary)
        )
        NavHost(
            navController,
            startDestination = Screen.Security.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            composable(Screen.Intro.route) {
                IntroScreen { navController.navigate(it) }
            }

            navigation(
                startDestination = Screen.Register.NewPhrase.route,
                route = Screen.Register.route
            ) {
                composable(Screen.Register.NewPhrase.route) {
                    NewPhraseScreen(
                        onNext = { navController.navigate(Screen.Register.VerifyPhrase.route) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Register.VerifyPhrase.route) {
                    VerifyPhraseScreen(
                        onNext = { navController.navigate(Screen.Security.EnablePasscode.route) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Register.ImportPhrase.route) {
                    ImportPhraseScreen { navController.navigate(Screen.Security.EnablePasscode.route) }
                }
            }

            navigation(
                startDestination = Screen.Security.EnablePasscode.route,
                route = Screen.Security.route
            ) {
                composable(Screen.Security.EnablePasscode.route) {
                    EnablePasscodeScreen(
                        onNext = { navController.navigate(Screen.Security.EnterPasscode.route) },
                        onSkip = { navController.navigate(Screen.Security.EnableBiometrics.route) }
                    )
                }
                composable(Screen.Security.EnterPasscode.route) {
                    EnterPasscodeScreen { navController.navigate(Screen.Security.RepeatPasscode.route) }
                }
                composable(Screen.Security.RepeatPasscode.route) {
                    RepeatPasscodeScreen { navController.navigate(Screen.Security.EnableBiometrics.route) }
                }
                composable(Screen.Security.EnableBiometrics.route) {
                    EnableBiometricsScreen { navController.navigate(Screen.Main.route) }
                }
            }

            navigation(
                startDestination = Screen.Main.Home.route,
                route = Screen.Main.route
            ) {
                composable(Screen.Main.Home.route) { HomeScreen() }
                composable(Screen.Main.Rewards.route) { RewardsScreen() }
                composable(Screen.Main.Wallet.route) { WalletScreen() }
                composable(Screen.Main.Credentials.route) { CredentialsScreen() }
                composable(Screen.Main.Settings.route) { SettingsScreen() }
            }
        }
    }
}

@Composable
fun NavigationBarColor(route: String) {
    val pureBgRoutes = listOf(
        Screen.Register.NewPhrase.route,
        Screen.Register.VerifyPhrase.route,
    )

    val view = LocalView.current
    val color = if (route in pureBgRoutes) {
        RarimeTheme.colors.backgroundPure
    } else {
        RarimeTheme.colors.backgroundPrimary
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = color.toArgb()
        }
    }
}

@Preview
@Composable
private fun MainViewPreview() {
    MainScreen()
}
