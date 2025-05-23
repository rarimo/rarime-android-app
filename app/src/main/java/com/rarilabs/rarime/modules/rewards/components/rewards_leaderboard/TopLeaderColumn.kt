package com.rarilabs.rarime.modules.rewards.components.rewards_leaderboard

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.rewards.view_models.MOCKED_LEADER_BOARD_LIST
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.NumberUtil
import com.rarilabs.rarime.util.WalletUtil

data class ContentColors(
    val container: Color,
    val markerWrp: Color,
    val marker: Color,
    val address: Color,
    val balance: Color,
    val tokenIcon: Color,
)

@Composable
fun topLeaderColumnDefaultContentColors(
    container: Color = RarimeTheme.colors.componentPrimary,
    markerWrp: Color = RarimeTheme.colors.componentPrimary,
    marker: Color = RarimeTheme.colors.textSecondary,
    address: Color = RarimeTheme.colors.textSecondary,
    balance: Color = RarimeTheme.colors.textPrimary,
    tokenIcon: Color = RarimeTheme.colors.textPrimary,
): ContentColors {
    return ContentColors(
        container = container,
        markerWrp = markerWrp,
        marker = marker,
        address = address,
        balance = balance,
        tokenIcon = tokenIcon,
    )
}

@Composable
fun TopLeaderColumn(
    modifier: Modifier = Modifier,
    contentColors: ContentColors = topLeaderColumnDefaultContentColors(),
    number: Int = 1,
    address: String,
    balance: Double,
    tokenIcon: Int,
    isCurrentUser: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NumberCircle(
            modifier = Modifier.absoluteOffset(y = 16.dp),
            number = number
        )
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(contentColors.container)
                .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxHeight()
                    .requiredWidth(60.dp)
            ) {
                if (isCurrentUser) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(contentColors.markerWrp)
                            .padding(vertical = 2.dp, horizontal = 10.dp)
                    ) {
                        Text(
                            text = "YOU",
                            style = RarimeTheme.typography.overline3,
                            color = contentColors.marker
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = WalletUtil.formatAddress(address, 4, 4),
                    style = RarimeTheme.typography.caption3,
                    color = contentColors.address
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = NumberUtil.formatBalance(balance),
                        style = RarimeTheme.typography.subtitle5,
                        color = contentColors.balance,
                        textAlign = TextAlign.Center
                    )
                    AppIcon(id = tokenIcon, size = 14.dp, tint = contentColors.tokenIcon)
                }
            }
        }
    }
}

@Preview
@Composable
fun TopLeaderColumnPreview() {
    Row(
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