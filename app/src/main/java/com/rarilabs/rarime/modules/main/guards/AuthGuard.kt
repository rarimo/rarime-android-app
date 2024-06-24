package com.rarilabs.rarime.modules.main.guards

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.util.Screen

@Composable
fun AuthGuard(
    navigate: (String) -> Unit,
    currentRoute: String,
    updateSavedNextNavScreen: (String) -> Unit,
    guardViewModel: GuardViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
//    val isAuthorized by guardViewModel.isAuthorized.collectAsState()

    val isScreenLocked by guardViewModel.isScreenLocked

    val privateKey by guardViewModel.privateKey.collectAsState()

    LaunchedEffect(Unit) {
        updateSavedNextNavScreen(currentRoute)
    }

    if (privateKey != null) {
        if (isScreenLocked) {
            navigate(Screen.Lock.route)
        } else {
            content()
        }
    } else {
        navigate(Screen.Intro.route)
    }
}