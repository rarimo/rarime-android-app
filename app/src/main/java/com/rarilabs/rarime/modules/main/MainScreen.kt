package com.rarilabs.rarime.modules.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.ExtIntActionPreview
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
        }
    }
}

@Composable
fun AppLoadingScreen() {
    val scale = remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
//        while (true) {
//            scale.floatValue = 1.1f
//            delay(500)
//            scale.floatValue = 1f
//            delay(500)
//        }
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
) {
    val mainViewModel = LocalMainViewModel.current

    // Collect states using 'by' to avoid accessing .value
    val passportStatus by mainViewModel.passportStatus.collectAsState()
    val isModalShown by mainViewModel.isModalShown.collectAsState()
    val modalContent by mainViewModel.modalContent.collectAsState()
    val pointsToken by mainViewModel.pointsToken.collectAsState()

    val snackbarHostState by mainViewModel.snackbarHostState.collectAsState()
    val snackbarContent by mainViewModel.snackbarContent.collectAsState()

    val extIntDataURI by mainViewModel.extIntDataURI.collectAsState()

    val enterProgramSheetState = rememberAppSheetState()

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
            if (route == Screen.Main.Rewards.RewardsMain.route) {
                if (currentPointsToken?.balanceDetails?.attributes == null) {
                    currentEnterProgramSheetState.show()
                } else {
                    //TODO: should be return from here
                    navController.navigate(route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
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
            bottomBar = {
                if (mainViewModel.isBottomBarShown.value) {
                    BottomTabBar(
                        currentRoute = currentRoute,
                        onRouteSelected = { navigateWithPopUp(it) }
                    )
                }
            },

            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) {
                    snackbarContent?.let { snackContent ->
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            UiSnackbarDefault(snackContent)
                        }
                    }
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

            key(extIntDataURI) {
                ExtIntActionPreview(dataUri = extIntDataURI)
            }

            MainScreenRoutes(
                navController = navController,
                simpleNavigate = { simpleNavigate(it) },
                navigateWithPopUp = { navigateWithPopUp(it) }
            )

            if (isModalShown) {
                Dialog(onDismissRequest = { mainViewModel.setModalVisibility(false) }) {
                    modalContent()
                }
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
