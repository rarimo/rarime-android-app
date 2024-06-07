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
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.StepIndicator
import com.distributedLab.rarime.ui.components.UiLinearProgressBar
import com.distributedLab.rarime.ui.theme.RarimeTheme

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

@Composable
fun RewardsLeveling() {
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
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
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
                                text = "Level 2",
                                style = RarimeTheme.typography.h5,
                            )
                            Text (
                                text = "Subtitle text here",
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
                            text = "110",
                            style = RarimeTheme.typography.subtitle3,
                            color = RarimeTheme.colors.textPrimary,
                        )
                        Text(
                            text = "/300",
                            color = RarimeTheme.colors.textSecondary,
                        )
                    }

                    UiLinearProgressBar(
                        percentage = 0.36f,
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
        RewardsLeveling()
    }
}