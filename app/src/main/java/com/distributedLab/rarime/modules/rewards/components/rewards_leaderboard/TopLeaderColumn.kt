package com.distributedLab.rarime.modules.rewards.components.rewards_leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.rewards.view_models.MOCKED_LEADER_BOARD_LIST
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
    isCurrentUser: Boolean = false,
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NumberCircle(
            modifier = Modifier.absoluteOffset(y = 16.dp),
            number = number
        )
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(RarimeTheme.colors.componentPrimary)
                .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxHeight()
                    .requiredWidth(54.dp)
            ) {
                if (isCurrentUser) {
                    Box (
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(RarimeTheme.colors.componentPrimary)
                            .padding(vertical = 2.dp, horizontal = 10.dp)
                    ) {
                        Text (
                            text = "You",
                            style = RarimeTheme.typography.overline3,
                            color = RarimeTheme.colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

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
                        text = NumberUtil.formatBalance(balance),
                        style = RarimeTheme.typography.subtitle5,
                        color = RarimeTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    AppIcon(id = tokenIcon, size = 14.dp)
                }
            }
        }
    }
}

@Preview
@Composable
fun TopLeaderColumnPreview() {
    Row (
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        TopLeaderColumn(
            modifier = Modifier.requiredHeight(100.dp),
            number = 2,
            address = MOCKED_LEADER_BOARD_LIST[0].address,
            balance = MOCKED_LEADER_BOARD_LIST[0].balance,
            tokenIcon = R.drawable.ic_rarimo
        )
        TopLeaderColumn(
            modifier = Modifier
                .requiredHeight(126.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(RarimeTheme.colors.primaryMain),
            number = 1,
            address = MOCKED_LEADER_BOARD_LIST[1].address,
            balance = MOCKED_LEADER_BOARD_LIST[1].balance,
            tokenIcon = R.drawable.ic_rarimo,
            isCurrentUser = true
        )
        TopLeaderColumn(
            modifier = Modifier
                .requiredHeight(84.dp),
            number = 3,
            address = MOCKED_LEADER_BOARD_LIST[2].address,
            balance = MOCKED_LEADER_BOARD_LIST[2].balance,
            tokenIcon = R.drawable.ic_rarimo
        )
    }
}