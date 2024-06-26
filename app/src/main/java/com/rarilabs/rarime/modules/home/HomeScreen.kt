package com.rarilabs.rarime.modules.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.modules.home.components.no_passport.HomeScreenNoPassportMain
import com.rarilabs.rarime.modules.home.components.passport.HomeScreenPassportMain

val LocalHomeViewModel = compositionLocalOf<HomeViewModel> { error("No HomeViewModel provided") }

@Composable
fun HomeScreen(
    navigate: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val passport by homeViewModel.passport.collectAsState()

    CompositionLocalProvider(LocalHomeViewModel provides homeViewModel) {
        if (passport == null) {
            HomeScreenNoPassportMain(navigate)
        } else {
            HomeScreenPassportMain(navigate)
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
