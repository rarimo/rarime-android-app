package com.rarilabs.rarime.modules.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.ExtIntActionPreview
import com.rarilabs.rarime.api.voting.models.MOCKED_POLL_ITEM
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.modules.home.v3.HomeScreenV3
import com.rarilabs.rarime.modules.intro.IntroScreen
import com.rarilabs.rarime.modules.main.guards.AuthGuard
import com.rarilabs.rarime.modules.maintenance.MaintenanceScreen
import com.rarilabs.rarime.modules.notifications.NotificationsScreen
import com.rarilabs.rarime.modules.passportScan.ScanPassportScreen
import com.rarilabs.rarime.modules.passportVerify.VerifyPassportScreen
import com.rarilabs.rarime.modules.profile.AppIconScreen
import com.rarilabs.rarime.modules.profile.AuthMethodScreen
import com.rarilabs.rarime.modules.profile.ExportKeysScreen
import com.rarilabs.rarime.modules.profile.LanguageScreen
import com.rarilabs.rarime.modules.profile.ProfileScreen
import com.rarilabs.rarime.modules.profile.ThemeScreen
import com.rarilabs.rarime.modules.register.NewIdentityScreen
import com.rarilabs.rarime.modules.security.EnableBiometricsScreen
import com.rarilabs.rarime.modules.security.EnablePasscodeScreen
import com.rarilabs.rarime.modules.security.LockScreen
import com.rarilabs.rarime.modules.security.SetupPasscode
import com.rarilabs.rarime.modules.votes.voteProcessScreen.VoteProcessScreen
import com.rarilabs.rarime.modules.wallet.WalletReceiveScreen
import com.rarilabs.rarime.modules.wallet.WalletScreen
import com.rarilabs.rarime.modules.wallet.WalletSendScreen
import com.rarilabs.rarime.modules.you.ZkIdentityDebugScreen
import com.rarilabs.rarime.modules.you.ZkIdentityScreen
import com.rarilabs.rarime.ui.components.AppWebView
import com.rarilabs.rarime.ui.components.CongratsInvitationModalContent
import com.rarilabs.rarime.util.AppIconUtil
import com.rarilabs.rarime.util.BiometricUtil
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.LocaleUtil
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreenRoutes(
    navController: NavHostController,
    simpleNavigate: (String) -> Unit,
    navigateWithPopUp: (String) -> Unit,
) {
    val mainViewModel = LocalMainViewModel.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var savedNextNavScreen by remember { mutableStateOf<String?>(null) }
    val appIcon by mainViewModel.appIcon.collectAsState()

    val isLocked by mainViewModel.isScreenLocked.collectAsState()

    val extIntDataURI by mainViewModel.extIntDataURI.collectAsState()

    var extIntDataURIState: Pair<Uri?, Long>? by remember {
        mutableStateOf(
            null
        )
    }

    LaunchedEffect(extIntDataURI) {
        if (!isLocked) {
            if (extIntDataURIState?.second != extIntDataURI?.second) {
                extIntDataURIState = extIntDataURI

            }
        }
    }


    fun navigateWithSavedNextNavScreen(route: String) {
        savedNextNavScreen?.let {
            navigateWithPopUp(savedNextNavScreen!!)
            savedNextNavScreen = null
        } ?: run {
            navigateWithPopUp(route)
        }
    }

    key(extIntDataURIState?.second) {
        extIntDataURIState?.first?.let { uri ->
            ExtIntActionPreview(navigate = navigateWithPopUp, dataUri = uri, onError = {
                extIntDataURIState = null
                mainViewModel.setExtIntDataURI(null)
            }, onCancel = {
                extIntDataURIState = null
                mainViewModel.setExtIntDataURI(null)
            }, onSuccess = { extDestination, localDestination ->
                if (!extDestination.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, extDestination.toUri())

                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            context, "No app available to open this link.", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                if (!localDestination.isNullOrEmpty()) {
                    navigateWithPopUp(localDestination)
                }
                extIntDataURIState = null
                mainViewModel.setExtIntDataURI(null)
            })
        }
    }



    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route,
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
    ) {

        composable(Screen.Loading.route) {
            val appLoadingState by mainViewModel.appLoadingStates.collectAsState()
            val isPkInit = remember {
                mainViewModel.getIsPkInit()
            }
            val isScreenLocked by mainViewModel.isScreenLocked.collectAsState()


            LaunchedEffect(appLoadingState, isPkInit, isScreenLocked) {
                when (appLoadingState) {
                    AppLoadingStates.MAINTENANCE -> {
                        navController.navigate(Screen.Maintenance.route) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                        }
                    }

                    AppLoadingStates.LOAD_FAILED -> {
                        delay(50)
                        navController.navigate(Screen.LoadFailed.route) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                        }
                    }

                    AppLoadingStates.LOADED -> {
                        delay(50)

                        val destination = when {
                            !isPkInit -> Screen.Intro.route
                            extIntDataURI != null -> Screen.ExtIntegrator.route
                            else -> Screen.Main.Home.route
                        }


                        navController.navigate(destination) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                        }
                    }

                    AppLoadingStates.LOADING -> {
                    }
                }
            }
            AppLoadingScreen()
        }

        composable(Screen.Maintenance.route) {
            MaintenanceScreen()
        }

        composable(Screen.LoadFailed.route) {
            AppLoadingFailedScreen()
        }

        composable(Screen.Intro.route) {
            ScreenInsetsContainer {
                IntroScreen(
                    onFinish = {
                        coroutineScope.launch(Dispatchers.IO) {
                            mainViewModel.finishIntro()
                        }
                        simpleNavigate(Screen.Main.Home.route)
                    },
                    onNavigate = simpleNavigate
                )
            }
        }

        navigation(
            startDestination = Screen.Register.NewIdentity.route, route = Screen.Register.route
        ) {
            composable(Screen.Register.NewIdentity.route) {
                ScreenInsetsContainer {
                    NewIdentityScreen(onNext = {
                        coroutineScope.launch {
                            mainViewModel.finishIntro()
                            navigateWithPopUp(Screen.Main.Home.route)
                        }

                    }, onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Register.ImportIdentity.route) {
                ScreenInsetsContainer {
                    NewIdentityScreen(
                        isImporting = true,
                        onBack = { navController.popBackStack() },
                        onNext = {
                            coroutineScope.launch {
                                mainViewModel.finishIntro()
                                navigateWithPopUp(Screen.Main.Home.route)
                            }

                        },
                    )
                }
            }
        }

        navigation(
            startDestination = Screen.Passcode.EnablePasscode.route, route = Screen.Passcode.route
        ) {
            composable(Screen.Passcode.EnablePasscode.route) {
                ScreenInsetsContainer {
                    EnablePasscodeScreen(
                        onNext = { simpleNavigate(Screen.Passcode.AddPasscode.route) },
                        onSkip = {
                            mainViewModel.updatePasscodeState(SecurityCheckState.DISABLED)
                            navigateWithSavedNextNavScreen(Screen.Main.route)
                        })
                }
            }

            composable(Screen.Passcode.AddPasscode.route) {
                ScreenInsetsContainer {
                    SetupPasscode(onPasscodeChange = {
                        if (BiometricUtil.isSupported(context)) {
                            navigateWithPopUp(Screen.EnableBiometrics.route)
                        } else {
                            mainViewModel.updateBiometricsState(SecurityCheckState.DISABLED)
                            navigateWithPopUp(Screen.Main.route)
                        }
                    }, onClose = {
                        navController.popBackStack(
                            Screen.Passcode.EnablePasscode.route, false
                        )
                    })
                }
            }
        }

        composable(Screen.NotificationsList.route) {
            ScreenInsetsContainer {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }
        }

        composable(Screen.EnableBiometrics.route) {
            ScreenInsetsContainer {
                EnableBiometricsScreen(onNext = {
                    navigateWithSavedNextNavScreen(Screen.Main.route)
                }, onSkip = {
                    navigateWithSavedNextNavScreen(Screen.Main.route)
                })
            }
        }

        composable(Screen.Lock.route) {
            ScreenInsetsContainer {
                LockScreen(
                    onPass = { it ->
                        navController.popBackStack()
                        val route = savedNextNavScreen ?: Screen.Main.Home.route

                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                        if (extIntDataURI != null) {
                            extIntDataURIState = extIntDataURI
                        }
                        savedNextNavScreen = null
                    })
            }
        }

        //Scan Flow

        composable(Screen.ScanPassport.ScanPassportPoints.route) {
            ScreenInsetsContainer {
                ScanPassportScreen(onClose = {
                    coroutineScope.launch {
                        navigateWithPopUp(Screen.Main.Identity.route)
                    }
                }, onClaim = {
                    coroutineScope.launch {
                        navigateWithPopUp(Screen.Claim.Reserve.route)
                    }
                }, setVisibilityOfBottomBar = {})
            }
        }

        composable(Screen.Claim.Reserve.route) {
            VerifyPassportScreen(
                onSendError = { navigateWithPopUp(Screen.Main.Profile.route) },
                onFinish = {
                    navigateWithPopUp(Screen.Main.route)
                })
        }


        navigation(
            startDestination = Screen.Main.Home.route, route = Screen.Main.route
        ) {

            composable(Screen.Main.Home.route) {
                SharedTransitionLayout {
                    AuthGuard(navigate = simpleNavigate) {
                        HomeScreenV3(
                            navigate = simpleNavigate,
                            navigateWithPopUp = navigateWithPopUp,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            setVisibilityOfBottomBar = { mainViewModel.setBottomBarVisibility(it) },
                        )
                    }
                }
            }

            composable(Screen.Main.DebugIdentity.route) {
                AuthGuard(navigate = simpleNavigate) {
                    ZkIdentityDebugScreen(
                        navigate = simpleNavigate,
                        onClose = {
                            coroutineScope.launch {
                                navigateWithPopUp(Screen.Main.route)
                            }
                        },
                        setBottomBarVisibility = { mainViewModel.setBottomBarVisibility(it) })
                }
            }

            composable(Screen.Main.Identity.route) {
                AuthGuard(navigate = simpleNavigate) {
                    ZkIdentityScreen(navigate = simpleNavigate, onClose = {
                        coroutineScope.launch {
                            navigateWithPopUp(Screen.Main.route)
                        }
                    }, onClaim = {
                        coroutineScope.launch {
                            navigateWithPopUp(Screen.Claim.Specific.route)

                        }
                    }, setBottomBarVisibility = { mainViewModel.setBottomBarVisibility(it) })
                }
            }

            composable(
                Screen.Main.Vote.route,
                arguments = listOf(navArgument("vote_id") { type = NavType.StringType }),
            ) { backStackEntry ->
                val voteId = backStackEntry.arguments?.getString("vote_id")


                voteId?.let {
                    VoteProcessScreen(
                        selectedPoll = MOCKED_POLL_ITEM,
                        onBackClick = { navController.popBackStack() },
                        onVote = {})
                } ?: run {
                    navigateWithPopUp(Screen.Main.Home.route)
                }
            }

            composable(Screen.Main.Wallet.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        WalletScreen(navigate = { simpleNavigate(it) })
                    }
                }
            }
            composable(Screen.Main.Wallet.Receive.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        WalletReceiveScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
            composable(Screen.Main.Wallet.Send.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        WalletSendScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
            composable(Screen.Main.Profile.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        ProfileScreen(
                            appIcon = appIcon, navigate = { simpleNavigate(it) })
                    }
                }
            }
            composable(Screen.Main.Profile.AuthMethod.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        AuthMethodScreen(onBack = { navController.popBackStack() })
                    }
                }
            }

            composable(Screen.Main.Profile.ExportKeys.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        ExportKeysScreen({ navController.popBackStack() })
                    }
                }
            }
            composable(Screen.Main.Profile.Language.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        LanguageScreen(onLanguageChange = {
                            LocaleUtil.updateLocale(context, it.localeTag)
                        }, onBack = { navController.popBackStack() })
                    }
                }
            }

            composable(Screen.Main.Profile.Theme.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        ThemeScreen(onBack = { navController.popBackStack() })
                    }
                }
            }

            composable(Screen.Main.Profile.AppIcon.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        AppIconScreen(appIcon = appIcon, onAppIconChange = {
                            mainViewModel.setAppIcon(it)
                            AppIconUtil.setIcon(context, it)
                        }, onBack = { navController.popBackStack() })
                    }
                }
            }
            composable(Screen.Main.Profile.Terms.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        AppWebView(
                            title = stringResource(R.string.terms_of_use),
                            url = Constants.TERMS_URL,
                            onBack = { navController.popBackStack() })
                    }
                }
            }
            composable(Screen.Main.Profile.Privacy.route) {
                AuthGuard(navigate = navigateWithPopUp) {
                    ScreenInsetsContainer {
                        AppWebView(
                            title = stringResource(R.string.privacy_policy),
                            url = Constants.PRIVACY_URL,
                            onBack = { navController.popBackStack() })
                    }
                }
            }
        }

        composable(
            route = Screen.Invitation.route, arguments = listOf(
                navArgument("code") {
                    type = NavType.StringType
                })
        ) { entry ->
            val code = entry.arguments?.getString("code")
            AuthGuard(
                init = {
                    code?.let {
                        savedNextNavScreen = Screen.Invitation.route.replace("{code}", code)
                    } ?: run {
                        savedNextNavScreen = Screen.Main.route
                    }
                },
                navigate = navigateWithPopUp,
            ) {
                AcceptInvitation(code = code, onFinish = {
                    mainViewModel.setModalVisibility(true)
                    mainViewModel.setModalContent {
                        CongratsInvitationModalContent(
                            onClose = {
                                mainViewModel.setModalVisibility(false)
                            })
                    }

                    navigateWithPopUp(Screen.Main.Home.route)
                }, onError = { navigateWithPopUp(Screen.Main.Home.route) })
            }
        }

        composable(
            route = Screen.ExtIntegrator.route,
        ) {
            AuthGuard(init = {
                savedNextNavScreen = Screen.Main.Home.route
            }, navigate = navigateWithPopUp, content = {
                LaunchedEffect(Unit) {
                    extIntDataURIState = extIntDataURI
                }
            })
        }

    }
}


@Composable
fun AcceptInvitation(
    onFinish: () -> Unit, onError: () -> Unit, code: String?
) {
    val mainViewModel = LocalMainViewModel.current

    val scope = rememberCoroutineScope()

    suspend fun acceptInvitation() {
        ErrorHandler.logDebug("MainScreen", "acceptInvitation: $code")
        try {
            code?.let {
                mainViewModel.acceptInvitation(code)

                //mainViewModel.loadUserDetails()

                onFinish()
            } ?: run {
                throw Exception("No code provided")
            }
        } catch (e: Exception) {
            ErrorHandler.logError("MainScreen", "acceptInvitation: $e", e)
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