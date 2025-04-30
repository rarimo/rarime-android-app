package com.rarilabs.rarime.modules.main


import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.ExtIntActionPreview
import com.rarilabs.rarime.modules.maintenanceScreen.MaintenanceScreen
import com.rarilabs.rarime.modules.qr.ScanQrScreen
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.UiSnackbarDefault
import com.rarilabs.rarime.ui.components.enter_program.EnterProgramFlow
import com.rarilabs.rarime.ui.components.enter_program.UNSPECIFIED_PASSPORT_STEPS
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.launch

val mainRoutes = listOf(
    Screen.Main.Home.route,
    Screen.Main.Rewards.RewardsMain.route,
    Screen.Main.Wallet.route,
    Screen.Main.Profile.route,
    Screen.Main.Identity.route
)

val LocalMainViewModel = compositionLocalOf<MainViewModel> { error("No MainViewModel provided") }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController
) {
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
                MainScreenContent(
                    navController = navController,
                )
            }

            AppLoadingStates.MAINTENANCE -> {
                MaintenanceScreen()
            }
        }
    }
}

@Composable
fun AppLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(
            modifier = Modifier,
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
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenContent(
    navController: NavHostController,
) {
    val mainViewModel = LocalMainViewModel.current
    val context = LocalContext.current


    // Collect states using 'by' to avoid accessing .value
    val passportStatus by mainViewModel.passportStatus.collectAsState()
    val isModalShown by mainViewModel.isModalShown.collectAsState()
    val modalContent by mainViewModel.modalContent.collectAsState()
    val pointsToken by mainViewModel.pointsToken.collectAsState()

    val snackbarHostState by mainViewModel.snackbarHostState.collectAsState()
    val snackbarContent by mainViewModel.snackbarContent.collectAsState()

    val extIntDataURI by mainViewModel.extIntDataURI.collectAsState()

    val enterProgramSheetState = rememberAppSheetState()
    val qrCodeState = rememberAppSheetState()

    // Use remember to cache navBackStackEntry and currentRoute
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember(navBackStackEntry) {
        derivedStateOf { navBackStackEntry?.destination?.route }
    }

    // Compute shouldShowBottomBar based on currentRoute
    val shouldShowBottomBar by remember(currentRoute) {
        derivedStateOf { currentRoute != null && currentRoute in mainRoutes }
    }

    // Use LaunchedEffect to update bottom bar visibility when shouldShowBottomBar changes
    LaunchedEffect(shouldShowBottomBar) {
        mainViewModel.setBottomBarVisibility(shouldShowBottomBar)
    }

    // Use rememberUpdatedState for pointsToken and enterProgramSheetState
    val pointsTokenState = rememberUpdatedState(pointsToken)
    val enterProgramSheetStateState = rememberUpdatedState(enterProgramSheetState)
    val qrCodeSheetState = rememberUpdatedState(qrCodeState)

    // Define navigation functions using remember to prevent recomposition
    val simpleNavigate = remember(navController) {
        { route: String ->
            navController.navigate(route)
        }
    }

    val navigateWithPopUp = remember(navController) {
        { route: String ->
            val currentPointsToken = pointsTokenState.value
            val currentEnterProgramSheetState = enterProgramSheetStateState.value

            Log.d("URL string", route)
            if (route == Screen.Main.Rewards.RewardsMain.route) {
                if (currentPointsToken?.balanceDetails?.attributes == null) {
                    currentEnterProgramSheetState.show()
                } else {
                    navController.navigate(route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            } else if (
                route == Screen.Main.QrScan.route
            ) {
                val currentQrCodeSheetState = qrCodeSheetState.value
                currentQrCodeSheetState.show()
            } else {
                navController.navigate(route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    restoreState = true
                    launchSingleTop = true
                }
            }
        }
    }

    AppTheme(colorScheme = mainViewModel.colorScheme.value) {
        Scaffold(
            containerColor = RarimeTheme.colors.backgroundPrimary,
            bottomBar = {
                if (mainViewModel.isBottomBarShown.value) {
                    BottomTabBar(
                        modifier = Modifier.navigationBarsPadding(),
                        currentRoute = currentRoute,
                        onRouteSelected = { navigateWithPopUp(it) },
                        onQrCodeRouteSelected = { mainViewModel }
                    )
                }
            },

            snackbarHost = {
                // Show custom snackbar instead of `SnackbarHost`
                snackbarContent?.let { snackContent ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        UiSnackbarDefault(snackContent)

                        // Disappear automatically
                        LaunchedEffect(snackContent) {
                            kotlinx.coroutines.delay(
                                when (snackContent.duration) {
                                    SnackbarDuration.Short -> 2000
                                    SnackbarDuration.Long -> 4000
                                    SnackbarDuration.Indefinite -> Long.MAX_VALUE
                                }
                            )
                            mainViewModel.clearSnackbarOptions()
                        }
                    }
                }
            },
        ) { innerPaddings ->
            mainViewModel.setScreenInsets(
                top = innerPaddings.calculateTopPadding().value,
                bottom = innerPaddings.calculateBottomPadding().value
            )

            ScreenBarsColor(
                colorScheme = mainViewModel.colorScheme.value, route = currentRoute ?: ""
            )

            key(extIntDataURI?.second) {
                extIntDataURI?.first?.let { uri ->
                    // If the "external" route comes in, the handler shows a blank white screen,
                    // thus  it's necessary to immediately redirect back to the Home route.
                    if (currentRoute == "external") {
                        navigateWithPopUp(Screen.Main.Home.route)
                    }
                    ExtIntActionPreview(
                        navigate = navigateWithPopUp,
                        dataUri = uri,
                        onError = {
                            navigateWithPopUp(Screen.Main.Home.route)
                            mainViewModel.setExtIntDataURI(null)
                        },
                        onCancel = {
                            navigateWithPopUp(Screen.Main.Home.route)
                            mainViewModel.setExtIntDataURI(null)
                        },
                        onSuccess = { extDestination, localDestination ->
                            if (!extDestination.isNullOrEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, extDestination.toUri())

                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(
                                        context,
                                        "No app available to open this link.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            if (!localDestination.isNullOrEmpty()) {
                                navigateWithPopUp(localDestination)
                            }

                            mainViewModel.setExtIntDataURI(null)
                        })
                }
            }

            MainScreenRoutes(
                navController = navController,
                simpleNavigate = { simpleNavigate(it) },
                navigateWithPopUp = { navigateWithPopUp(it) },
            )

            if (isModalShown) {
                Dialog(onDismissRequest = { mainViewModel.setModalVisibility(false) }) {
                    modalContent()
                }
            }

            AppBottomSheet(
                shape = RectangleShape,
                state = qrCodeState,
                fullScreen = true,
                isHeaderEnabled = false
            ) {
                ScanQrScreen(
                    onBack = {
                        qrCodeState.hide()
                    },
                    onScan = {
                        val uri = it.toUri()
                        qrCodeState.hide()
                        mainViewModel.setExtIntDataURI(uri)
                    }
                )
            }

            AppBottomSheet(
                state = enterProgramSheetState,
                fullScreen = true,
                isHeaderEnabled = false,
            ) { hide ->
                EnterProgramFlow(
                    initialStep = UNSPECIFIED_PASSPORT_STEPS.ONLY_INVITATION,
                    onFinish = {
                        hide {
                            navController.navigate(Screen.Main.Rewards.RewardsMain.route)
                        }
                    },
                    sheetState = enterProgramSheetState,
                    hide = hide,
                    passportStatus = passportStatus,
                )
            }
        }
    }
}