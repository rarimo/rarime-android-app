package com.distributedLab.rarime.modules.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.tokens.Erc20Token
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.SecondaryTextButton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil

@Composable
fun HomeScreenHeader(
    walletAsset: WalletAsset,
    onBalanceClick: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
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
                    text = NumberUtil.formatAmount(walletAsset.humanBalance()),
                    style = RarimeTheme.typography.h4,
                    color = RarimeTheme.colors.textPrimary
                )
            }
        }
    }
}

@Preview
@Composable
fun HeaderPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        HomeScreenHeader(walletAsset = WalletAsset("0x000000", Erc20Token("0x00000000")))
    }
}