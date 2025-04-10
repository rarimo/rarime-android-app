package com.rarilabs.rarime.modules.rewards

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.api.points.models.PointsBalanceDataAttributes
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.data.enums.PassportStatus
import com.rarilabs.rarime.data.tokens.PointsToken
import com.rarilabs.rarime.data.tokens.PreviewerToken
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.modules.rewards.components.ActiveTasksList
import com.rarilabs.rarime.modules.rewards.components.ActiveTasksListSkeleton
import com.rarilabs.rarime.modules.rewards.components.LevelingProgress
import com.rarilabs.rarime.modules.rewards.components.RewardsLeveling
import com.rarilabs.rarime.modules.rewards.components.TimeEventsList
import com.rarilabs.rarime.modules.rewards.components.TimeEventsListSkeleton
import com.rarilabs.rarime.modules.rewards.components.getNormalizeLeveling
import com.rarilabs.rarime.modules.rewards.components.rewards_leaderboard.RewardsLeaderBoard
import com.rarilabs.rarime.modules.rewards.view_models.CONST_MOCKED_EVENTS_LIST
import com.rarilabs.rarime.modules.rewards.view_models.LeaderBoardItem
import com.rarilabs.rarime.modules.rewards.view_models.MOCKED_LEADER_BOARD_LIST
import com.rarilabs.rarime.modules.rewards.view_models.RewardsViewModel
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppCircularProgressIndicator
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppSkeleton
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.ErrorView
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.NumberUtil
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.launch

val localRewardsScreenViewModel =
    compositionLocalOf<RewardsViewModel> { error("No RewardsViewModel provided") }

@Composable
fun RewardsScreen(
    navigate: (String) -> Unit, rewardsViewModel: RewardsViewModel = hiltViewModel()
) {
    val isAuthorized = remember { rewardsViewModel.isAuthorized }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var isError by remember {
        mutableStateOf(false)
    }

    val passportStatus by rewardsViewModel.passportStatus.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                rewardsViewModel.init()
                isLoading = false
            } catch (e: Exception) {
                ErrorHandler.logError("RewardsScreenContent", "init: ${e.message}", e)
                isError = true
            }
        }
    }

    CompositionLocalProvider(localRewardsScreenViewModel provides rewardsViewModel) {

        if (passportStatus == PassportStatus.NOT_ALLOWED || passportStatus == PassportStatus.WAITLIST_NOT_ALLOWED) {
            rewardsViewModel.getNationality()?.let {
                UnSupportedPassport(
                    nationality = it
                )
            }
        } else if (isError) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ErrorView()
            }
        } else if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppCircularProgressIndicator()
            }
        }
//        else if (isAuthorized) {
//            RewardsScreenContent(navigate)
//        }
    }
}

@Composable
fun RewardsScreenContent(
    navigate: (String) -> Unit,
) {
    val rewardsViewModel = localRewardsScreenViewModel.current

    val passportStatus by rewardsViewModel.passportStatus.collectAsState()

    val pointsWalletAsset by rewardsViewModel.pointsWalletAsset.collectAsState()

    val limitedTimeEvents by rewardsViewModel.limitedTimeEvents.collectAsState()

    val activeTasksEvents by rewardsViewModel.activeTasksEvents.collectAsState()

    val leaderBoardList by rewardsViewModel.leaderBoardList.collectAsState()

    val pointsToken by rewardsViewModel.pointsToken.collectAsState()

    val userLeaderBoardItem by rewardsViewModel.userLeaderBoardItem.collectAsState()

    pointsWalletAsset?.let { walletAsset ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(R.string.rewards_screen_title),
                    style = RarimeTheme.typography.subtitle2,
                    color = RarimeTheme.colors.textPrimary
                )

                RewardsRatingBadge(
                    leaderBoardList = leaderBoardList,
                    walletAsset = walletAsset,
                    userLeaderBoardItem = userLeaderBoardItem,
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                pointsToken?.balanceDetails?.let {
                    RewardsScreenUserStatistic(
                        navigate = navigate,
                        pointsWalletAsset = walletAsset,
                        passportStatus = passportStatus,
                        pointsBalanceData = it,
                    )
                }

                limitedTimeEvents?.let {
                    if (passportStatus == PassportStatus.ALLOWED && it.isNotEmpty()) {
                        CardContainer {
                            LimitedEventsList(navigate = navigate, limitedTimeEvents = it)
                        }
                    }
                }

                CardContainer {
                    ActiveTasksList(
                        navigate = navigate,
                        activeTasksEvents = activeTasksEvents,
                        passportStatus = passportStatus,
                        refreshEvents = {
                            rewardsViewModel.refreshEvents()
                        },
                        claimEvent = { it ->
                            rewardsViewModel.claimRewardsEvent(it)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun RewardsScreenUserStatistic(
    navigate: (String) -> Unit,
    pointsWalletAsset: WalletAsset,
    passportStatus: PassportStatus,
    pointsBalanceData: PointsBalanceData,
) {
    val levelingSheetState = rememberAppSheetState()

    Column(
        verticalArrangement = Arrangement.spacedBy((-43).dp)
    ) {
//        StatusCard(passportStatus = passportStatus)
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
//                        BaseTooltip(
//                            tooltipContent = {
//                                RichTooltip(
//                                    text = {
//                                        Text(
//                                            text = stringResource(id = R.string.rewards_amount_overline_tooltip),
//                                            style = RarimeTheme.typography.body3,
//                                            color = RarimeTheme.colors.textSecondary,
//                                        )
//                                    },
//                                    colors = RichTooltipColors(
//                                        containerColor = RarimeTheme.colors.baseWhite,
//                                        contentColor = RarimeTheme.colors.textPrimary,
//                                        titleContentColor = RarimeTheme.colors.textPrimary,
//                                        actionContentColor = RarimeTheme.colors.textPrimary,
//                                    ),
//                                )
//                            },
//                            iconColor = RarimeTheme.colors.textSecondary,
//                        ) {
//                            Text(
//                                text = pointsWalletAsset.token.name,
//                                color = RarimeTheme.colors.textSecondary,
//                                style = RarimeTheme.typography.body3,
//                            )
//                        }
                        Text(
                            text = pointsWalletAsset.token.name,
                            color = RarimeTheme.colors.textSecondary,
                            style = RarimeTheme.typography.body3,
                        )
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
                    val level =
                        getNormalizeLeveling(pointsWalletAsset.balance.value.toDouble()).find {
                            it.isCurrentLevel
                        }

                    level?.let {
                        LevelingProgress(
                            level = it,
                            leadingContent = {
                                Row(
                                    modifier = Modifier
                                        .padding(0.dp)
                                        .clickable { levelingSheetState.show() },
                                ) {
                                    Text(
                                        text = level.title,
                                        style = RarimeTheme.typography.subtitle5,
                                        color = RarimeTheme.colors.textPrimary,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AppIcon(
                                        id = R.drawable.ic_caret_right,
                                        size = 16.dp,
                                        tint = RarimeTheme.colors.textPrimary
                                    )
                                }
                            }
                        )
                    }
                }

//                if (passportStatus == PassportStatus.UNSCANNED) {
//                    InfoAlert(
//                        text = stringResource(
//                            id = R.string.rewards_screen_statistics_unscanned,
//                            pointsWalletAsset.token.symbol
//                        )
//                    )
//                }
            }
        }
    }

    AppBottomSheet(state = levelingSheetState, fullScreen = true) { hide ->
        RewardsLeveling(pointsBalanceData)
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
                    text = "\uD83D\uDD25",
                    color = RarimeTheme.colors.textPrimary
                )
            }
            Text(
                text = "Limited time events",
                style = RarimeTheme.typography.subtitle3,
                color = RarimeTheme.colors.textPrimary
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
    navigate: (String) -> Unit,
    activeTasksEvents: List<PointsEventData>?,
    passportStatus: PassportStatus,
    refreshEvents: suspend () -> Unit,
    claimEvent: suspend (String) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Active Tasks",
                style = RarimeTheme.typography.subtitle3,
                color = RarimeTheme.colors.textPrimary
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
                    navigate = navigate,
                    pointsEventData = it,
                    passportStatus = passportStatus,
                    refreshEvents = refreshEvents,
                    claimEvent = claimEvent
                )
            }
        } ?: ActiveTasksListSkeleton()
    }
}

@Composable
fun RewardsRatingBadge(
    leaderBoardList: List<LeaderBoardItem>,
    walletAsset: WalletAsset,
    userLeaderBoardItem: LeaderBoardItem
) {
    val leaderboardSheetState = rememberAppSheetState()

    val pointsToken = walletAsset.token as PointsToken

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

        pointsToken.balanceDetails?.attributes?.rank?.let {
            Text(
                text = it.toString(),
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.warningDarker,
            )
        } ?: AppSkeleton(
            modifier = Modifier
                .width(18.dp)
                .height(18.dp)
        )
    }

    AppBottomSheet(state = leaderboardSheetState, fullScreen = true) {
        RewardsLeaderBoard(
            leaderBoardList,
            userLeaderBoardItem,
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
        leaderBoardList = listOf(),
        walletAsset = WalletAsset("", PreviewerToken("")),
        MOCKED_LEADER_BOARD_LIST[0]
    )
}

@Preview(showBackground = true)
@Composable
private fun RewardsScreenUserStatisticPreview() {
    val mockedPointsBalanceData = PointsBalanceData(
        id = "",
        type = "",
        attributes = PointsBalanceDataAttributes(
            amount = 10,
            is_disabled = false,
            is_verified = true,
            created_at = 0,
            updated_at = 0,
            rank = 0,
            referral_codes = listOf(),
            level = 1,
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RewardsScreenUserStatistic(
            navigate = {},
            pointsWalletAsset = WalletAsset("", PreviewerToken("", "Reserved RMO", "RRMO")),
            passportStatus = PassportStatus.NOT_ALLOWED,
            pointsBalanceData = mockedPointsBalanceData,
        )
        RewardsScreenUserStatistic(
            navigate = {},
            pointsWalletAsset = WalletAsset("", PreviewerToken("", "Reserved RMO", "RRMO")),
            passportStatus = PassportStatus.WAITLIST,
            pointsBalanceData = mockedPointsBalanceData,
        )
        RewardsScreenUserStatistic(
            navigate = {},
            pointsWalletAsset = WalletAsset("", PreviewerToken("", "Reserved RMO", "RRMO")),
            passportStatus = PassportStatus.ALLOWED,
            pointsBalanceData = mockedPointsBalanceData,
        )
        RewardsScreenUserStatistic(
            navigate = {},
            pointsWalletAsset = WalletAsset("", PreviewerToken("", "Reserved RMO", "RRMO")),
            passportStatus = PassportStatus.UNSCANNED,
            pointsBalanceData = mockedPointsBalanceData,
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
                navigate = {},
                pointsEventData = CONST_MOCKED_EVENTS_LIST.subList(0, 2),
                passportStatus = PassportStatus.ALLOWED,
                claimEvent = {},
                refreshEvents = {}
            )
            ActiveTasksList(
                navigate = {},
                pointsEventData = listOf(),
                passportStatus = PassportStatus.ALLOWED,
                claimEvent = {},
                refreshEvents = {}
            )
            ActiveTasksList(
                navigate = {},
                pointsEventData = listOf(),
                passportStatus = PassportStatus.ALLOWED,
                claimEvent = {},
                refreshEvents = {}
            )
        }
    }
}