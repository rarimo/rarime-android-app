package com.distributedLab.rarime.modules.rewards.event_item

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.api.points.models.BaseEvents
import com.distributedLab.rarime.modules.rewards.view_models.RewardsEventItemViewModel
import com.distributedLab.rarime.modules.wallet.WalletRouteLayout
import com.distributedLab.rarime.ui.components.AppSkeleton
import com.distributedLab.rarime.ui.components.HorizontalDivider
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun RewardsEventItemScreen(
    onBack: () -> Unit,
    rewardsEventItemViewModel: RewardsEventItemViewModel = hiltViewModel(),
) {
    val pointsToken by rewardsEventItemViewModel.pointsToken.collectAsState()

    val _pointsEvent = rewardsEventItemViewModel.pointsEventData.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                rewardsEventItemViewModel.loadPointsEvent()
            } catch (e: Exception) {
                Log.e("RewardsEventItemScreen", "Error loading points event", e)
            }
        }
    }

    _pointsEvent.value?.let { pointsEvent ->
        when (pointsEvent.attributes.meta.static.name) {
            BaseEvents.REFERRAL_COMMON.value -> {
                pointsToken?.balanceDetails?.let {
                    RewardsEventItemInvites(
                        pointsEvent = pointsEvent,
                        onBack = onBack,
                        pointsBalance = it
                    )
                }
            }
            else -> {
                RewardsEventItemCommon(pointsEvent = pointsEvent, onBack = onBack)
            }
        }
    } ?: PageSkeleton()
}

@Composable
private fun PageSkeleton() {
    WalletRouteLayout(
        headerModifier = Modifier
            .padding(horizontal = 20.dp),
        onBack = {},
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppSkeleton(modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(32.dp))
            AppSkeleton(modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(16.dp))

            HorizontalDivider()

            AppSkeleton(modifier = Modifier
                .fillMaxWidth()
                .weight(1f))

            repeat(7) {
                AppSkeleton(
                    modifier = Modifier
                        .fillMaxWidth(Random.nextFloat() * (1f - 0.75f) + 0.75f)
                        .height((Random.nextFloat() * (48 - 18) + 18).dp)
                )
            }

            AppSkeleton(modifier = Modifier
                .fillMaxWidth()
                .height(52.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PageSkeletonPreview() {
    PageSkeleton()
}

@Preview
@Composable
fun RewardsEventItemScreenPreview() {
    RewardsEventItemScreen(onBack = {})
}