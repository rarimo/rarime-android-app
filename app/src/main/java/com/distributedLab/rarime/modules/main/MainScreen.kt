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
import com.distributedLab.rarime.modules.home.HomeScreen
import com.distributedLab.rarime.modules.intro.IntroScreen
import com.distributedLab.rarime.modules.passport.ScanPassportScreen
import com.distributedLab.rarime.modules.profile.ProfileScreen
import com.distributedLab.rarime.modules.register.ImportIdentityScreen
import com.distributedLab.rarime.modules.register.NewIdentityScreen
import com.distributedLab.rarime.modules.rewards.RewardsScreen
import com.distributedLab.rarime.modules.security.EnableBiometricsScreen
import com.distributedLab.rarime.modules.security.EnablePasscodeScreen
import com.distributedLab.rarime.modules.security.EnterPasscodeScreen
import com.distributedLab.rarime.modules.security.RepeatPasscodeScreen
import com.distributedLab.rarime.modules.wallet.WalletScreen
import com.distributedLab.rarime.ui.theme.RarimeTheme

sealed class Screen(val route: String) {
    data object Intro : Screen("intro")
    data object ScanPassport : Screen("scan_passport")

    data object Register : Screen("register") {
        data object NewIdentity : Screen("new_identity")
        data object ImportIdentity : Screen("import_identity")
    }

    data object Security : Screen("security") {
        data object EnablePasscode : Screen("enable_passcode")
        data object EnterPasscode : Screen("enter_passcode")
        data object RepeatPasscode : Screen("repeat_passcode")
        data object EnableBiometrics : Screen("enable_biometrics")
    }

    data object Main : Screen("main") {
        data object Home : Screen("home")
        data object Wallet : Screen("wallet")
        data object Rewards : Screen("rewards")
        data object Profile : Screen("profile")
    }
}

val mainRoutes = listOf(
    Screen.Main.Home.route,
    Screen.Main.Rewards.route,
    Screen.Main.Wallet.route,
    Screen.Main.Profile.route
)

// We have a floating tab bar at the bottom of the screen,
// so no need to use scaffold padding
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isBottomBarVisible = currentRoute != null && currentRoute in mainRoutes

    fun navigateWithPopUp(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.id) { inclusive = true }
            restoreState = true
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                BottomTabBar(
                    currentRoute = currentRoute,
                    onRouteSelected = { navigateWithPopUp(it) }
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
            // TODO: Set startDestination using saved state
            startDestination = Screen.Main.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            composable(Screen.Intro.route) {
                IntroScreen { navController.navigate(it) }
            }

            composable(Screen.ScanPassport.route) {
                ScanPassportScreen { navController.popBackStack() }
            }

            navigation(
                startDestination = Screen.Register.NewIdentity.route,
                route = Screen.Register.route
            ) {
                composable(Screen.Register.NewIdentity.route) {
                    NewIdentityScreen(
                        onNext = { navController.navigate(Screen.Security.EnablePasscode.route) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Register.ImportIdentity.route) {
                    ImportIdentityScreen { navigateWithPopUp(Screen.Security.EnablePasscode.route) }
                }
            }

            navigation(
                startDestination = Screen.Security.EnablePasscode.route,
                route = Screen.Security.route
            ) {
                composable(Screen.Security.EnablePasscode.route) {
                    EnablePasscodeScreen(
                        onNext = { navController.navigate(Screen.Security.EnterPasscode.route) },
                        onSkip = { navigateWithPopUp(Screen.Security.EnableBiometrics.route) }
                    )
                }
                composable(Screen.Security.EnterPasscode.route) {
                    EnterPasscodeScreen(
                        onNext = { navController.navigate(Screen.Security.RepeatPasscode.route) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Security.RepeatPasscode.route) {
                    RepeatPasscodeScreen(
                        onNext = { navigateWithPopUp(Screen.Security.EnableBiometrics.route) },
                        onBack = {
                            navController.popBackStack()
                            // TODO: clear passcode field
                        },
                        onClose = {
                            navController.popBackStack(
                                Screen.Security.EnablePasscode.route,
                                false
                            )
                        }
                    )
                }
                composable(Screen.Security.EnableBiometrics.route) {
                    EnableBiometricsScreen { navigateWithPopUp(Screen.Main.route) }
                }
            }

            navigation(
                startDestination = Screen.Main.Home.route,
                route = Screen.Main.route
            ) {
                composable(Screen.Main.Home.route) {
                    HomeScreen { navController.navigate(Screen.ScanPassport.route) }
                }
                composable(Screen.Main.Wallet.route) { WalletScreen() }
                composable(Screen.Main.Rewards.route) { RewardsScreen() }
                composable(Screen.Main.Profile.route) { ProfileScreen() }
            }
        }
    }
}

@Composable
fun NavigationBarColor(route: String) {
    val pureBgRoutes = listOf(
        Screen.Register.NewIdentity.route,
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
