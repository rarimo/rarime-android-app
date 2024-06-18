package com.distributedLab.rarime.modules.main

import android.annotation.SuppressLint
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.SecurityCheckState
import com.distributedLab.rarime.modules.passportVerify.VerifySpecificScreen
import com.distributedLab.rarime.modules.passportVerify.VerifyPoitntsScreen
import com.distributedLab.rarime.modules.home.HomeScreen
import com.distributedLab.rarime.modules.intro.IntroScreen
import com.distributedLab.rarime.modules.passportScan.ScanPassportScreen
import com.distributedLab.rarime.modules.profile.AppIconScreen
import com.distributedLab.rarime.modules.profile.AuthMethodScreen
import com.distributedLab.rarime.modules.profile.ExportKeysScreen
import com.distributedLab.rarime.modules.profile.LanguageScreen
import com.distributedLab.rarime.modules.profile.ProfileScreen
import com.distributedLab.rarime.modules.profile.ThemeScreen
import com.distributedLab.rarime.modules.register.NewIdentityScreen
import com.distributedLab.rarime.modules.rewards.RewardsClaimScreen
import com.distributedLab.rarime.modules.rewards.event_item.RewardsEventItemScreen
import com.distributedLab.rarime.modules.rewards.RewardsScreen
import com.distributedLab.rarime.modules.security.EnableBiometricsScreen
import com.distributedLab.rarime.modules.security.EnablePasscodeScreen
import com.distributedLab.rarime.modules.security.LockScreen
import com.distributedLab.rarime.modules.security.SetupPasscode
import com.distributedLab.rarime.modules.wallet.WalletReceiveScreen
import com.distributedLab.rarime.modules.wallet.WalletScreen
import com.distributedLab.rarime.modules.wallet.WalletSendScreen
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppWebView
import com.distributedLab.rarime.ui.components.enter_program.EnterProgramFlow
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.AppTheme
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.AppIconUtil
import com.distributedLab.rarime.util.Constants
import com.distributedLab.rarime.util.LocaleUtil
import com.distributedLab.rarime.util.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val mainRoutes = listOf(
    Screen.Main.Home.route,
    Screen.Main.Rewards.RewardsMain.route,
    Screen.Main.Wallet.route,
    Screen.Main.Profile.route
)

val LocalMainViewModel = compositionLocalOf<MainViewModel> { error("No MainViewModel provided") }

@Composable
fun MainScreen(mainViewModel: MainViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()

    val appLoadingState = mainViewModel.appLoadingState

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            mainViewModel.initApp()
        }
    }

    CompositionLocalProvider(LocalMainViewModel provides mainViewModel) {
        when (appLoadingState.value) {
            AppLoadingStates.LOADING -> {
                AppLoadingScreen()
            }

            AppLoadingStates.LOAD_FAILED -> {
                AppLoadingFailedScreen()
            }

            AppLoadingStates.LOADED -> {
                MainScreenContent()
            }
        }
    }
}

@Composable
private fun AppLoadingScreen() {
    val scale = remember { mutableStateOf(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            scale.value = 1.1f
            delay(500)
            scale.value = 1f
            delay(500)
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = scale.value,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(
            modifier = Modifier
                .scale(animatedScale),
            id = R.drawable.ic_rarime,
            size = 140.dp
        )
    }
}

@Composable
private fun AppLoadingFailedScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(id = R.drawable.ic_warning, tint = RarimeTheme.colors.errorDark, size = 140.dp)
    }
}

// We have a floating tab bar at the bottom of the screen,
// so no need to use scaffold padding
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenContent() {
    val mainViewModel = LocalMainViewModel.current
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val coroutineScope = rememberCoroutineScope()

    val currentRoute = navBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        mainViewModel.setBottomBarVisibility(currentRoute != null && currentRoute in mainRoutes)
    }

    val context = LocalContext.current
    var appIcon by remember { mutableStateOf(AppIconUtil.getIcon(context)) }

    val isModalShown = mainViewModel.isModalShown.collectAsState()
    val modalContent = mainViewModel.modalContent.collectAsState()

    val pointsBalance by mainViewModel.pointsBalance.collectAsState()

    val enterProgramSheetState = rememberAppSheetState()

    val startDestination = if (mainViewModel.isScreenLocked.value) {
        Screen.Lock.route
    } else if (mainViewModel.biometricsState.value != SecurityCheckState.UNSET) {
        Screen.Main.route
    } else if (mainViewModel.passcodeState.value != SecurityCheckState.UNSET) {
        Screen.EnableBiometrics.route
    } else if (mainViewModel.isIntroFinished.value) {
        Screen.Passcode.route
    } else {
        Screen.Intro.route
    }

    fun navigateWithPopUp(route: String) {
        var nextRoute = route

        if (route == Screen.Main.Rewards.RewardsMain.route) {
            pointsBalance?.data?.attributes?.let {} ?: run {
                enterProgramSheetState.show()

                return
            }
        }

        navController.navigate(nextRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            restoreState = true
            launchSingleTop = true
        }
    }

    AppTheme(colorScheme = mainViewModel.colorScheme.value) {
        Scaffold(
            bottomBar = {
                if (mainViewModel.isBottomBarShown.value) {
                    BottomTabBar(
                        currentRoute = currentRoute,
                        onRouteSelected = { navigateWithPopUp(it) }
                    )
                }
            },
        ) {
            ScreenBarsColor(
                colorScheme = mainViewModel.colorScheme.value, route = currentRoute ?: ""
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RarimeTheme.colors.backgroundPrimary)
            )
            NavHost(
                navController,
                startDestination = startDestination,
                enterTransition = { fadeIn() },
                exitTransition = { ExitTransition.None },
            ) {
                composable(Screen.Intro.route) {
                    IntroScreen { navController.navigate(it) }
                }

                composable(Screen.Lock.route) {
                    LockScreen(onPass = {
                        navController.navigate(Screen.Main.route)
                    })
                }

                //Scan Flow
                composable(Screen.ScanPassport.ScanPassportSpecific.route) {
                    ScanPassportScreen(onClose = { navController.popBackStack() },
                        onClaim = { navigateWithPopUp(Screen.Claim.Specific.route) })
                }

                composable(Screen.ScanPassport.ScanPassportPoints.route) {
                    ScanPassportScreen(onClose = { navController.popBackStack() },
                        onClaim = { navigateWithPopUp(Screen.Claim.Reserve.route) })
                }

                navigation(
                    startDestination = Screen.Register.NewIdentity.route,
                    route = Screen.Register.route
                ) {
                    composable(Screen.Register.NewIdentity.route) {
                        NewIdentityScreen(
                            onNext = {
                                coroutineScope.launch {
                                    mainViewModel.finishIntro()
                                    navController.navigate(Screen.Passcode.route)
                                    mainViewModel.initApp()
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }

                navigation(
                    startDestination = Screen.Passcode.EnablePasscode.route,
                    route = Screen.Passcode.route
                ) {
                    composable(Screen.Passcode.EnablePasscode.route) {
                        EnablePasscodeScreen(onNext = { navController.navigate(Screen.Passcode.AddPasscode.route) },
                            onSkip = { navigateWithPopUp(Screen.EnableBiometrics.route) })
                    }
                    composable(Screen.Passcode.AddPasscode.route) {
                        SetupPasscode(onPasscodeChange = {
                            navigateWithPopUp(Screen.EnableBiometrics.route)
                        }, onClose = {
                            navController.popBackStack(
                                Screen.Passcode.EnablePasscode.route, false
                            )
                        })
                    }
                }

                composable(Screen.EnableBiometrics.route) {
                    EnableBiometricsScreen(onNext = {
                        navigateWithPopUp(Screen.Main.route)
                    }, onSkip = {
                        navigateWithPopUp(Screen.Main.route)
                    })
                }

                composable(Screen.Claim.Specific.route) {
                    VerifySpecificScreen {
                        navigateWithPopUp(Screen.Main.route)
                    }
                }

                composable(Screen.Claim.Reserve.route) {
                    VerifyPoitntsScreen {
                        navigateWithPopUp(Screen.Main.route)
                    }
                }

                navigation(
                    startDestination = Screen.Main.Home.route, route = Screen.Main.route
                ) {
                    composable(Screen.Main.Home.route) {
                        HomeScreen(navigate = { navController.navigate(it) })
                    }

                    composable(Screen.Main.Wallet.route) {
                        WalletScreen(navigate = { navController.navigate(it) })
                    }
                    composable(Screen.Main.Wallet.Receive.route) {
                        WalletReceiveScreen(onBack = { navController.popBackStack() })
                    }
                    composable(Screen.Main.Wallet.Send.route) {
                        WalletSendScreen(
                            onBack = { navController.popBackStack() },
                        )
                    }

                    navigation(
                        startDestination = Screen.Main.Rewards.RewardsMain.route,
                        route = Screen.Main.Rewards.route,
                    ) {
                        composable(Screen.Main.Rewards.RewardsMain.route) {
                            RewardsScreen(navigate = { navController.navigate(it) })
                        }
                        composable(Screen.Main.Rewards.RewardsClaim.route) {
                            RewardsClaimScreen(onBack = { navController.popBackStack() })
                        }
                        composable(
                            Screen.Main.Rewards.RewardsEventsItem.route,
                            arguments = listOf(navArgument("item_id") { type = NavType.StringType })
                        ) {
                            RewardsEventItemScreen(onBack = { navController.popBackStack() })
                        }
                    }

                    composable(Screen.Main.Profile.route) {
                        ProfileScreen(appIcon = appIcon, navigate = { navController.navigate(it) })
                    }
                    composable(Screen.Main.Profile.AuthMethod.route) {
                        AuthMethodScreen(onBack = { navController.popBackStack() })
                    }
                    composable(Screen.Main.Profile.ExportKeys.route) {
                        ExportKeysScreen({ navController.popBackStack() })
                    }
                    composable(Screen.Main.Profile.Language.route) {
                        LanguageScreen(onLanguageChange = {
                            LocaleUtil.updateLocale(context, it.localeTag)
                        }, onBack = { navController.popBackStack() })
                    }
                    composable(Screen.Main.Profile.Theme.route) {
                        ThemeScreen(onBack = { navController.popBackStack() })
                    }
                    composable(Screen.Main.Profile.AppIcon.route) {
                        AppIconScreen(appIcon = appIcon, onAppIconChange = {
                            appIcon = it
                            AppIconUtil.setIcon(context, it)
                        }, onBack = { navController.popBackStack() })
                    }
                    composable(Screen.Main.Profile.Terms.route) {
                        AppWebView(title = stringResource(R.string.terms_of_use),
                            url = Constants.TERMS_URL,
                            onBack = { navController.popBackStack() })
                    }
                    composable(Screen.Main.Profile.Privacy.route) {
                        AppWebView(title = stringResource(R.string.privacy_policy),
                            url = Constants.PRIVACY_URL,
                            onBack = { navController.popBackStack() })
                    }
                }
            }

            if (isModalShown.value) {
                Dialog(onDismissRequest = { mainViewModel.setModalVisibility(false) }) {
                    modalContent.value()
                }
            }

            AppBottomSheet(
                state = enterProgramSheetState,
                fullScreen = true,
                isHeaderEnabled = false,
            ) { hide ->
                EnterProgramFlow(
                    navigate = { navController.navigate(Screen.ScanPassport.ScanPassportPoints.route) },
                    sheetState = enterProgramSheetState,
                    hide = hide
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainViewPreview() {
    MainScreen()
}
