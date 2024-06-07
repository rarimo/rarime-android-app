package com.distributedLab.rarime.modules.rewards.components.rewards_leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
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
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.rewards.view_models.LeaderBoardItem
import com.distributedLab.rarime.modules.rewards.view_models.MOCKED_LEADER_BOARD_LIST
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun RewardsLeaderBoard (
    leaderboardList: List<LeaderBoardItem>,
    userAddress: String,
) {
    val scrollState = rememberLazyListState()

    val topThree = leaderboardList.take(3)
    val rest = leaderboardList.drop(3)

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
                modifier = Modifier.requiredHeight(120.dp),
                number = 2,
                address = topThree[1].address,
                balance = topThree[1].balance,
                tokenIcon = R.drawable.ic_rarimo,
                isCurrentUser = topThree[1].address == userAddress,
            )
            TopLeaderColumn(
                modifier = Modifier
                    .requiredHeight(146.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(RarimeTheme.colors.primaryMain),
                number = 1,
                address = topThree[0].address,
                balance = topThree[0].balance,
                tokenIcon = R.drawable.ic_rarimo,
                isCurrentUser = topThree[0].address == userAddress,
            )
            TopLeaderColumn(
                modifier = Modifier
                    .requiredHeight(104.dp),
                number = 3,
                address = topThree[2].address,
                balance = topThree[2].balance,
                tokenIcon = R.drawable.ic_rarimo,
                isCurrentUser = topThree[2].address == userAddress,
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
                itemsIndexed(rest) { idx, it ->
                    RewardsLeaderBoardItem(
                        it,
                        isCurrentUser = it.address == userAddress,
                    )

                    if (idx != rest.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
            rest.find { it.address == userAddress }?.let {
                Column () {
                    HorizontalDivider()
                    RewardsLeaderBoardItem(it, true)
                }
            }
        }
    }
}

@Preview
@Composable
fun RewardsLeaderBoardPreview() {
    RewardsLeaderBoard(MOCKED_LEADER_BOARD_LIST, MOCKED_LEADER_BOARD_LIST[5].address)
}