package com.rarilabs.rarime.modules.main.guards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.util.Screen

@Composable
fun AuthGuard(
    navigate: (String) -> Unit,
    init: () -> Unit = {},
    guardViewModel: GuardViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val isScreenLocked by guardViewModel.isScreenLocked.collectAsState()
    val biometricsState by guardViewModel.biometricsState
    val passcodeState by guardViewModel.passcodeState

    val privateKey by guardViewModel.privateKey.collectAsState()

    LaunchedEffect(Unit) {
        init()
    }

    if (privateKey != null) {
        if (isScreenLocked) {
            navigate(Screen.Lock.route)
        } else {
            if (passcodeState.value == SecurityCheckState.UNSET.value) {
                navigate(Screen.Passcode.route)
            } else {
                content()
            }
        }
    } else {
        navigate(Screen.Intro.route)
    }
}