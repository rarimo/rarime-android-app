package com.rarilabs.rarime.modules.rewards.components

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.api.points.models.PointsBalanceDataAttributes
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.StepIndicator
import com.rarilabs.rarime.ui.components.UiLinearProgressBar
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.NumberUtil
import kotlinx.coroutines.launch

@Composable
private fun StageIndicator(
    modifier: Modifier = Modifier,
    number: Int,
    isActive: Boolean = false,
) {
    Box(
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
        Text(
            text = number.toString(),
            style = RarimeTheme.typography.body3,
            color = if (isActive) RarimeTheme.colors.baseBlack else RarimeTheme.colors.textSecondary,
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

data class LevelReward(
    val title: String,
    val subtitle: String,
    val iconId: Int,
)

@Composable
private fun RewardsItem(levelReward: LevelReward) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RarimeTheme.colors.componentPrimary)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(1000.dp))
                .background(RarimeTheme.colors.componentPrimary)
                .width(40.dp)
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(id = levelReward.iconId, size = 20.dp, tint = RarimeTheme.colors.textPrimary)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = levelReward.title,
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary,
            )
            Text(
                text = levelReward.subtitle,
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary,
            )
        }

    }
}

data class RewardLevel(
    val isCurrentLevel: Boolean = false,

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
        subtitle = "Reserve tokens to unlock new levels and rewards",
        logo = R.drawable.reward_coin,

        amount = 0.0,
        minAmount = 0.0,
        maxAmount = 30.0,
        rewards = listOf(

        ),
    ),
    RewardLevel(
        title = "Level 2",
        subtitle = "Reserve tokens to unlock new levels and rewards",
        logo = R.drawable.reward_coin,

        amount = 0.0,
        minAmount = 30.0,
        maxAmount = 50.0,
        rewards = listOf(
            LevelReward(
                title = "10 extra referrals",
                subtitle = "Invite more people, earn more rewards",
                iconId = R.drawable.ic_users,
            ),
            LevelReward(
                title = "Exclusive campaigns",
                subtitle = "Only level 2 specials",
                iconId = R.drawable.ic_airdrop,
            )
        ),
    ),
    RewardLevel(
        title = "Level 3",
        subtitle = "Reserve tokens to unlock new levels and rewards",
        logo = R.drawable.reward_coin,

        amount = 0.0,
        minAmount = 50.0,
        maxAmount = null,
        rewards = listOf(
            LevelReward(
                title = "Staking",
                subtitle = "Earn more rewards",
                iconId = R.drawable.ic_rarimo,
            )
        ),
    ),
)

val INFINITY_STUB = 999999999999999.0

fun getNormalizeLeveling(balance: Double): List<RewardLevel> {
    return LEVELING.map {
        if (balance == 0.0) {
            it.copy(
                isCurrentLevel = true,
            )
            // if user has pass current level
        } else if (it.maxAmount != null && balance >= it.maxAmount) {
            it.copy(
                amount = it.maxAmount
            )
            // if user is in current level
        } else if (balance > it.minAmount && (it.maxAmount == null || balance < it.maxAmount)) {
            it.copy(
                amount = balance,
                isCurrentLevel = true,
            )
        } else {
            it
        }
    }
}

@Composable
fun LevelingProgress(
    level: RewardLevel,
    leadingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (leadingContent != null) {
            leadingContent()
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = NumberUtil.formatAmount(level.amount),
                style = RarimeTheme.typography.subtitle3,
                color = RarimeTheme.colors.textPrimary,
            )
            Text(
                text = " / " + (level.maxAmount?.let { maxAmount -> NumberUtil.formatAmount(maxAmount) }
                    ?: "∞"),
                color = RarimeTheme.colors.textSecondary,
            )
        }
    }

    val percentage = try {
        NumberUtil.formatAmount((level.amount / (level.maxAmount ?: INFINITY_STUB) * 100) / 100.0)
            .toFloat()
    } catch (e: Exception) {
        0f
    }

    UiLinearProgressBar(
        percentage = percentage,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RewardsLeveling(pointsBalance: PointsBalanceData) {
    val scope = rememberCoroutineScope()

    val balance = pointsBalance.attributes.amount.toDouble()

    val leveling = getNormalizeLeveling(balance)

    var selectedLevelingCardId by remember {
        mutableStateOf(leveling.indexOf(
            leveling.find { it.amount == balance }
        ))
    }

    val pagerState = rememberPagerState(
        initialPage = selectedLevelingCardId,
        pageCount = { leveling.size },
        initialPageOffsetFraction = 0f,
    )
    LaunchedEffect(pagerState.currentPage) {
        selectedLevelingCardId = pagerState.currentPage
    }

    fun handleUpdateStepIndicator(idx: Int) {
        scope.launch {
            selectedLevelingCardId = idx
            pagerState.animateScrollToPage(idx)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(22.dp))
        // FIXME: overlapped a close btn
        Text(
            text = "Leveling",
            style = RarimeTheme.typography.subtitle4,
            color = RarimeTheme.colors.textPrimary,
        )

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leveling.forEachIndexed { idx, it ->
                StageIndicator(number = idx + 1, isActive = selectedLevelingCardId == idx)

                if (idx < leveling.size - 1) {
                    GradientDivider()
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                pageSpacing = 12.dp
            ) { idx ->
                val level = leveling[idx]

                CardContainer {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = level.title,
                                    style = RarimeTheme.typography.h5,
                                    color = RarimeTheme.colors.textPrimary,
                                )
                                Text(
                                    text = level.subtitle,
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

                        LevelingProgress(level)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        StepIndicator(
            itemsCount = leveling.size,
            selectedIndex = selectedLevelingCardId,
            updateSelectedIndex = { handleUpdateStepIndicator(it) },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
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
                color = RarimeTheme.colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (leveling[selectedLevelingCardId].rewards.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    leveling[selectedLevelingCardId].rewards.forEach {
                        RewardsItem(it)
                    }
                }
            } else {
                Text(
                    text = "Start journey to unlock rewards",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
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
            pointsBalance = PointsBalanceData(
                id = "",
                type = "",

                attributes = PointsBalanceDataAttributes(
                    amount = 9,
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
    }
}