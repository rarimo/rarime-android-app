package com.rarilabs.rarime.modules.wallet.walletTokens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.data.tokens.PreviewerToken
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.NumberUtil

@Composable
fun WalletTokenCard(walletAsset: WalletAsset) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

        modifier = Modifier
            .requiredWidthIn(min = 300.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, RarimeTheme.colors.backgroundPure, RoundedCornerShape(24.dp))
            .background(RarimeTheme.colors.backgroundPure)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .border(1.dp, RarimeTheme.colors.backgroundPure, RoundedCornerShape(999.dp))
                    .background(RarimeTheme.colors.baseBlack),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(id = walletAsset.getToken().icon, tint = RarimeTheme.colors.baseWhite)
            }

            Text(
                text = walletAsset.getTokenSymbol().uppercase(),
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = NumberUtil.formatAmount(walletAsset.humanBalance()),
                color = RarimeTheme.colors.textPrimary,
                style = RarimeTheme.typography.subtitle4
            )
 
        }
    }
}

@Preview
@Composable
private fun WalletTokenCardPreview() {

    WalletTokenCard(WalletAsset("", PreviewerToken("", "Reserved RMO", "RRMO")))
}