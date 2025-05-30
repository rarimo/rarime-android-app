package com.rarilabs.rarime.modules.you

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.passportScan.ScanPassportScreen

val LocalZkIdentityScreenViewModel =
    compositionLocalOf<ZkIdentityScreenViewModel> { error("No ZkIdentityScreenViewModel provided") }


@Composable
fun ZkIdentityScreen(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    onClose: () ->Unit,
    onClaim: () ->Unit,
    setVisibilityOfBottomBar: (Boolean) -> Unit,
    zkIdentityScreenViewModel: ZkIdentityScreenViewModel = hiltViewModel()
) {
    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()
    val passport by zkIdentityScreenViewModel.passport.collectAsState()

    CompositionLocalProvider(LocalZkIdentityScreenViewModel provides zkIdentityScreenViewModel) {
        if (passport != null) {
            setVisibilityOfBottomBar(true)
            ZkIdentityPassport(navigate = navigate)
        } else {
            setVisibilityOfBottomBar(false)
            ScanPassportScreen(onClose = onClose, onClaim = onClaim, innerPaddings = innerPaddings)
        }
    }

}
