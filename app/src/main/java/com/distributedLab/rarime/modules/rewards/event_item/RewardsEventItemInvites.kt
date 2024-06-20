package com.distributedLab.rarime.modules.rewards.event_item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.api.points.models.PointsBalanceData
import com.distributedLab.rarime.api.points.models.PointsBalanceDataAttributes
import com.distributedLab.rarime.api.points.models.PointsEventData
import com.distributedLab.rarime.api.points.models.ReferralCode
import com.distributedLab.rarime.api.points.models.ReferralCodeStatuses
import com.distributedLab.rarime.modules.rewards.components.RewardsEventItemInvitesCard
import com.distributedLab.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.distributedLab.rarime.modules.wallet.WalletRouteLayout
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.theme.RarimeTheme

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
            ) {
                Text(
                    text = "Invite Friends",
                    style = RarimeTheme.typography.h4,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Short description text here",
                    style = RarimeTheme.typography.body2,
                    color = RarimeTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(100.dp))

            CardContainer(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Invited 4/5",
                        style = RarimeTheme.typography.subtitle3
                    )
                    Text(
                        text = "Short description text here",
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