package com.rarilabs.rarime.modules.home.v2.details

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.rewards.components.RewardsEventItemInvitesCard
import com.rarilabs.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun InviteOthersScreen(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    pointsEvent: PointsEventData?,
    pointsBalance: PointsBalanceData?,
) {

    val backgroundGradient = RarimeTheme.colors.gradient2

    val rewardPerInvite = remember {
        pointsBalance?.attributes?.referral_codes?.size?.let {
            pointsEvent?.attributes?.meta?.static?.reward?.div(
                it
            )
        } ?: 0
    }

    val properties = DetailsProperties(
        id = id,
        header = "Invite",
        subTitle = "Others",
        imageId = R.drawable.invite_groupe_image,
        backgroundGradient = backgroundGradient
    )

    BaseDetailsScreen(
        properties = properties,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onBack = onBack,
        innerPaddings = innerPaddings,
        footer = {
            Column(
                modifier = modifier
                    .padding(top = 24.dp)
                    .verticalScroll(state = rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pointsBalance?.attributes?.referral_codes?.forEach {
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
            onBack = {},
            innerPaddings = mapOf(ScreenInsets.TOP to 23, ScreenInsets.BOTTOM to 12),
            pointsBalance = tempPointsBalances,
            pointsEvent = CONST_MOCKED_EVENTS_LIST[0]
        )

    }
}