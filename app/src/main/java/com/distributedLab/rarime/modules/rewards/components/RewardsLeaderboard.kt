package com.distributedLab.rarime.modules.rewards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil
import com.distributedLab.rarime.util.WalletUtil

data class LeaderBoardItem (
    val number: Int,
    val address: String,
    val balance: Double,
    val tokenIcon: Int,
)

@Composable
fun NumberCircle (
    modifier: Modifier = Modifier,
    number: Int
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(32.dp)
            .height(32.dp)
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
}

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
fun RewardsLeaderBoardHead() {
    Row (
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column (
                modifier = Modifier.requiredWidth(64.dp)
            ) {
                Text(
                    text = "Places".uppercase(),
                    style = RarimeTheme.typography.overline3,
                    color = RarimeTheme.colors.textSecondary
                )
            }

            Column (
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Incognito id".uppercase(),
                    style = RarimeTheme.typography.overline3,
                    color = RarimeTheme.colors.textSecondary
                )
            }

            Column (
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "RMO",
                    style = RarimeTheme.typography.overline3,
                    color = RarimeTheme.colors.textSecondary
                )
            }
        }
    }
}

val MOCKED_USER_ADDRESS = "memememememememememememememememememmemememe"

@Composable
fun RewardsLeaderBoardItem(item: LeaderBoardItem) {
    val isCurrentUser = item.address == MOCKED_USER_ADDRESS

    Row (
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
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
                        text = NumberUtil.formatAmount(item.balance),
                        style = RarimeTheme.typography.subtitle5,
                        color = RarimeTheme.colors.textPrimary
                    )
                    AppIcon(id = item.tokenIcon, size = 14.dp)
                }

            }
        }
    }
}

@Composable
fun RewardsLeaderBoard () {
    val scrollState = rememberLazyListState()

    val leaderboardList = listOf(
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 1235566777888.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 122343.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.0,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 1.0,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = MOCKED_USER_ADDRESS,
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
        LeaderBoardItem(
            number = 0,
            address = "0x1234567890abcdef",
            balance = 123.45,
            tokenIcon = R.drawable.ic_rarimo
        ),
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(22.dp))
        // FIXME: overlapped a close btn
        Text (
            text = "Leaderboard",
            style = RarimeTheme.typography.subtitle4,
        )
        Spacer(modifier = Modifier.weight(1f))
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
                .fillMaxHeight(0.7f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(RarimeTheme.colors.backgroundPure)
                .absolutePadding(top = 0.dp, left = 20.dp, right = 20.dp, bottom = 0.dp),
        ) {
            Column () {
                RewardsLeaderBoardHead()
                HorizontalDivider()
            }
            LazyColumn (
                modifier = Modifier.weight(1f),
                state = scrollState,
            ) {
                itemsIndexed(leaderboardList) { idx, it ->
                    RewardsLeaderBoardItem(it)

                    if (idx != leaderboardList.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
            leaderboardList.find { it.address == MOCKED_USER_ADDRESS }?.let {
                Column () {
                    HorizontalDivider()
                    RewardsLeaderBoardItem(it)
                }
            }
        }
    }
}

@Preview
@Composable
fun RewardsLeaderBoardPreview() {
    RewardsLeaderBoard()
}