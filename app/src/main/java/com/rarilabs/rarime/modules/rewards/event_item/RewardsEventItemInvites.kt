package com.rarilabs.rarime.modules.rewards.event_item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.api.points.models.PointsBalanceDataAttributes
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.api.points.models.ReferralCode
import com.rarilabs.rarime.api.points.models.ReferralCodeStatuses
import com.rarilabs.rarime.modules.rewards.components.RewardsEventItemInvitesCard
import com.rarilabs.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.rarilabs.rarime.modules.wallet.WalletRouteLayout
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun RewardsEventItemInvites(
    onBack: () -> Unit,
    pointsEvent: PointsEventData,
    pointsBalance: PointsBalanceData,
) {
    val rewardPerInvite = pointsBalance.attributes.referral_codes?.size?.let { pointsEvent.attributes.meta.static.reward.div(it) } ?: 0

    WalletRouteLayout(
        headerModifier = Modifier
            .padding(horizontal = 20.dp),
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .zIndex(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.rewards_event_item_invites_title),
                    style = RarimeTheme.typography.h4,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.rewards_event_item_invites_subtitle),
                    style = RarimeTheme.typography.body2,
                    color = RarimeTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(100.dp))

            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.invite_screen_bg),
                        contentDescription = "decor",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .width(345.dp)
                            .height(200.dp)
                            .absoluteOffset(y = -150.dp, x = 100.dp)
                            .zIndex(0f)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .zIndex(1f)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(RarimeTheme.colors.backgroundPure)
                            .absolutePadding(
                                top = 24.dp,
                                left = 24.dp,
                                right = 24.dp,
                                bottom = 0.dp,
                            )
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.rewards_event_item_invites_status,
                                pointsBalance.attributes.referral_codes?.filter { it.status != ReferralCodeStatuses.ACTIVE.value }?.size ?: 0,
                                pointsBalance.attributes.referral_codes?.size ?: 0
                            ),
                            style = RarimeTheme.typography.subtitle3
                        )
                        Text(
                            text = stringResource(id = R.string.rewards_event_item_invites_status_subtitle),
                            style = RarimeTheme.typography.body3,
                            color = RarimeTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardsEventItemInvitesPreview() {
    RewardsEventItemInvites(
        onBack = {},
        pointsEvent = CONST_MOCKED_EVENTS_LIST[0],
        pointsBalance = PointsBalanceData(
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
                        status = ReferralCodeStatuses.REWARDED.value
                    ),
                    ReferralCode(
                        id = "QrisPfszkps_2",
                        status = ReferralCodeStatuses.CONSUMED.value,
                    ),
                    ReferralCode(
                        id = "QrisPfszkps_3",
                        status = ReferralCodeStatuses.ACTIVE.value
                    ),
                    ReferralCode(
                        id = "QrisPfszkps_4",
                        status = ReferralCodeStatuses.BANNED.value
                    ),
                    ReferralCode(
                        id = "QrisPfszkps_5",
                        status = ReferralCodeStatuses.LIMITED.value
                    ),
                    ReferralCode(
                        id = "QrisPfszkps_6",
                        status = ReferralCodeStatuses.AWAITING.value
                    )
                ),
                level = 0,
            ),
        )
    )
}