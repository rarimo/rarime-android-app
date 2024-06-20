package com.distributedLab.rarime.modules.rewards

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.distributedLab.rarime.api.points.models.PointsEventData
import com.distributedLab.rarime.data.enums.PassportStatus
import com.distributedLab.rarime.data.tokens.PointsToken
import com.distributedLab.rarime.data.tokens.PreviewerToken
import com.distributedLab.rarime.manager.WalletAsset
import com.distributedLab.rarime.modules.home.components.passport.StatusCard
import com.distributedLab.rarime.modules.rewards.components.ActiveTasksList
import com.distributedLab.rarime.modules.rewards.components.ActiveTasksListSkeleton
import com.distributedLab.rarime.modules.rewards.components.RewardsLeveling
import com.distributedLab.rarime.modules.rewards.components.TimeEventsList
import com.distributedLab.rarime.modules.rewards.components.TimeEventsListSkeleton
import com.distributedLab.rarime.modules.rewards.components.rewards_leaderboard.RewardsLeaderBoard
import com.distributedLab.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.distributedLab.rarime.modules.rewards.view_models.LeaderBoardItem
import com.distributedLab.rarime.modules.rewards.view_models.RewardsViewModel
import com.distributedLab.rarime.ui.base.BaseTooltip
import com.distributedLab.rarime.ui.components.AppBottomSheet
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.InfoAlert
import com.distributedLab.rarime.ui.components.PrimaryButton
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
    navigate: (String) -> Unit, rewardsViewModel: RewardsViewModel = hiltViewModel()
) {
    val isAuthorized = rewardsViewModel.isAuthorized.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                rewardsViewModel.init()
            } catch (e: Exception) {
                Log.e("RewardsScreenContent", "init: ${e.message}")
            }
        }
    }

    CompositionLocalProvider(localRewardsScreenViewModel provides rewardsViewModel) {

        if (rewardsViewModel.passportStatus.value == PassportStatus.NOT_ALLOWED) {
            rewardsViewModel.getIssuerAuthority()?.let {
                UnSupportedPassport(
                    issuerAuthority = it
                )
            }
        } else if (isAuthorized.value) {
            RewardsScreenContent(navigate)
        } else {
            RewardsUnauthorized()
        }
    }
}

@Composable
fun RewardsUnauthorized() {
    val rewardsViewModel = localRewardsScreenViewModel.current

    val coroutineScope = rememberCoroutineScope()

    suspend fun login() {
        try {
            rewardsViewModel.login()
        } catch (e: Exception) {
            Log.e("HomeViewModel", "login: ${e.message}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO: implement properly
        PrimaryButton(text = "Login", onClick = { coroutineScope.launch { login() } })
    }
}

@Composable
fun RewardsScreenContent(
    navigate: (String) -> Unit,
) {
    val rewardsViewModel = localRewardsScreenViewModel.current

    val passportStatus = rewardsViewModel.passportStatus.collectAsState()

    val levelProgress = rewardsViewModel.levelProgress

    val pointsWalletAsset = rewardsViewModel.pointsWalletAsset.collectAsState()

    val limitedTimeEvents = rewardsViewModel.limitedTimeEvents.collectAsState()

    val activeTasksEvents = rewardsViewModel.activeTasksEvents.collectAsState()

    val leaderBoardList = rewardsViewModel.leaderBoardList.collectAsState()



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

                if (leaderBoardList.value.isNotEmpty()) {
                    RewardsRatingBadge(
                        leaderBoardList = leaderBoardList.value,
                        walletAsset = walletAsset,
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RewardsScreenUserStatistic(
                    navigate = navigate,
                    pointsWalletAsset = walletAsset,
                    passportStatus = passportStatus.value,
                    levelProgress = levelProgress,
                )

                limitedTimeEvents.value?.let {
                    if (passportStatus.value == PassportStatus.ALLOWED && it.isNotEmpty()) {
                        CardContainer {
                            LimitedEventsList(navigate = navigate, limitedTimeEvents = it)
                        }
                    }
                }

                CardContainer {
                    ActiveTasksList(
                        navigate = navigate, activeTasksEvents = activeTasksEvents.value
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreenUserStatistic(
    navigate: (String) -> Unit,
    pointsWalletAsset: WalletAsset,
    passportStatus: PassportStatus,
    levelProgress: Float,
) {
    val levelingSheetState = rememberAppSheetState()

    val pointsBalanceData = pointsWalletAsset.token as PointsToken

    Column(
        verticalArrangement = Arrangement.spacedBy((-43).dp)
    ) {
        StatusCard(passportStatus = passportStatus)
        CardContainer {
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
                                text = pointsWalletAsset.token.name,
                                color = RarimeTheme.colors.textSecondary,
                                style = RarimeTheme.typography.body3,
                            )
                        }
                        Text(
                            text = NumberUtil.formatBalance(pointsWalletAsset.humanBalance()),
                            color = RarimeTheme.colors.textPrimary,
                            style = RarimeTheme.typography.h4,
                        )
                    }

                    PrimaryButton(
                        text = stringResource(R.string.rewards_claim_btn),
                        leftIcon = R.drawable.ic_swap,
                        onClick = { navigate(Screen.Main.Rewards.RewardsClaim.route) },
                        // TODO: implement in next releases
//                        enabled = passportStatus == PassportStatus.ALLOWED && pointsWalletAsset.balance.value.toDouble() > 0.0,
                        enabled = false,
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

                if (passportStatus == PassportStatus.UNSCANNED) {
                    InfoAlert(
                        text = stringResource(
                            id = R.string.rewards_screen_statistics_unscanned,
                            pointsWalletAsset.token.symbol
                        )
                    )
                }
            }
        }
    }

    AppBottomSheet(state = levelingSheetState, fullScreen = true) { hide ->
        pointsBalanceData.balanceDetails?.let { RewardsLeveling(it) }
    }
}

@Composable
fun LimitedEventsList(
    navigate: (String) -> Unit, limitedTimeEvents: List<PointsEventData>?
) {
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
                text = "Limited time events", style = RarimeTheme.typography.subtitle3
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        limitedTimeEvents?.let {
            if (it.isEmpty()) {
                Text(
                    text = "No events",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
            } else {
                TimeEventsList(
                    modifier = Modifier.fillMaxWidth(), navigate = navigate, pointsEventData = it
                )
            }
        } ?: TimeEventsListSkeleton()
    }
}

@Composable
fun ActiveTasksList(
    navigate: (String) -> Unit, activeTasksEvents: List<PointsEventData>?
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Active Tasks", style = RarimeTheme.typography.subtitle3
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        activeTasksEvents?.let {
            if (it.isEmpty()) {
                Text(
                    text = "No tasks",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
            } else {
                ActiveTasksList(
                    navigate = navigate, pointsEventData = it
                )
            }
        } ?: ActiveTasksListSkeleton()
    }
}

@Composable
fun RewardsRatingBadge(
    leaderBoardList: List<LeaderBoardItem>, walletAsset: WalletAsset
) {
    val leaderboardSheetState = rememberAppSheetState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(RarimeTheme.colors.warningLighter)
            .padding(vertical = 4.dp, horizontal = 9.dp)
            .clickable { leaderboardSheetState.show() }) {
        AppIcon(
            id = R.drawable.ic_trophy,
            tint = RarimeTheme.colors.warningDarker,
        )

        Text(
            text = leaderBoardList.size.toString(),
            color = RarimeTheme.colors.warningDarker,
        )
    }

    AppBottomSheet(state = leaderboardSheetState, fullScreen = true) {
        RewardsLeaderBoard(
            leaderBoardList,
            walletAsset.userAddress,
        )
    }
}

@Composable
fun RewardsRatingBadgeSkeleton() {
    Row(
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
            text = "---",
            color = RarimeTheme.colors.warningDarker,
        )
    }

}

@Preview(showBackground = true)
@Composable
fun RewardsRatingBadgePreview() {
    RewardsRatingBadge(
        leaderBoardList = listOf(), walletAsset = WalletAsset("", PreviewerToken(""))
    )
}

@Preview(showBackground = true)
@Composable
private fun RewardsScreenUserStatisticPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RewardsScreenUserStatistic(
            navigate = {},
            pointsWalletAsset = WalletAsset("", PreviewerToken("", "Reserved", "RRMO")),
            passportStatus = PassportStatus.NOT_ALLOWED,
            levelProgress = 0f
        )
        RewardsScreenUserStatistic(
            navigate = {},
            pointsWalletAsset = WalletAsset("", PreviewerToken("", "Reserved", "RRMO")),
            passportStatus = PassportStatus.WAITLIST,
            levelProgress = 0f
        )
        RewardsScreenUserStatistic(
            navigate = {},
            pointsWalletAsset = WalletAsset("", PreviewerToken("", "Reserved", "RRMO")),
            passportStatus = PassportStatus.ALLOWED,
            levelProgress = 0.75f
        )
        RewardsScreenUserStatistic(
            navigate = {},
            pointsWalletAsset = WalletAsset("", PreviewerToken("", "Reserved", "RRMO")),
            passportStatus = PassportStatus.UNSCANNED,
            levelProgress = 0f
        )
    }
}

@Preview
@Composable
private fun RewardsEventsListsPreview() {
    Column(
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            LimitedEventsList(
                navigate = {}, limitedTimeEvents = CONST_MOCKED_EVENTS_LIST.subList(0, 2)
            )
            LimitedEventsList(
                navigate = {}, limitedTimeEvents = listOf()
            )
            LimitedEventsList(
                navigate = {}, limitedTimeEvents = null
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ActiveTasksList(
                navigate = {}, activeTasksEvents = CONST_MOCKED_EVENTS_LIST.subList(0, 2)
            )
            ActiveTasksList(
                navigate = {}, activeTasksEvents = listOf()
            )
            ActiveTasksList(
                navigate = {}, activeTasksEvents = null
            )
        }
    }
}