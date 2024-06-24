package com.rarilabs.rarime.modules.main

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
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
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.modules.passportVerify.ClaimAirdropScreen
import com.rarilabs.rarime.modules.passportVerify.VerifyPassportScreen
import com.rarilabs.rarime.modules.home.HomeScreen
import com.rarilabs.rarime.modules.intro.IntroScreen
import com.rarilabs.rarime.modules.main.guards.AuthGuard
import com.rarilabs.rarime.modules.passportScan.ScanPassportScreen
import com.rarilabs.rarime.modules.profile.AppIconScreen
import com.rarilabs.rarime.modules.profile.AuthMethodScreen
import com.rarilabs.rarime.modules.profile.ExportKeysScreen
import com.rarilabs.rarime.modules.profile.LanguageScreen
import com.rarilabs.rarime.modules.profile.ProfileScreen
import com.rarilabs.rarime.modules.profile.ThemeScreen
import com.rarilabs.rarime.modules.register.NewIdentityScreen
import com.rarilabs.rarime.modules.rewards.RewardsClaimScreen
import com.rarilabs.rarime.modules.rewards.event_item.RewardsEventItemScreen
import com.rarilabs.rarime.modules.rewards.RewardsScreen
import com.rarilabs.rarime.modules.security.EnableBiometricsScreen
import com.rarilabs.rarime.modules.security.EnablePasscodeScreen
import com.rarilabs.rarime.modules.security.LockScreen
import com.rarilabs.rarime.modules.security.SetupPasscode
import com.rarilabs.rarime.modules.wallet.WalletReceiveScreen
import com.rarilabs.rarime.modules.wallet.WalletScreen
import com.rarilabs.rarime.modules.wallet.WalletSendScreen
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.AppWebView
import com.rarilabs.rarime.ui.components.CongratsInvitationModalContent
import com.rarilabs.rarime.ui.components.enter_program.EnterProgramFlow
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.AppIconUtil
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.LocaleUtil
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val mainRoutes = listOf(
    Screen.Main.Home.route,
    Screen.Main.Rewards.RewardsMain.route,
    Screen.Main.Wallet.route,
    Screen.Main.Profile.route
)

val LocalMainViewModel = compositionLocalOf<MainViewModel> { error("No MainViewModel provided") }

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()

    val appLoadingState = mainViewModel.appLoadingState

    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        Log.i("currentRoute", currentRoute ?: "none")
        mainViewModel.setBottomBarVisibility(currentRoute != null && currentRoute in mainRoutes)
    }

    val pointsToken by mainViewModel.pointsToken.collectAsState()

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
        if (route == Screen.Main.Rewards.RewardsMain.route) {
            pointsToken?.balanceDetails?.attributes?.let {} ?: run {
                enterProgramSheetState.show()

                return
            }
        }

        navController.navigate(route) {
            popUpTo(navController.graph.id) { inclusive = true }
            restoreState = true
            launchSingleTop = true
        }
    }

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
                MainScreenContent(
                    navController = navController,
                    currentRoute = currentRoute,
                    navigateWithPopUp = { navigateWithPopUp(it) },
                    startDestination = startDestination,
                    enterProgramSheetState = enterProgramSheetState,
                )
            }
        }
    }
}

@Composable
private fun AppLoadingScreen() {
    val scale = remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            scale.floatValue = 1.1f
            delay(500)
            scale.floatValue = 1f
            delay(500)
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = scale.floatValue,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(
            modifier = Modifier
                .scale(animatedScale),
            id = R.drawable.ic_rarime,
            size = 140.dp,
            tint = RarimeTheme.colors.textPrimary
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
fun MainScreenContent(
    navController: NavHostController,
    currentRoute: String?,
    navigateWithPopUp: (String) -> Unit,
    startDestination: String,
    enterProgramSheetState: AppSheetState,
) {
    val coroutineScope = rememberCoroutineScope()

    val mainViewModel = LocalMainViewModel.current
    val context = LocalContext.current

    val passportStatus = mainViewModel.passportStatus.collectAsState()

    val isModalShown = mainViewModel.isModalShown.collectAsState()
    val modalContent = mainViewModel.modalContent.collectAsState()

    var appIcon by remember { mutableStateOf(AppIconUtil.getIcon(context)) }

    var savedNextNavScreen by remember { mutableStateOf<String?>(null) }

    fun navigateWithSavedNextNavScreen(route: String) {
        Log.i("navigateWithSavedNextNavScreen", "route: $route")

        savedNextNavScreen?.let {
            Log.i("savedNextNavScreen", "route: $it")
            navController.navigate(it)
            savedNextNavScreen = null
        } ?: run {
            navigateWithPopUp(route)
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
                    IntroScreen { navController.navigate(Screen.Register.NewIdentity.route) }
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
                        EnablePasscodeScreen(
                            onNext = { navController.navigate(Screen.Passcode.AddPasscode.route) },
                            onSkip = { navigateWithSavedNextNavScreen(Screen.Main.route) }
                        )
                    }
                    composable(Screen.Passcode.AddPasscode.route) {
                        SetupPasscode(
                            onPasscodeChange = {
                                navigateWithPopUp(Screen.EnableBiometrics.route)
                            },
                            onClose = {
                                navController.popBackStack(
                                    Screen.Passcode.EnablePasscode.route, false
                                )
                            }
                        )
                    }
                }

                composable(Screen.EnableBiometrics.route) {
                    EnableBiometricsScreen(
                        onNext = {
                            navigateWithSavedNextNavScreen(Screen.Main.route)
                        },
                        onSkip = {
                            navigateWithSavedNextNavScreen(Screen.Main.route)
                        }
                    )
                }

                composable(Screen.Lock.route) {
                    LockScreen(
                        onPass = { navigateWithSavedNextNavScreen(Screen.Main.route) }
                    )
                }

                //Scan Flow
                composable(Screen.ScanPassport.ScanPassportSpecific.route) {
                    ScanPassportScreen(
                        onClose = { navController.popBackStack() },
                        onClaim = { navigateWithPopUp(Screen.Claim.Specific.route) }
                    )
                }

                composable(Screen.ScanPassport.ScanPassportPoints.route) {
                    ScanPassportScreen(
                        onClose = { navController.popBackStack() },
                        onClaim = { navigateWithPopUp(Screen.Claim.Reserve.route) }
                    )
                }

                composable(Screen.Claim.Specific.route) {
                    ClaimAirdropScreen {
                        navigateWithPopUp(Screen.Main.route)
                    }
                }

                composable(Screen.Claim.Reserve.route) {
                    VerifyPassportScreen {
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

                composable(
                    route = Screen.Invitation.route,
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = "${BaseConfig.INVITATION_BASE_URL}/r/{code}"
                            action = Intent.ACTION_VIEW
                        }
                    ),
                    arguments = listOf(
                        navArgument("code") {
                            type = NavType.StringType
                        }
                    )
                ) { entry ->
                    val code = entry.arguments?.getString("code")
                    AuthGuard(
                        currentRoute = Screen.Invitation.route,
                        updateSavedNextNavScreen = {
                            code?.let {
                                savedNextNavScreen = currentRoute?.replace("{code}", code)
                            } ?: run {
                                savedNextNavScreen = Screen.Main.route // FIXME: mb add guard to main route
                            }
                       },
                        navigate = navigateWithPopUp,
                    ) {
                        AcceptInvitation(
                            code = code,
                            onFinish = {
                                mainViewModel.setModalVisibility(true)
                                mainViewModel.setModalContent {
                                    CongratsInvitationModalContent(
                                        onClose = {
                                            mainViewModel.setModalVisibility(false)
                                        }
                                    )
                                }

                                navigateWithPopUp(Screen.Main.Home.route)
                           },
                            onError = { navigateWithPopUp(Screen.Main.Home.route) }
                        )
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
                    onFinish = {
                        hide {
                            when (passportStatus.value) {
                                PassportStatus.UNSCANNED -> {
                                    navController.navigate(Screen.ScanPassport.ScanPassportPoints.route)
                                }

                                PassportStatus.WAITLIST -> {
                                    navController.navigate(Screen.Main.Rewards.RewardsMain.route)
                                }

                                else -> {
                                    navController.navigate(Screen.Claim.Reserve.route)
                                }
                            }
                        }
                    },
                    sheetState = enterProgramSheetState,
                    hide = hide,
                    passportStatus = passportStatus.value,
                )
            }
        }
    }
}

@Composable
private fun AcceptInvitation(
    onFinish: () -> Unit,
    onError: () -> Unit,
    code: String?
) {
    val mainViewModel = LocalMainViewModel.current

    val scope = rememberCoroutineScope()

    suspend fun acceptInvitation() {
        Log.i("MainScreen", "acceptInvitation: $code")
        try {
            code?.let {
                mainViewModel.acceptInvitation(code)

                mainViewModel.loadUserDetails()

                onFinish()
            } ?: run {
                throw Exception("No code provided")
            }
        } catch (e: Exception) {
            Log.e("MainScreen", "acceptInvitation: $e")
            onError()
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            acceptInvitation()
        }
    }

    AppLoadingScreen()
}

@Preview
@Composable
private fun MainViewPreview() {
    MainScreen()
}
