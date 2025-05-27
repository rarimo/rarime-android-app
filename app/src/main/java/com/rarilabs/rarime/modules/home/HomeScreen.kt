package com.rarilabs.rarime.modules.home

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.modules.home.HomeViewModel
import com.rarilabs.rarime.modules.home.components.no_passport.HomeScreenNoPassportMain
import com.rarilabs.rarime.modules.home.components.passport.HomeScreenPassportMain

val LocalHomeViewModel = compositionLocalOf<HomeViewModel> { error("No HomeViewModel provided") }

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navigate: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val isShowPassport by homeViewModel.isShowPassport.collectAsState()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )

        LaunchedEffect(Unit) {
            //homeViewModel.generateTestProof()
        }

        LaunchedEffect(Unit) {
            try {
                if (!notificationPermission.status.isGranted) {
                    Log.i("Not granted notification", "Nice")
                    notificationPermission.launchPermissionRequest()
                } else {
                    Log.i("Already granted notification", "Nice")
                }
            } catch(e: Exception) {
                Log.e(e.toString(), e.stackTraceToString(), e)
            }

            try {

            } catch (e: Exception) {
                Log.e("Exep", "xd", e)
            }
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.loadNotifications()
        homeViewModel.loadUserDetails()
    }

    CompositionLocalProvider(LocalHomeViewModel provides homeViewModel) {
        if (isShowPassport) {
            HomeScreenPassportMain(navigate)
        } else {
            HomeScreenNoPassportMain(navigate)
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        navigate = {},
    )
}
