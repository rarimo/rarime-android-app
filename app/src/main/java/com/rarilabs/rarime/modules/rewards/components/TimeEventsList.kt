package com.rarilabs.rarime.modules.rewards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun TimeEventsList(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    pointsEventData: List<PointsEventData>
) {
    Column(
        modifier = modifier
    ) {
        pointsEventData.forEachIndexed { idx, item ->
            TimeEventItem(
                modifier = Modifier.fillMaxWidth(),
                navigate = navigate,
                pointsEventData = item,
            )

            if (idx != pointsEventData.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun TimeEventsListSkeleton() {
    Column {

        TimeEventItemSkeleton()
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )
        TimeEventItemSkeleton()
    }
}

@Preview
@Composable
private fun TimeEventsListPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(16.dp)
    ) {
        TimeEventsList(
            navigate = {},
            pointsEventData = CONST_MOCKED_EVENTS_LIST
        )
    }
}

@Preview
@Composable
private fun TimeEventsListSkeletonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(16.dp)
    ) {
        TimeEventsListSkeleton()
    }
}