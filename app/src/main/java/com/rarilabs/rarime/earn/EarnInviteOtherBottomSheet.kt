package com.rarilabs.rarime.earn

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.api.points.models.PointsBalanceDataAttributes
import com.rarilabs.rarime.api.points.models.ReferralCode
import com.rarilabs.rarime.api.points.models.ReferralCodeStatuses
import com.rarilabs.rarime.modules.home.v2.details.DetailsProperties
import com.rarilabs.rarime.modules.rewards.components.RewardsEventItemInvitesCard
import com.rarilabs.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider


val tempPointsBalances = PointsBalanceData(
    id = "",
    type = "",

    attributes = PointsBalanceDataAttributes(
        amount = 0,
        is_disabled = false,
        is_verified = true,
        created_at = 0,
        updated_at = 0,
        rank = 0,
        referral_codes = listOf(
            ReferralCode(
                id = "QrisPfszkps_1",
                status = ReferralCodeStatuses.ACTIVE.value
            ),
            ReferralCode(
                id = "QrisPfszkps_2",
                status = ReferralCodeStatuses.ACTIVE.value,
            ),
            ReferralCode(
                id = "QrisPfszkps_3",
                status = ReferralCodeStatuses.ACTIVE.value
            ),
            ReferralCode(
                id = "QrisPfszkps_4",
                status = ReferralCodeStatuses.ACTIVE.value
            ),
            ReferralCode(
                id = "QrisPfszkps_5",
                status = ReferralCodeStatuses.ACTIVE.value
            ),
//                    ReferralCode(
//                        id = "QrisPfszkps_6",
//                        status = ReferralCodeStatuses.AWAITING.value
//                    )
        ),
        level = 0,
    ),
)


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun InviteOthersContent(
    modifier: Modifier = Modifier,
    properties: DetailsProperties,
    pointsBalance: PointsBalanceData = tempPointsBalances, //TODO: remove default temp value
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val rewardPerInvite = pointsBalance.attributes.referral_codes?.size?.let {
        CONST_MOCKED_EVENTS_LIST[0].attributes.meta.static.reward.div(it)
    } ?: 0L

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        with(sharedTransitionScope) {
            Text(
                style = RarimeTheme.typography.h1,
                color = RarimeTheme.colors.textPrimary,
                text = properties.header,
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(
                        "header-${properties.id}"
                    ), animatedVisibilityScope = animatedContentScope
                )
            )
        }
        with(sharedTransitionScope) {
            Text(
                style = RarimeTheme.typography.additional1,
                text = properties.subTitle,
                color = RarimeTheme.colors.textSecondary,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(
                            "subTitle-${properties.id}"
                        ), animatedVisibilityScope = animatedContentScope
                    ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))


        Text(
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary,
            text = "Share your referral link and get bonuses when your friends join and make a purchase!"
        )

        Column(
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
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

    Spacer(modifier = Modifier.height(24.dp))
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun InviteOthersContentPreview() {


    val properties = DetailsProperties(
        id = 1,
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

    Surface {
        PrevireSharedAnimationProvider { state, anim ->
            InviteOthersContent(
                properties = properties,
                sharedTransitionScope = state,
                animatedContentScope = anim,
                pointsBalance = tempPointsBalances,
            )
        }
    }


}