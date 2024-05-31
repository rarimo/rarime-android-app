package com.distributedLab.rarime.modules.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.ActionCard
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.PrimaryTextButton
import com.distributedLab.rarime.ui.components.SecondaryTextButton
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil
import com.distributedLab.rarime.util.Screen

@Composable
fun HomeScreen(
    navigate: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    var isCongratsModalVisible by remember { mutableStateOf(false) }
    val passport by homeViewModel.passport
    val passportCardLook by homeViewModel.passportCardLook
    val passportIdentifiers by homeViewModel.passportIdentifiers
    val isIncognito by homeViewModel.isIncognito

    val rmoAsset = homeViewModel.rmoAsset.collectAsState()

    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.backgroundPrimary)
                .blur(if (isCongratsModalVisible) 12.dp else 0.dp)
        ) {

            rmoAsset.value?.let {
                Header(walletAsset = it) { navigate(Screen.Main.Wallet.route) }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                if (passport == null) {
                    AirdropCard { navigate(Screen.ScanPassport.route) }
                    OtherPassportCard { navigate(Screen.ScanPassport.route) }
                } else {
                    PassportCard(
                        passport = passport!!,
                        isIncognito = isIncognito,
                        look = passportCardLook,
                        identifiers = passportIdentifiers,
                        onLookChange = { homeViewModel.onPassportCardLookChange(it) },
                        onIncognitoChange = { homeViewModel.onIncognitoChange(it) },
                        onIdentifiersChange = { homeViewModel.onPassportIdentifiersChange(it) }
                    )
                    RarimeCard()
                }
            }
        }

        if (isCongratsModalVisible) {
            CongratulationsModal(
                isClaimed = true,
                onClose = { isCongratsModalVisible = false }
            )
        }
    }
}

@Composable
private fun Header(walletAsset: WalletAsset, onBalanceClick: () -> Unit = {}) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(RarimeTheme.colors.warningLighter)
                .padding(vertical = 4.dp, horizontal = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.beta_launch),
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.warningDark,
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)

        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryTextButton(onClick = onBalanceClick) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.balance_rmo, walletAsset.token.symbol),
                            style = RarimeTheme.typography.body3,
                            color = RarimeTheme.colors.textSecondary
                        )
                        AppIcon(
                            id = R.drawable.ic_caret_right,
                            size = 16.dp,
                            tint = RarimeTheme.colors.textSecondary
                        )
                    }
                }
                Text(
                    text = NumberUtil.formatAmount(walletAsset.balance.value.toDouble()),
                    style = RarimeTheme.typography.h4,
                    color = RarimeTheme.colors.textPrimary
                )
            }
            PrimaryTextButton(
                leftIcon = R.drawable.ic_qr_code,
                onClick = { /*TODO*/ }
            )
        }
    }
}

@Composable
private fun AirdropCard(onPassportScan: () -> Unit) {
    val sheetState = rememberAppSheetState()

    CardContainer {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(72.dp)
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
            ) {
                Text(
                    text = "🇺🇦",
                    style = RarimeTheme.typography.h5,
                    color = RarimeTheme.colors.textPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Text(
                text = stringResource(R.string.airdrop_intro_title),
                style = RarimeTheme.typography.h6,
                textAlign = TextAlign.Center,
                color = RarimeTheme.colors.textPrimary
            )
            Text(
                text = stringResource(R.string.airdrop_intro_description),
                style = RarimeTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = RarimeTheme.colors.textSecondary
            )
            HorizontalDivider()
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = stringResource(R.string.lets_start_btn),
                rightIcon = R.drawable.ic_arrow_right,
                onClick = { sheetState.show() }
            )
        }
    }
    AppBottomSheet(state = sheetState, fullScreen = true) { hide ->
        AirdropIntroScreen(onStart = { hide(onPassportScan) })
    }
}

@Composable
private fun OtherPassportCard(onPassportScan: () -> Unit) {
    val sheetState = rememberAppSheetState()

    ActionCard(
        title = stringResource(R.string.other_passport_card_title),
        description = stringResource(R.string.other_passport_card_description),
        onClick = { sheetState.show() }
    )
    AppBottomSheet(state = sheetState, fullScreen = true) { hide ->
        OtherPassportIntroScreen(onStart = { hide(onPassportScan) })
    }
}

@Composable
private fun RarimeCard() {
    val sheetState = rememberAppSheetState()

    ActionCard(
        title = stringResource(R.string.rarime_card_title),
        description = stringResource(R.string.rarime_card_description),
        onClick = { sheetState.show() }
    )
    AppBottomSheet(state = sheetState, fullScreen = true) { hide ->
        RarimeInfoScreen(onClose = { hide {} })
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        navigate = {}
    )
}
