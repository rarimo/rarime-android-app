package com.distributedLab.rarime.modules.wallet.walletTokens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.modules.wallet.WalletScreen
import com.distributedLab.rarime.ui.components.StepIndicator
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun WalletTokensList() {
    Column (
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Assets (2)".uppercase(),
                style = RarimeTheme.typography.overline2,
                color = RarimeTheme.colors.textSecondary
            )

            // TODO: handle case, when tokens is too much to fit on the screen
            // TODO: add bullet click handler to scroll to specific token card
            StepIndicator(itemsCount = 5, selectedIndex = 1)
        }

        var scrollState = rememberScrollState()

        Row (
            modifier = Modifier
                .horizontalScroll(scrollState)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WalletTokenCard()
            WalletTokenCard()
            WalletTokenCard()
            WalletTokenCard()
            WalletTokenCard()
        }
    }
}

@Preview
@Composable
private fun WalletTokensListPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        WalletTokensList()
    }
}