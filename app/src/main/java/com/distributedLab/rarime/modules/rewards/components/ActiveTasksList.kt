package com.distributedLab.rarime.modules.rewards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.domain.points.PointsEvent
import com.distributedLab.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun ActiveTasksList(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    pointsEvents: List<PointsEvent>
) {
    Column (
        modifier = modifier
    ) {
        pointsEvents.forEachIndexed { idx, item ->
            ActiveTaskItem(navigate = navigate, pointEvent = item)

            if (idx != pointsEvents.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp)
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
            .padding(16.dp)
    ) {
        ActiveTasksList(
            navigate = {},
            pointsEvents = CONST_MOCKED_EVENTS_LIST
        )
    }
}