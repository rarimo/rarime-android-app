package com.rarilabs.rarime.modules.home.v2.details

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.modules.rewards.components.RewardsEventItemInvitesCard
import com.rarilabs.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun InviteOthersScreen(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val pointsBalance: PointsBalanceData = tempPointsBalances

    val rewardPerInvite = pointsBalance.attributes.referral_codes?.size?.let {
        CONST_MOCKED_EVENTS_LIST[0].attributes.meta.static.reward.div(it)
    } ?: 0L

    val properties = DetailsProperties(
        id = id,
        header = "Invite",
        subTitle = "Others",
        imageId = R.drawable.invite_groupe_image,
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFCBE7EC),
                Color(0xFFF2F8EE)
            )
        )
    )

    BaseDetailsScreen(
        properties = properties,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onBack = onBack,
        footer = {
            Column(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .scrollable(
                        orientation = Orientation.Vertical,
                        state = rememberScrollableState { delta ->
                            val newValue = delta + 1
                            newValue.coerceIn(0f, 1f)
                        }
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pointsBalance.attributes.referral_codes?.forEach {
                    RewardsEventItemInvitesCard(
                        code = it,
                        rewardAmount = rewardPerInvite,
                        pointsBalance = pointsBalance,
                    )
                }
            }
        }
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun InviteOthersScreenPreview() {

    PrevireSharedAnimationProvider { state, anim ->
        InviteOthersScreen(
            id = 0,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            onBack = {}
        )

    }
}