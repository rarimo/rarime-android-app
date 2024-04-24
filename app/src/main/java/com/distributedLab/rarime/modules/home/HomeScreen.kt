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
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.main.Screen
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.models.PersonDetails
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

@Composable
fun HomeScreen(navigate: (String) -> Unit) {
    // TODO: Replace with real data
    var passport: EDocument? by remember {
        mutableStateOf(
            EDocument(
                personDetails = PersonDetails(
                    name = "John",
                    surname = "Doe",
                    birthDate = "01.01.1990",
                    nationality = "USA",
                    serialNumber = "123456789",
                    faceImageInfo = null
                )
            )
        )
    }
    // TODO: Use data from storage
    var passportCardLook by remember { mutableStateOf(PassportCardLook.BLACK) }
    var isIncognito by remember { mutableStateOf(false) }
    // TODO: Use view model
    var isCongratsModalVisible by remember { mutableStateOf(false) }
    val balance by remember { mutableStateOf(0.0) }

    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.backgroundPrimary)
                .blur(if (isCongratsModalVisible) 12.dp else 0.dp)
        ) {
            Header(balance = balance) { navigate(Screen.Main.Wallet.route) }
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
                        onLookChange = { passportCardLook = it },
                        onIncognitoChange = { isIncognito = it },
                        onDelete = { passport = null }
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
private fun Header(balance: Double, onBalanceClick: () -> Unit = {}) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            text = stringResource(R.string.beta_launch),
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.warningDark,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(RarimeTheme.colors.warningLighter)
                .padding(vertical = 4.dp, horizontal = 20.dp)
        )
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
                            text = stringResource(R.string.balance_rmo),
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
                    text = NumberUtil.formatAmount(balance),
                    style = RarimeTheme.typography.h4,
                    color = RarimeTheme.colors.textPrimary
                )
            }
            PrimaryTextButton(onClick = { /*TODO*/ }) {
                AppIcon(id = R.drawable.ic_qr_code)
            }
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
    HomeScreen {}
}
