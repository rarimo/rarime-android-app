package com.distributedLab.rarime.modules.rewards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.points.models.PointsEventData
import com.distributedLab.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSkeleton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Screen

@Composable
fun ActiveTaskItem(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    pointEvent: PointsEventData
) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                navigate(
                    Screen.Main.Rewards.RewardsEventsItem.route.replace(
                        "{item_id}",
                        "1", // pointsEvent.id
                    )
                )
            }
    ) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .width(40.dp)
                .height(40.dp)
                .background(RarimeTheme.colors.baseBlack)
        ) {
            AppIcon(
                id = R.drawable.ic_users,
                tint = RarimeTheme.colors.baseWhite
            )
        }

        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = pointEvent.attributes.meta.static.title,
                    style = RarimeTheme.typography.subtitle4,
                    color = RarimeTheme.colors.textPrimary,
                )

                Text (
                    text = pointEvent.attributes.meta.static.shortDescription,
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textSecondary,
                )
            }

            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RewardAmountPreview(amount = pointEvent.attributes.meta.static.reward)

                AppIcon(
                    id = R.drawable.ic_caret_right,
                    tint = RarimeTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
fun ActiveTaskItemSkeleton() {
    Row (
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppSkeleton(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
        )

        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppSkeleton(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                )
                AppSkeleton(
                    modifier = Modifier
                        .width(140.dp)
                        .height(12.dp)
                )
            }

            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppSkeleton(
                    modifier = Modifier
                        .width(50.dp)
                        .height(14.dp)
                )

                AppSkeleton(
                    modifier = Modifier
                        .width(12.dp)
                        .height(28.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun TimeEventsListPreview () {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActiveTaskItem(
            navigate = {},
            pointEvent = CONST_MOCKED_EVENTS_LIST[0]
        )
        ActiveTaskItem(
            navigate = {},
            pointEvent = CONST_MOCKED_EVENTS_LIST[1]
        )
        ActiveTaskItemSkeleton()
    }
}