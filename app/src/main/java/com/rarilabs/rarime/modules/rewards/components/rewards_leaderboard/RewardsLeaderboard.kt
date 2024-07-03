package com.rarilabs.rarime.modules.rewards.components.rewards_leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.rewards.view_models.LeaderBoardItem
import com.rarilabs.rarime.modules.rewards.view_models.MOCKED_LEADER_BOARD_LIST
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun RewardsLeaderBoard (
    leaderboardList: List<LeaderBoardItem>,
    userLeaderBoardItem: LeaderBoardItem,
) {
    val scrollState = rememberLazyListState()

    var topThree: List<LeaderBoardItem> by remember { mutableStateOf(emptyList()) }
    var rest: List<LeaderBoardItem> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(Unit) {
        if (leaderboardList.size >= 3) {
            topThree = listOf(
                leaderboardList[1],
                leaderboardList[0],
                leaderboardList[2]
            )
            rest = leaderboardList.drop(3)
        } else {
            topThree = leaderboardList
        }
    }

    val topThreeContainerStylesMap = mapOf(
        1 to Modifier
            .requiredHeight(146.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        2 to Modifier.requiredHeight(120.dp),
        3 to Modifier.requiredHeight(104.dp),
    )

    val topThreeContentStylesMap = mapOf(
        1 to topLeaderColumnDefaultContentColors(
            container = RarimeTheme.colors.primaryMain,
            address = RarimeTheme.colors.baseBlack.copy(alpha = 0.6f),
            balance = RarimeTheme.colors.baseBlack,
            tokenIcon = RarimeTheme.colors.baseBlack,
        ),
        2 to topLeaderColumnDefaultContentColors(),
        3 to topLeaderColumnDefaultContentColors(),
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(22.dp))
        // FIXME: overlapped a close btn
        Text (
            text = stringResource(id = R.string.leaderboard_title),
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(40.dp))

        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                topThree.forEachIndexed { idx, it ->
                    TopLeaderColumn(
                        modifier = topThreeContainerStylesMap[it.number]!!,
                        contentColors = topThreeContentStylesMap[it.number]!!,
                        number = it.number,
                        address = it.address,
                        balance = it.balance,
                        tokenIcon = R.drawable.ic_rarimo,
                        isCurrentUser = it.address == userLeaderBoardItem.address,
                    )
                }
            }
            Column (
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(RarimeTheme.colors.backgroundPure)
                    .absolutePadding(top = 0.dp, left = 20.dp, right = 20.dp, bottom = 0.dp),
            ) {
                Column {
                    RewardsLeaderBoardHead()
                    HorizontalDivider()
                }

                if (rest.isNotEmpty()) {
                    LazyColumn (
                        modifier = Modifier.weight(1f),
                        state = scrollState,
                    ) {
                        itemsIndexed(rest) { idx, it ->
                            RewardsLeaderBoardItem(
                                it,
                                isCurrentUser = it.address == userLeaderBoardItem.address,
                            )

                            if (idx != rest.size - 1) {
                                HorizontalDivider()
                            }
                        }
                    }
                }

                Column () {
                    HorizontalDivider()
                    RewardsLeaderBoardItem(userLeaderBoardItem, true)
                }
            }
        }
    }
}

@Preview
@Composable
fun RewardsLeaderBoardPreview() {
    RewardsLeaderBoard(MOCKED_LEADER_BOARD_LIST.take(4), MOCKED_LEADER_BOARD_LIST[6])
}