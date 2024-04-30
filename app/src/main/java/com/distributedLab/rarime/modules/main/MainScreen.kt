package com.distributedLab.rarime.modules.main

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.modules.common.IdentityViewModel
import com.distributedLab.rarime.modules.common.PassportViewModel
import com.distributedLab.rarime.modules.common.SecurityViewModel
import com.distributedLab.rarime.modules.common.SettingsViewModel
import com.distributedLab.rarime.modules.common.WalletViewModel
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
import com.distributedLab.rarime.modules.security.LockScreen
import com.distributedLab.rarime.modules.security.RepeatPasscodeScreen
import com.distributedLab.rarime.modules.wallet.WalletReceiveScreen
import com.distributedLab.rarime.modules.wallet.WalletScreen
import com.distributedLab.rarime.modules.wallet.WalletSendScreen
import com.distributedLab.rarime.ui.components.AppWebView
import com.distributedLab.rarime.ui.theme.AppTheme
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Constants
import com.distributedLab.rarime.util.Screen

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
fun MainScreen() {
    val identityViewModel: IdentityViewModel = hiltViewModel()
    val securityViewModel: SecurityViewModel = hiltViewModel()
    val walletViewModel: WalletViewModel = hiltViewModel()
    val passportViewModel: PassportViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val viewModel: MainViewModel = hiltViewModel()

    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route
    val isBottomBarVisible = currentRoute != null && currentRoute in mainRoutes

    val startDestination =
        if (securityViewModel.biometricsState.value != SecurityCheckState.UNSET) {
            Screen.Main.route
        } else if (securityViewModel.passcodeState.value != SecurityCheckState.UNSET) {
            Screen.EnableBiometrics.route
        } else if (viewModel.isIntroFinished.value) {
            Screen.Passcode.route
        } else {
            Screen.Intro.route
        }

    fun navigateWithPopUp(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.id) { inclusive = true }
            restoreState = true
            launchSingleTop = true
        }
    }

    AppTheme(colorScheme = settingsViewModel.colorScheme.value) {
        if (viewModel.isLocked.value) {
            LockScreen(
                isBiometricEnabled = securityViewModel.biometricsState.value == SecurityCheckState.ENABLED,
                passcode = securityViewModel.passcode.value,
                onPass = { viewModel.unlock() }
            )
        } else {
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
                        ScanPassportScreen(
                            claimAirdrop = { walletViewModel.claimAirdrop() },
                            onClose = { navController.popBackStack() }
                        )
                    }

                    navigation(
                        startDestination = Screen.Register.NewIdentity.route,
                        route = Screen.Register.route
                    ) {
                        composable(Screen.Register.NewIdentity.route) {
                            NewIdentityScreen(
                                privateKey = identityViewModel.privateKey.value,
                                onNext = {
                                    viewModel.finishIntro()
                                    navController.navigate(Screen.Passcode.route)
                                },
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
                            HomeScreen(
                                balance = walletViewModel.balance.doubleValue,
                                passport = passportViewModel.passport.value,
                                passportCardLook = passportViewModel.passportCardLook.value,
                                isIncognito = passportViewModel.isIncognitoMode.value,
                                onPassportCardLookChange = passportViewModel::updatePassportCardLook,
                                onIncognitoChange = passportViewModel::updateIsIncognitoMode,
                                navigate = { navController.navigate(it) }
                            )
                        }
                        composable(Screen.Main.Wallet.route) {
                            WalletScreen(
                                balance = walletViewModel.balance.doubleValue,
                                transactions = walletViewModel.transactions.value,
                                navigate = { navController.navigate(it) }
                            )
                        }
                        composable(Screen.Main.Wallet.Receive.route) {
                            WalletReceiveScreen(
                                address = walletViewModel.address,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Main.Wallet.Send.route) {
                            WalletSendScreen(
                                balance = walletViewModel.balance.doubleValue,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Main.Rewards.route) { RewardsScreen() }

                        composable(Screen.Main.Profile.route) {
                            ProfileScreen(
                                did = identityViewModel.did,
                                language = settingsViewModel.language.value,
                                colorScheme = settingsViewModel.colorScheme.value,
                            ) { navController.navigate(it) }
                        }
                        composable(Screen.Main.Profile.AuthMethod.route) {
                            AuthMethodScreen(
                                biometricsState = securityViewModel.biometricsState.value,
                                passcodeState = securityViewModel.passcodeState.value,
                                onBiometricsStateChanged = securityViewModel::updateBiometricsState,
                                onPasscodeStateChanged = securityViewModel::updatePasscodeState,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Main.Profile.ExportKeys.route) {
                            ExportKeysScreen(privateKey = identityViewModel.privateKey.value) { navController.popBackStack() }
                        }
                        composable(Screen.Main.Profile.Language.route) {
                            LanguageScreen(
                                language = settingsViewModel.language.value,
                                onLanguageChanged = settingsViewModel::updateLanguage,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.Main.Profile.Theme.route) {
                            ThemeScreen(
                                colorScheme = settingsViewModel.colorScheme.value,
                                onColorSchemeChanged = settingsViewModel::updateColorScheme,
                                onBack = { navController.popBackStack() }
                            )
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
    }
}

@Preview
@Composable
private fun MainViewPreview() {
    MainScreen()
}
