package com.distributedLab.rarime.modules.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.RichTooltipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.rewards.components.RewardAmountPreview
import com.distributedLab.rarime.modules.rewards.components.RewardsLeaderBoard
import com.distributedLab.rarime.modules.rewards.view_models.RewardsViewModel
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.AppSkeleton
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.base.BaseTooltip
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.UiLinearProgressBar
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil
import com.distributedLab.rarime.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    navigate: (String) -> Unit,
    rewardsViewModel: RewardsViewModel = hiltViewModel()
) {
    val pointsWalletAsset = rewardsViewModel.pointsWalletAsset

    val sheetState = rememberAppSheetState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.rewards_screen_title),
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
                    .clickable { sheetState.show() }
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

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
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
                                    text = pointsWalletAsset.token.name,
                                    color = RarimeTheme.colors.textSecondary,
                                    style = RarimeTheme.typography.body3,
                                )
                            }
                            Text(
                                text = NumberUtil.formatAmount(pointsWalletAsset.humanBalance()),
                                color = RarimeTheme.colors.textPrimary,
                                style = RarimeTheme.typography.h4,
                            )
                        }

                        PrimaryButton(
                            text = stringResource(R.string.rewards_claim_btn),
                            leftIcon = R.drawable.ic_swap,
                            onClick = { navigate(Screen.Main.Rewards.RewardsClaim.route) }
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

            CardContainer() {
                Column {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box (
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(RarimeTheme.colors.warningLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "\uD83D\uDD25"
                            )
                        }
                        Text(
                            text = "Limited time events",
                            style = RarimeTheme.typography.subtitle3
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TimeEventsList(
                        modifier = Modifier.fillMaxWidth(),
                        navigate,
                    )
                }
            }

            CardContainer() {
                Column {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Active Tasks",
                            style = RarimeTheme.typography.subtitle3
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    ActiveTasksList(navigate)
                }
            }
        }
    }


    AppBottomSheet(state = sheetState, fullScreen = true) { hide ->
        RewardsLeaderBoard()
    }
}

@Composable
fun TimeEventsList(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
) {
    val limitedTimeEvents = listOf(
        "Limited time event 1",
        "Limited time event 2",
    )

    Column (
        modifier = modifier
    ) {
        limitedTimeEvents.forEachIndexed { idx, item ->
            TimeEventItem(navigate = navigate)

            if (idx != limitedTimeEvents.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun TimeEventItem(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
) {
    Row (
        modifier = modifier.clickable { navigate(Screen.Main.Rewards.RewardsEventsItem.route) },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
//            painter = rememberAsyncImagePainter("https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
            painter = painterResource(id = R.drawable.event_stub),
            contentDescription = "Limited time event",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(64.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(8.dp)),
        )

        Column (
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Limited time event Limited time event Limited time event",
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary,
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RewardAmountPreview()

                Text(
                    text = "2 days left",
                    style = RarimeTheme.typography.caption2,
                    color = RarimeTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
fun ActiveTasksList(
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTasksList = listOf(
        "Limited time event 1",
        "Limited time event 2",
    )

    Column (
        modifier = modifier
    ) {
        activeTasksList.forEachIndexed { idx, item ->
            ActiveTaskItem(navigate = navigate)

            if (idx != activeTasksList.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ActiveTaskItem(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { navigate(Screen.Main.Rewards.RewardsEventsItem.route) }
    ) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .width(40.dp)
                .height(40.dp)
                .background(RarimeTheme.colors.baseBlack)
        ) {
            AppIcon(
                id = R.drawable.ic_users,
                tint = RarimeTheme.colors.baseWhite
            )
        }

        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Invite 5 users",
                    style = RarimeTheme.typography.subtitle4,
                    color = RarimeTheme.colors.textPrimary,
                )

                Text (
                    text = "Invite frients into app",
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textSecondary,
                )
            }

            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RewardAmountPreview()

                AppIcon(
                    id = R.drawable.ic_caret_right,
                    tint = RarimeTheme.colors.textSecondary
                )
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
        RewardsScreen(navigate = {})
    }
}
