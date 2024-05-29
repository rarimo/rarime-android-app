package com.distributedLab.rarime.modules.wallet.walletTokens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun WalletTokenCard() {
    Row (
        modifier = Modifier
            // TODO: colors.Container?
            .background(RarimeTheme.colors.baseWhite)
            .requiredWidthIn(min = 300.dp)
            .padding(16.dp)
            // TODO: clip self
//            .clip(RoundedCornerShape(24.dp))
//            .border(2.dp, RarimeTheme.colors.baseWhite, RoundedCornerShape(24.dp))
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(RarimeTheme.colors.baseBlack)
                )

                Text(
                    text = "Total " + BaseConfig.DENOM,
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
            }

            // FIXME: hotfix?
            Spacer(modifier = Modifier.width(4.dp))

            Column {
                Text(
                    text = "3",
                    color = RarimeTheme.colors.textPrimary,
                    style = RarimeTheme.typography.subtitle4
                )
                Text(
                    text = "---",
                    color = RarimeTheme.colors.textSecondary,
                    style = RarimeTheme.typography.caption3
                )
            }
        }
    }
}

@Preview
@Composable
private fun WalletTokenCardPreview() {
    WalletTokenCard()
}