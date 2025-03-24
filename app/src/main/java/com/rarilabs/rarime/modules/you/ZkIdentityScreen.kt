package com.rarilabs.rarime.modules.you

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

val LocalZkIdentityScreenViewModel =
    compositionLocalOf<ZkIdentityScreenViewModel> { error("No ZkIdentityScreenViewModel provided") }


@Composable
fun ZkIdentityScreen(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    zkIdentityScreenViewModel: ZkIdentityScreenViewModel = hiltViewModel()
) {

    val passport by zkIdentityScreenViewModel.passport.collectAsState()

    CompositionLocalProvider(LocalZkIdentityScreenViewModel provides zkIdentityScreenViewModel) {
        if (passport != null) {
            ZkIdentityPassport(navigate = navigate)
        } else {
            ZkIdentityNoPassport(navigate = navigate)
        }
    }

}
