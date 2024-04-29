package com.distributedLab.rarime.modules.main

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.modules.common.SecurityViewModel
import com.distributedLab.rarime.modules.home.HomeScreen
import com.distributedLab.rarime.modules.intro.IntroScreen
import com.distributedLab.rarime.modules.passport.ScanPassportScreen
import com.distributedLab.rarime.modules.profile.AuthMethodScreen
import com.distributedLab.rarime.modules.profile.ExportKeysScreen
import com.distributedLab.rarime.modules.profile.LanguageScreen
import com.distributedLab.rarime.modules.profile.ProfileScreen
import com.distributedLab.rarime.modules.profile.ThemeScreen
import com.distributedLab.rarime.modules.register.ImportIdentityScreen
import com.distributedLab.rarime.modules.register.NewIdentityScreen
import com.distributedLab.rarime.modules.rewards.RewardsScreen
import com.distributedLab.rarime.modules.security.EnableBiometricsScreen
import com.distributedLab.rarime.modules.security.EnablePasscodeScreen
import com.distributedLab.rarime.modules.security.EnterPasscodeScreen
import com.distributedLab.rarime.modules.security.RepeatPasscodeScreen
import com.distributedLab.rarime.modules.wallet.WalletReceiveScreen
import com.distributedLab.rarime.modules.wallet.WalletScreen
import com.distributedLab.rarime.modules.wallet.WalletSendScreen
import com.distributedLab.rarime.ui.components.AppWebView
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Constants

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
fun MainScreen(
    securityViewModel: SecurityViewModel = viewModel(LocalContext.current as ComponentActivity),
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isBottomBarVisible = currentRoute != null && currentRoute in mainRoutes

    val startDestination =
        if (securityViewModel.biometricsState.value != SecurityCheckState.UNSET) {
            Screen.Main.route
        } else if (securityViewModel.passcodeState.value != SecurityCheckState.UNSET) {
            Screen.EnableBiometrics.route
        } else {
            Screen.Passcode.route
        }

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
        ScreenBarsColor(route = currentRoute ?: "")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.backgroundPrimary)
        )
        NavHost(
            navController,
            startDestination = startDestination,
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
                        onNext = { navController.navigate(Screen.Passcode.route) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Register.ImportIdentity.route) {
                    ImportIdentityScreen { navigateWithPopUp(Screen.Passcode.EnablePasscode.route) }
                }
            }

            navigation(
                startDestination = Screen.Passcode.EnablePasscode.route,
                route = Screen.Passcode.route
            ) {
                composable(Screen.Passcode.EnablePasscode.route) {
                    EnablePasscodeScreen(
                        onNext = { navController.navigate(Screen.Passcode.EnterPasscode.route) },
                        onSkip = { navigateWithPopUp(Screen.EnableBiometrics.route) }
                    )
                }
                composable(Screen.Passcode.EnterPasscode.route) {
                    EnterPasscodeScreen(
                        onNext = {
                            securityViewModel.setPasscode(it)
                            navController.navigate(Screen.Passcode.RepeatPasscode.route)
                        },
                        onBack = {
                            securityViewModel.updatePasscodeState(SecurityCheckState.DISABLED)
                            navController.popBackStack()
                        }
                    )
                }
                composable(Screen.Passcode.RepeatPasscode.route) {
                    RepeatPasscodeScreen(
                        passcode = securityViewModel.passcode.value,
                        onNext = {
                            securityViewModel.updatePasscodeState(SecurityCheckState.ENABLED)
                            navigateWithPopUp(Screen.EnableBiometrics.route)
                        },
                        onBack = {
                            navController.popBackStack()
                            // TODO: clear passcode field
                        },
                        onClose = {
                            navController.popBackStack(
                                Screen.Passcode.EnablePasscode.route,
                                false
                            )
                        }
                    )
                }
            }

            composable(Screen.EnableBiometrics.route) {
                EnableBiometricsScreen(
                    onNext = {
                        securityViewModel.updateBiometricsState(SecurityCheckState.ENABLED)
                        navigateWithPopUp(Screen.Main.route)
                    },
                    onSkip = {
                        securityViewModel.updateBiometricsState(SecurityCheckState.DISABLED)
                        navigateWithPopUp(Screen.Main.route)
                    }
                )
            }

            navigation(
                startDestination = Screen.Main.Home.route,
                route = Screen.Main.route
            ) {
                composable(Screen.Main.Home.route) {
                    HomeScreen { navController.navigate(it) }
                }
                composable(Screen.Main.Wallet.route) {
                    WalletScreen { navController.navigate(it) }
                }
                composable(Screen.Main.Wallet.Receive.route) {
                    WalletReceiveScreen { navController.popBackStack() }
                }
                composable(Screen.Main.Wallet.Send.route) {
                    WalletSendScreen { navController.popBackStack() }
                }
                composable(Screen.Main.Rewards.route) { RewardsScreen() }

                composable(Screen.Main.Profile.route) {
                    ProfileScreen { navController.navigate(it) }
                }
                composable(Screen.Main.Profile.AuthMethod.route) {
                    AuthMethodScreen { navController.popBackStack() }
                }
                composable(Screen.Main.Profile.ExportKeys.route) {
                    ExportKeysScreen { navController.popBackStack() }
                }
                composable(Screen.Main.Profile.Language.route) {
                    LanguageScreen { navController.popBackStack() }
                }
                composable(Screen.Main.Profile.Theme.route) {
                    ThemeScreen { navController.popBackStack() }
                }
                composable(Screen.Main.Profile.Terms.route) {
                    AppWebView(
                        title = stringResource(R.string.terms_of_use),
                        url = Constants.TERMS_URL,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Main.Profile.Privacy.route) {
                    AppWebView(
                        title = stringResource(R.string.privacy_policy),
                        url = Constants.PRIVACY_URL,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

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

@Preview
@Composable
private fun MainViewPreview() {
    MainScreen()
}
