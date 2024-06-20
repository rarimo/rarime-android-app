package com.rarilabs.rarime.modules.rewards.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.api.points.models.PointsBalanceDataAttributes
import com.rarilabs.rarime.api.points.models.ReferralCode
import com.rarilabs.rarime.api.points.models.ReferralCodeStatuses
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

enum class RewardsEventItemInvitesCardContainerVariants(val value: String) {
    FILLED("filled"),
    OUTLINED("outlined"),
}

fun shareContent(context: Context, text: String) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, null))
}

@Composable
private fun RewardsEventItemInvitesCardContainer(
    modifier: Modifier = Modifier,
    isCodeDisabled: Boolean = false,
    title: String,
    description: String,
    variant: RewardsEventItemInvitesCardContainerVariants = RewardsEventItemInvitesCardContainerVariants.FILLED,
    columnContent: @Composable () -> Unit = {},
    actionContent: @Composable () -> Unit = {},
) {
    val boxBackground = when (variant) {
        RewardsEventItemInvitesCardContainerVariants.FILLED -> RarimeTheme.colors.componentPrimary
        RewardsEventItemInvitesCardContainerVariants.OUTLINED -> Color.Transparent
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(boxBackground)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .absolutePadding(right = 16.dp)
                    .alpha(if (isCodeDisabled) 0.5f else 1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    textDecoration = if (isCodeDisabled) TextDecoration.LineThrough else null,
                    text = title,
                    style = RarimeTheme.typography.subtitle4,
                )
                Text(
                    text = description,
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                )
                columnContent()
            }

            actionContent()
        }
    }
}

@Composable
fun RewardsEventItemInvitesCard(
    code: ReferralCode,
    rewardAmount: Long,
    pointsBalance: PointsBalanceData,
) {
    val context = LocalContext.current
    
    val notActiveActionContent = @Composable {
        if (pointsBalance.attributes.is_verified == true) {
            RewardAmountPreview(
                amount = rewardAmount,
            )
        }
    }

    Column {
        when (code.status) {
            ReferralCodeStatuses.REWARDED.value -> {
                RewardsEventItemInvitesCardContainer(
                    title = code.id,
                    isCodeDisabled = true,
                    description = "Rewarded",
                    actionContent = {
                        notActiveActionContent()
                    }
                )
            }

            ReferralCodeStatuses.CONSUMED.value -> {
                RewardsEventItemInvitesCardContainer(
                    title = code.id,
                    isCodeDisabled = true,
                    description = "used • friend need to scan passport",
                    actionContent = {
                        notActiveActionContent()
                    }
                )
            }

            ReferralCodeStatuses.ACTIVE.value -> {
                val invitationUrl = "${BaseConfig.INVITATION_BASE_URL}/${code.id}"

                RewardsEventItemInvitesCardContainer(
                    title = code.id,
                    description = invitationUrl,
                    actionContent = {
                        IconButton(
                            onClick = {
                                shareContent(context, invitationUrl)
                            }
                        ) {
                            AppIcon(id = R.drawable.ic_share)
                        }
                    },
                    columnContent = {
                        Text(
                            text = "active",
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.successDark,
                        )
                    }
                )
            }

            ReferralCodeStatuses.BANNED.value -> {
                RewardsEventItemInvitesCardContainer(
                    title = code.id,
                    isCodeDisabled = true,
                    description = "Your friends country (known after scanning passport) is not allowed to participate in the referral program",
                    actionContent = {
                        notActiveActionContent()
                    }
                )
            }

            ReferralCodeStatuses.LIMITED.value -> {
                RewardsEventItemInvitesCardContainer(
                    title = code.id,
                    isCodeDisabled = true,
                    description = "The limit of reserved tokens in your friends country is reached",
                    actionContent = {
                        notActiveActionContent()
                    }
                )
            }

            ReferralCodeStatuses.AWAITING.value -> {
                RewardsEventItemInvitesCardContainer(
                    title = code.id,
                    isCodeDisabled = true,
                    description = "Friend has scanned passport • need your passport scan",
                    actionContent = {
                        notActiveActionContent()
                    }
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardsEventItemInvitesCardPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        listOf(
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
        ).forEach {
            RewardsEventItemInvitesCard(
                code = it,
                rewardAmount = 100,
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
                        referral_codes = listOf(),
                        level = 1,
                    )
                )
            )
        }
    }
}
