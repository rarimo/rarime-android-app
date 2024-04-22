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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.PassportIntroScreen
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

@Composable
fun HomeScreen(onPassportScan: () -> Unit) {
    val hasPassport = false

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        Header()
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            if (hasPassport) {
                // TODO: Add passport card
                RarimeCard()
            } else {
                AirdropCard(onPassportScan)
                OtherPassportsCard(onPassportScan)
            }
        }
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
            text = "Beta launch",
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
                SecondaryTextButton(onClick = { /*TODO*/ }) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Balance: RMO",
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
                    text = "0",
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
                text = "Programable Airdrop",
                style = RarimeTheme.typography.h6,
                textAlign = TextAlign.Center,
                color = RarimeTheme.colors.textPrimary
            )
            Text(
                text = "Beta launch is focused on distributing tokens to Ukrainian identity holders",
                style = RarimeTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = RarimeTheme.colors.textSecondary
            )
            HorizontalDivider()
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = "Let's Start",
                rightIcon = R.drawable.ic_arrow_right,
                onClick = { sheetState.show() }
            )
        }
    }
    AppBottomSheet(state = sheetState) { hide ->
        PassportIntroScreen(onStart = { hide(onPassportScan) })
    }
}

@Composable
private fun OtherPassportsCard(onPassportScan: () -> Unit) {
    val sheetState = rememberAppSheetState()

    ActionCard(
        title = "Other passport holders",
        description = "Join a waitlist",
        onClick = { sheetState.show() }
    )
    AppBottomSheet(state = sheetState) { hide ->
        PassportIntroScreen(onStart = { hide(onPassportScan) })
    }
}

@Composable
private fun RarimeCard() {
    val sheetState = rememberAppSheetState()

    ActionCard(
        title = "RARIME",
        description = "Learn more about RariMe App",
        onClick = { sheetState.show() }
    )
    AppBottomSheet(state = sheetState) { hide ->
        Column {
            Text("Rarime sheet content")
            PrimaryButton(text = "Okay", onClick = { hide {} })
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    HomeScreen(onPassportScan = {})
}
