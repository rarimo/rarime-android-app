package com.distributedLab.rarime.modules.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.RichTooltipColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSkeleton
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.base.BaseTooltip
import com.distributedLab.rarime.ui.components.UiLinearProgressBar
import com.distributedLab.rarime.ui.theme.RarimeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.transactions_title),
                style = RarimeTheme.typography.subtitle3,
                color = RarimeTheme.colors.textPrimary
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(RarimeTheme.colors.warningLighter)
                    .padding(vertical = 4.dp, horizontal = 9.dp)
            ) {
                AppIcon(
                    id = R.drawable.ic_trophy,
                    tint = RarimeTheme.colors.warningDarker,
                )

                Text(
                    text = "241",
                    color = RarimeTheme.colors.warningDarker,
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        CardContainer() {
            Column (
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        BaseTooltip (
                            tooltipContent = {
                                RichTooltip(
                                    text = {
                                        Text(
                                            text = stringResource(id = R.string.rewards_amount_overline_tooltip),
                                            style = RarimeTheme.typography.body3,
                                            color = RarimeTheme.colors.textSecondary,
                                        )
                                    },
                                    colors = RichTooltipColors(
                                        containerColor = RarimeTheme.colors.baseWhite,
                                        contentColor = RarimeTheme.colors.textPrimary,
                                        titleContentColor = RarimeTheme.colors.textPrimary,
                                        actionContentColor = RarimeTheme.colors.textPrimary,
                                    ),
                                )
                            },
                            iconColor = RarimeTheme.colors.textSecondary,
                        ) {
                            Text(
                                text = "Reserved RMO",
                                color = RarimeTheme.colors.textSecondary,
                                style = RarimeTheme.typography.body3,
                            )
                        }
                        Text(
                            text = "210",
                            color = RarimeTheme.colors.textPrimary,
                            style = RarimeTheme.typography.h4,
                        )
                    }

                    PrimaryButton(
                        text = stringResource(R.string.rewards_claim_btn),
                        leftIcon = R.drawable.ic_swap,
                        onClick = {},
                    )
                }

                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row (
                            modifier = Modifier
                                .padding(0.dp)
                                .clickable { /* TODO */ },
                        ) {
                            Text(
                                text = "Level 2",
                                style = RarimeTheme.typography.subtitle5,
                                color = RarimeTheme.colors.textPrimary,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AppIcon(
                                id = R.drawable.ic_caret_right,
                                size = 16.dp,
                            )
                        }

                        Text(
                            text = "110/300",
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
    }
}

@Composable
private fun RewardsSkeleton() {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(top = 20.dp)
            .padding(horizontal = 12.dp)
            .blur(6.dp)
    ) {
        AppSkeleton(
            modifier = Modifier
                .width(120.dp)
                .height(20.dp)
        )
        for (i in 0..4) {
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    AppSkeleton(
                        modifier = Modifier
                            .width(60.dp)
                            .height(12.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppSkeleton(
                            modifier = Modifier
                                .width(140.dp)
                                .height(30.dp)
                        )
                        AppSkeleton(
                            modifier = Modifier
                                .width(60.dp)
                                .height(20.dp)
                        )
                    }
                    AppSkeleton(
                        modifier = Modifier
                            .width(200.dp)
                            .height(12.dp)
                    )
                    AppSkeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RewardsScreenPreview() {
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        RewardsScreen()
    }
}
