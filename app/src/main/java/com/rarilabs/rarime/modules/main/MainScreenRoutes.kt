package com.rarilabs.rarime.modules.main

import android.content.Intent
import android.util.Log
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.modules.home.HomeScreen
import com.rarilabs.rarime.modules.intro.IntroScreen
import com.rarilabs.rarime.modules.main.guards.AuthGuard
import com.rarilabs.rarime.modules.passportScan.ScanPassportScreen
import com.rarilabs.rarime.modules.passportVerify.ClaimAirdropScreen
import com.rarilabs.rarime.modules.passportVerify.VerifyPassportScreen
import com.rarilabs.rarime.modules.profile.AppIconScreen
import com.rarilabs.rarime.modules.profile.AuthMethodScreen
import com.rarilabs.rarime.modules.profile.ExportKeysScreen
import com.rarilabs.rarime.modules.profile.LanguageScreen
import com.rarilabs.rarime.modules.profile.ProfileScreen
import com.rarilabs.rarime.modules.profile.ThemeScreen
import com.rarilabs.rarime.modules.register.NewIdentityScreen
import com.rarilabs.rarime.modules.rewards.RewardsClaimScreen
import com.rarilabs.rarime.modules.rewards.RewardsScreen
import com.rarilabs.rarime.modules.rewards.event_item.RewardsEventItemScreen
import com.rarilabs.rarime.modules.security.EnableBiometricsScreen
import com.rarilabs.rarime.modules.security.EnablePasscodeScreen
import com.rarilabs.rarime.modules.security.LockScreen
import com.rarilabs.rarime.modules.security.SetupPasscode
import com.rarilabs.rarime.modules.wallet.WalletReceiveScreen
import com.rarilabs.rarime.modules.wallet.WalletScreen
import com.rarilabs.rarime.modules.wallet.WalletSendScreen
import com.rarilabs.rarime.ui.components.AppWebView
import com.rarilabs.rarime.ui.components.CongratsInvitationModalContent
import com.rarilabs.rarime.util.AppIconUtil
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.LocaleUtil
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.launch

@Composable
fun MainScreenRoutes(
    navController: NavHostController,
    navigateWithPopUp: (String) -> Unit,
) {
    val mainViewModel = LocalMainViewModel.current
    val context = LocalContext.current
    var appIcon by remember { mutableStateOf(AppIconUtil.getIcon(context)) }
    val coroutineScope = rememberCoroutineScope()
    var savedNextNavScreen by remember { mutableStateOf<String?>(null) }

    fun navigateWithSavedNextNavScreen(route: String) {
        savedNextNavScreen?.let {
            navigateWithPopUp(it)
            savedNextNavScreen = null
        } ?: run {
            navigateWithPopUp(route)
        }
    }

    NavHost(
        navController,
        startDestination = Screen.Main.route,
        enterTransition = { fadeIn() },
        exitTransition = { ExitTransition.None },
    ) {
        composable(Screen.Intro.route) {
            IntroScreen { navigateWithPopUp(Screen.Register.NewIdentity.route) }
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
                            navigateWithPopUp(Screen.Passcode.route)
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
                    onNext = { navigateWithPopUp(Screen.Passcode.AddPasscode.route) },
                    onSkip = {
                        mainViewModel.updatePasscodeState(SecurityCheckState.DISABLED)
                        navigateWithSavedNextNavScreen(Screen.Main.route)
                    }
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
                onClose = { navigateWithPopUp(Screen.Main.route) },
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
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    HomeScreen(navigate = { navigateWithPopUp(it) })
                }
            }

            composable(Screen.Main.Wallet.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    WalletScreen(navigate = { navigateWithPopUp(it) })
                }
            }
            composable(Screen.Main.Wallet.Receive.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    WalletReceiveScreen(onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Main.Wallet.Send.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    WalletSendScreen(onBack = { navController.popBackStack() })
                }
            }

            navigation(
                startDestination = Screen.Main.Rewards.RewardsMain.route,
                route = Screen.Main.Rewards.route,
            ) {
                composable(Screen.Main.Rewards.RewardsMain.route) {
                    AuthGuard(
                        navigate = navigateWithPopUp,
                    ) {
                        RewardsScreen(navigate = { navigateWithPopUp(it) })
                    }
                }
                composable(Screen.Main.Rewards.RewardsClaim.route) {
                    AuthGuard(
                        navigate = navigateWithPopUp,
                    ) {
                        RewardsClaimScreen(onBack = { navController.popBackStack() })
                    }
                }
                composable(
                    Screen.Main.Rewards.RewardsEventsItem.route,
                    arguments = listOf(navArgument("item_id") { type = NavType.StringType })
                ) {
                    AuthGuard(
                        navigate = navigateWithPopUp,
                    ) {
                        RewardsEventItemScreen(onBack = { navController.popBackStack() })
                    }
                }
            }

            composable(Screen.Main.Profile.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    ProfileScreen(
                        appIcon = appIcon,
                        navigate = { navigateWithPopUp(it) })
                }
            }
            composable(Screen.Main.Profile.AuthMethod.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    AuthMethodScreen(onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Main.Profile.ExportKeys.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    ExportKeysScreen({ navController.popBackStack() })
                }
            }
            composable(Screen.Main.Profile.Language.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    LanguageScreen(onLanguageChange = {
                        LocaleUtil.updateLocale(context, it.localeTag)
                    }, onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Main.Profile.Theme.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    ThemeScreen(onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Main.Profile.AppIcon.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    AppIconScreen(appIcon = appIcon, onAppIconChange = {
                        appIcon = it
                        AppIconUtil.setIcon(context, it)
                    }, onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Main.Profile.Terms.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    AppWebView(title = stringResource(R.string.terms_of_use),
                        url = Constants.TERMS_URL,
                        onBack = { navController.popBackStack() })
                }
            }
            composable(Screen.Main.Profile.Privacy.route) {
                AuthGuard(
                    navigate = navigateWithPopUp,
                ) {
                    AppWebView(title = stringResource(R.string.privacy_policy),
                        url = Constants.PRIVACY_URL,
                        onBack = { navController.popBackStack() })
                }
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
                init = {
                    code?.let {
                        savedNextNavScreen = Screen.Invitation.route.replace("{code}", code)
                    } ?: run {
                        savedNextNavScreen = Screen.Main.route
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
}

@Composable
fun AcceptInvitation(
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