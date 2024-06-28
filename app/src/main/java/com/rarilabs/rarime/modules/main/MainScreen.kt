package com.rarilabs.rarime.modules.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.enter_program.EnterProgramFlow
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Screen
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
) {
    val mainViewModel = LocalMainViewModel.current

    val passportStatus = mainViewModel.passportStatus.collectAsState()

    val isModalShown = mainViewModel.isModalShown.collectAsState()
    val modalContent = mainViewModel.modalContent.collectAsState()

    val pointsToken by mainViewModel.pointsToken.collectAsState()

    val enterProgramSheetState = rememberAppSheetState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        mainViewModel.setBottomBarVisibility(currentRoute != null && currentRoute in mainRoutes)
    }

    fun simpleNavigate(route: String) {
        navController.navigate(route)
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

            MainScreenRoutes(
                navController = navController,
                simpleNavigate = { simpleNavigate(it) },
                navigateWithPopUp = { navigateWithPopUp(it) }
            )

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
