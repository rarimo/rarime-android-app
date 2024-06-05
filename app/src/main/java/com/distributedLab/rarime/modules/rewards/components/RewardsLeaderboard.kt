package com.distributedLab.rarime.modules.rewards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil
import com.distributedLab.rarime.util.WalletUtil

@Composable
fun TopLeaderColumn(
    modifier: Modifier = Modifier,
    number: Int = 1,
    address: String,
    balance: Double,
    tokenIcon: Int,
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
                .absoluteOffset(y = 16.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(RarimeTheme.colors.backgroundPure)
                .zIndex(1f)
        ) {
            Text(
                text = number.toString(),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary
            )
        }
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(RarimeTheme.colors.componentPrimary)
                .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight().requiredWidth(54.dp)
            ) {
                Text (
                    text = WalletUtil.formatAddress(address, 4, 4),
                    style = RarimeTheme.typography.caption3,
                    color = RarimeTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row (
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text (
                        text = NumberUtil.formatAmount(balance),
                        style = RarimeTheme.typography.subtitle5,
                        color = RarimeTheme.colors.textPrimary
                    )
                    AppIcon(id = tokenIcon, size = 14.dp)
                }
            }
        }
    }
}

@Composable
fun RewardsLeaderBoard () {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TopLeaderColumn(
                modifier = Modifier.requiredHeight(100.dp),
                number = 2,
                address = "0x1234567890abcdef",
                balance = 123.45,
                tokenIcon = R.drawable.ic_rarimo
            )
            TopLeaderColumn(
                modifier = Modifier
                    .requiredHeight(126.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(RarimeTheme.colors.primaryMain), // FIXME: overlap colors,
                number = 1,
                address = "0x1234567890abcdef",
                balance = 123.45,
                tokenIcon = R.drawable.ic_rarimo
            )
            TopLeaderColumn(
                modifier = Modifier
                    .requiredHeight(84.dp),
                number = 3,
                address = "0x1234567890abcdef",
                balance = 123.45,
                tokenIcon = R.drawable.ic_rarimo
            )
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(RarimeTheme.colors.backgroundPure)
        ) {

        }
    }
}

@Preview
@Composable
fun RewardsLeaderBoardPreview() {
    RewardsLeaderBoard()
}