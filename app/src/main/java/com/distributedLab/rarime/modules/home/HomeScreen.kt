package com.distributedLab.rarime.modules.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.home.components.HomeScreenHeader
import com.distributedLab.rarime.modules.home.components.RarimeInfoScreen
import com.distributedLab.rarime.modules.home.components.no_passport.specific.AirdropIntroScreen
import com.distributedLab.rarime.modules.home.components.no_passport.CongratulationsModal
import com.distributedLab.rarime.modules.home.components.no_passport.HomeScreenNoPassportMain
import com.distributedLab.rarime.modules.home.components.no_passport.non_specific.OtherPassportIntroScreen
import com.distributedLab.rarime.modules.home.components.passport.HomeScreenPassportMain
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.ActionCard
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Screen

val LocalHomeViewModel = compositionLocalOf<HomeViewModel> { error("No HomeViewModel provided") }

@Composable
fun HomeScreen(
    navigate: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val passport by homeViewModel.passport

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
        navigate = {}
    )
}
