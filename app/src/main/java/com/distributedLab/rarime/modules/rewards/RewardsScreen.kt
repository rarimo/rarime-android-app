package com.distributedLab.rarime.modules.rewards

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.data.enums.PassportStatus
import com.distributedLab.rarime.modules.rewards.components.ActiveTasksList
import com.distributedLab.rarime.modules.rewards.components.rewards_leaderboard.RewardsLeaderBoard
import com.distributedLab.rarime.modules.rewards.components.RewardsLeveling
import com.distributedLab.rarime.modules.rewards.components.TimeEventsList
import com.distributedLab.rarime.modules.rewards.view_models.RewardsViewModel
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.base.BaseTooltip
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.UiLinearProgressBar
import com.distributedLab.rarime.ui.components.rememberAppSheetState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil
import com.distributedLab.rarime.util.Screen
import kotlinx.coroutines.launch

val localRewardsScreenViewModel =
    compositionLocalOf<RewardsViewModel> { error("No RewardsViewModel provided") }

@Composable
fun RewardsScreen(
    navigate: (String) -> Unit,
    rewardsViewModel: RewardsViewModel = hiltViewModel()
) {
    CompositionLocalProvider(localRewardsScreenViewModel provides rewardsViewModel) {
        RewardsScreenContent(navigate)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreenContent(
    navigate: (String) -> Unit,
) {
    val rewardsViewModel = localRewardsScreenViewModel.current

    val passportStatus = rewardsViewModel.passportStatus.collectAsState()

    val levelProgress = rewardsViewModel.levelProgress

    val pointsWalletAsset = rewardsViewModel.pointsWalletAsset.collectAsState()

    val leaderboardSheetState = rememberAppSheetState()

    val levelingSheetState = rememberAppSheetState()

    val limitedTimeEvents = rewardsViewModel.limitedTimeEvents.collectAsState()

    val activeTasksEvents = rewardsViewModel.activeTasksEvents.collectAsState()

    val leaderBoardList = rewardsViewModel.leaderBoardList.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            rewardsViewModel.init()
        }
    }

    pointsWalletAsset.value?.let { walletAsset ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.rewards_screen_title),
                    style = RarimeTheme.typography.subtitle3,
                    color = RarimeTheme.colors.textPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(RarimeTheme.colors.warningLighter)
                        .padding(vertical = 4.dp, horizontal = 9.dp)
                        .clickable { leaderboardSheetState.show() }
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                pointsWalletAsset.value?.let {
                                    BaseTooltip(
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
                                            text = walletAsset.token.name,
                                            color = RarimeTheme.colors.textSecondary,
                                            style = RarimeTheme.typography.body3,
                                        )
                                    }
                                    Text(
                                        text = NumberUtil.formatBalance(walletAsset.humanBalance()),
                                        color = RarimeTheme.colors.textPrimary,
                                        style = RarimeTheme.typography.h4,
                                    )
                                }
                            }

                            PrimaryButton(
                                text = stringResource(R.string.rewards_claim_btn),
                                leftIcon = R.drawable.ic_swap,
                                onClick = { navigate(Screen.Main.Rewards.RewardsClaim.route) },
                                enabled = passportStatus.value == PassportStatus.ALLOWED &&
                                        walletAsset.balance.value.toDouble() > 0.0,
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(0.dp)
                                        .clickable { levelingSheetState.show() },
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
                                percentage = levelProgress,
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

                if (passportStatus.value == PassportStatus.ALLOWED) {
                    CardContainer() {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
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

                            limitedTimeEvents.value?.let {
                                TimeEventsList(
                                    modifier = Modifier.fillMaxWidth(),
                                    navigate = navigate,
                                    pointsEvents = it
                                )
                            } ?: Column() {
                                // TODO: implement skeleton loader and error view
                                Text(
                                    text = "Loading...",
                                    style = RarimeTheme.typography.body3,
                                    color = RarimeTheme.colors.textSecondary,
                                )
                            }
                        }
                    }
                }

                CardContainer() {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Active Tasks",
                                style = RarimeTheme.typography.subtitle3
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        activeTasksEvents.value?.let {
                            ActiveTasksList(
                                navigate = navigate,
                                pointsEvents = it
                            )
                        } ?: Column() {
                            // TODO: implement skeleton loader and error view
                            Text(
                                text = "Loading...",
                                style = RarimeTheme.typography.body3,
                                color = RarimeTheme.colors.textSecondary,
                            )
                        }
                    }
                }
            }
        }

        AppBottomSheet(state = leaderboardSheetState, fullScreen = true) { hide ->
            RewardsLeaderBoard(
                leaderBoardList.value,
                walletAsset.userAddress,
            )
        }
    }

    AppBottomSheet(state = levelingSheetState, fullScreen = true) { hide ->
        RewardsLeveling()
    }
}

@Preview
@Composable
private fun RewardsScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
    ) {
        RewardsScreen(navigate = {})
    }
}
