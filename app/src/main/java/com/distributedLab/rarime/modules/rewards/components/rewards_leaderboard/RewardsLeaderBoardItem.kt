package com.distributedLab.rarime.modules.rewards.components.rewards_leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.modules.rewards.view_models.LeaderBoardItem
import com.distributedLab.rarime.modules.rewards.view_models.MOCKED_LEADER_BOARD_LIST
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil
import com.distributedLab.rarime.util.WalletUtil

@Composable
fun RewardsLeaderBoardItem(
    item: LeaderBoardItem,
    isCurrentUser: Boolean = false,
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column (
                modifier = Modifier.requiredWidth(64.dp)
            )  {
                NumberCircle(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = RarimeTheme.colors.componentPrimary,
                            shape = RoundedCornerShape(100.dp)
                        ),
                    number = item.number
                )
            }

            Column (
                modifier = Modifier.weight(1f)
            ) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = WalletUtil.formatAddress(item.address, 4, 4),
                        style = RarimeTheme.typography.subtitle4,
                        color = RarimeTheme.colors.textPrimary
                    )

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
                    }
                }
            }

            Column (
                horizontalAlignment = Alignment.End
            ) {
                Row (
                    modifier = Modifier
                        .clip(RoundedCornerShape(48.dp))
                        .background(RarimeTheme.colors.componentPrimary)
                        .padding(vertical = 2.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        // TODO: create formatBalance function to format large numbers e.g. 12K, 1.2M, ...etc
                        text = NumberUtil.formatBalance(item.balance),
                        style = RarimeTheme.typography.subtitle5,
                        color = RarimeTheme.colors.textPrimary
                    )
                    AppIcon(id = item.tokenIcon, size = 14.dp)
                }

            }
        }
    }
}

@Preview
@Composable
fun RewardsLeaderBoardItemPreview() {
    Column (
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RewardsLeaderBoardItem(
            item = MOCKED_LEADER_BOARD_LIST[0],
        )
        RewardsLeaderBoardItem(
            item = MOCKED_LEADER_BOARD_LIST[1],
        )
        RewardsLeaderBoardItem(
            item = MOCKED_LEADER_BOARD_LIST[2],
        )
        RewardsLeaderBoardItem(
            item = MOCKED_LEADER_BOARD_LIST[3],
            isCurrentUser = true,
        )
    }
}
