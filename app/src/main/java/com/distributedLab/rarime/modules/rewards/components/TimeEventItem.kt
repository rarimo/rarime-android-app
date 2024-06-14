package com.distributedLab.rarime.modules.rewards.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.points.models.PointsEventData
import com.distributedLab.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.distributedLab.rarime.ui.components.AppSkeleton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.Screen

@Composable
fun TimeEventItem(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    pointsEventData: PointsEventData
) {
    Row(
        modifier = modifier
            .clickable {
                navigate(
                    Screen.Main.Rewards.RewardsEventsItem.route.replace(
                        "{item_id}",
                        "1", // pointsEvent.id
                    )
                )
            },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            // painter = rememberAsyncImagePainter(pointsEvent.meta.static.logo),
            painter = painterResource(id = R.drawable.event_stub),
            contentDescription = pointsEventData.attributes.meta.static.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(64.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(8.dp)),
        )

        Column (
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = pointsEventData.attributes.meta.static.title,
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary,
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RewardAmountPreview(amount = pointsEventData.attributes.meta.static.reward)

                Text(
                    text = "2 days left", // TODO: add date diff
                    style = RarimeTheme.typography.caption2,
                    color = RarimeTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
fun TimeEventItemSkeleton() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppSkeleton(
            modifier = Modifier
                .width(64.dp)
                .height(64.dp),
            cornerRadius = 16f
        )

        Column (
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppSkeleton(
                modifier = Modifier
                    .width(100.dp)
                    .height(14.dp)
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppSkeleton(
                    modifier = Modifier
                        .width(50.dp)
                        .height(14.dp)
                )

                AppSkeleton(
                    modifier = Modifier
                        .width(65.dp)
                        .height(14.dp)
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TimeEventItem(
            modifier = Modifier.fillMaxWidth(),
            navigate = {},
            pointsEventData = CONST_MOCKED_EVENTS_LIST[0]
        )

        TimeEventItemSkeleton()
    }
}