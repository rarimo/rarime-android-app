package com.distributedLab.rarime.modules.rewards.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.points.models.PointsBalanceBody
import com.distributedLab.rarime.api.points.models.PointsBalanceData
import com.distributedLab.rarime.api.points.models.PointsBalanceDataAttributes
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.StepIndicator
import com.distributedLab.rarime.ui.components.UiLinearProgressBar
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil

@Composable
private fun StageIndicator(
    modifier: Modifier = Modifier,
    number: Int,
    isActive: Boolean = false,
) {
    Box (
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .width(32.dp)
            .height(32.dp)
            .background(
                if (isActive) RarimeTheme.colors.primaryMain
                else Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        Text (
            text = number.toString(),
            style = RarimeTheme.typography.body3,
            color = if (isActive) RarimeTheme.colors.textPrimary else RarimeTheme.colors.textSecondary,
        )
    }
}

@Composable
private fun GradientDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier
            .width(42.dp)
            .height(4.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        RarimeTheme.colors.baseBlack.copy(alpha = 0.1f),
                    )
                )
            )
    )
}

@Composable
private fun RewardsItem () {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RarimeTheme.colors.componentPrimary)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box (
            Modifier
                .clip(RoundedCornerShape(1000.dp))
                .background(RarimeTheme.colors.componentPrimary)
                .width(40.dp)
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(id = R.drawable.ic_users, size = 20.dp)
        }

        Column (
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text (
                text = "X2 RMO Coins",
                style = RarimeTheme.typography.subtitle4,
            )
            Text (
                text = "Invite friends in to app",
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary,
            )
        }

    }
}

data class LevelReward(
    val title: String,
    val subtitle: String,
    val iconId: Int,
)

data class RewardLevel(
    val title: String,
    val subtitle: String,
    val logo: Int,

    val amount: Double,
    val minAmount: Double,
    val maxAmount: Double?,
    val rewards: List<LevelReward>,
)

val LEVELING: List<RewardLevel> = listOf(
    RewardLevel(
        title = "Level 1",
        subtitle = "0-30 RMO Coins",
        logo = R.drawable.reward_coin,

        amount = 0.0,
        minAmount = 0.0,
        maxAmount = 30.0,
        rewards = listOf(
            LevelReward(
                title = "X2 RMO Coins",
                subtitle = "Invite friends in to app",
                iconId = R.drawable.ic_users,
            )
        ),
    ),
    RewardLevel(
        title = "Level 2",
        subtitle = "3-50 RMO Coins",
        logo = R.drawable.reward_coin,

        amount = 0.0,
        minAmount = 30.0,
        maxAmount = 50.0,
        rewards = listOf(
            LevelReward(
                title = "X2 RMO Coins",
                subtitle = "Invite friends in to app",
                iconId = R.drawable.ic_users,
            )
        ),
    ),
    RewardLevel(
        title = "Level 3",
        subtitle = "50-xxx RMO Coins",
        logo = R.drawable.reward_coin,

        amount = 0.0,
        minAmount = 50.0,
        maxAmount = null,
        rewards = listOf(
            LevelReward(
                title = "X2 RMO Coins",
                subtitle = "Invite friends in to app",
                iconId = R.drawable.ic_users,
            )
        ),
    ),
)

val INFINITY_STUB = 999999999999999.0

@Composable
fun RewardsLeveling(pointsBalance: PointsBalanceBody) {
    val balance = pointsBalance.data.attributes.amount.toDouble()

    val leveling = LEVELING.map {
        // if user has pass current level
        if (it.maxAmount != null && balance >= it.maxAmount) {
            it.copy(
                amount = it.maxAmount
            )
        // if user is in current level
        } else if (balance > it.minAmount && (it.maxAmount == null || balance < it.maxAmount)) {
            it.copy(
                amount = pointsBalance.data.attributes.amount.toDouble()
            )
        } else { it }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(22.dp))
        // FIXME: overlapped a close btn
        Text (
            text = "Leaderboard",
            style = RarimeTheme.typography.subtitle4,
        )

        Spacer(modifier = Modifier.height(26.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StageIndicator(number = 1, isActive = false)

            GradientDivider()

            StageIndicator(number = 2, isActive = true)

            GradientDivider()

            StageIndicator(number = 3, isActive = false)
        }

        Spacer(modifier = Modifier.height(26.dp))

        Column (
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            leveling.forEach {
                CardContainer() {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column (
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text (
                                    text = it.title,
                                    style = RarimeTheme.typography.h5,
                                )
                                Text (
                                    text = it.subtitle,
                                    style = RarimeTheme.typography.body3,
                                    color = RarimeTheme.colors.textSecondary,
                                )
                            }

                            Image(
                                painter = painterResource(id = R.drawable.reward_coin),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(72.dp)
                            )
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text (
                                text = NumberUtil.formatAmount(it.amount),
                                style = RarimeTheme.typography.subtitle3,
                                color = RarimeTheme.colors.textPrimary,
                            )
                            Text(
                                text = " / " + (it.maxAmount?.let { maxAmount -> NumberUtil.formatAmount(maxAmount) } ?: "∞"),
                                color = RarimeTheme.colors.textSecondary,
                            )
                        }

                        UiLinearProgressBar(
                            percentage = NumberUtil.formatAmount((it.amount / (it.maxAmount ?: INFINITY_STUB) * 100) / 100.0).toFloat(),
                            trackColors = listOf(
                                RarimeTheme.colors.primaryMain,
                                RarimeTheme.colors.primaryDark,
                                RarimeTheme.colors.primaryDarker,
                            ),
                            backgroundModifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(100.dp)),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        StepIndicator(itemsCount = 3, selectedIndex = 1)

        Spacer(modifier = Modifier.height(24.dp))

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(RarimeTheme.colors.backgroundPure)
                .padding(vertical = 24.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "Rewards",
                style = RarimeTheme.typography.subtitle3,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column (
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RewardsItem()
                RewardsItem()
            }
        }

    }
}

@Preview
@Composable
fun RewardsLevelingPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.baseBlack)
    ) {
        RewardsLeveling(
            pointsBalance = PointsBalanceBody(
                data = PointsBalanceData(
                    id = "",
                    type = "",

                    attributes = PointsBalanceDataAttributes(
                        amount = 59,
                        is_disabled = false,
                        is_verified = true,
                        created_at = 0,
                        updated_at = 0,
                        rank = 0,
                        referral_codes = null,
                        level = 1,
                    )
                )
            )
        )
    }
}