package com.rarilabs.rarime.modules.home.v3.ui.expanded

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.MOCKED_POLL_ITEM
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_DOT_MAP_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetTitle
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.qr.ScanQrScreen
import com.rarilabs.rarime.modules.votes.ActiveVotesList
import com.rarilabs.rarime.modules.votes.HistoryVotesList
import com.rarilabs.rarime.modules.votes.VotesLoadingSkeleton
import com.rarilabs.rarime.modules.votes.VotesScreenViewModel
import com.rarilabs.rarime.modules.votes.voteProcessScreen.VotingAppSheet
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SecondaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import kotlinx.coroutines.launch

@Composable
fun FreedomtoolExpandedWidget(
    modifier: Modifier = Modifier,
    expandedWidgetProps: BaseWidgetProps.Expanded,
    navigate: (String) -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    viewModel: VotesScreenViewModel = hiltViewModel()
) {
    val mainViewModel = LocalMainViewModel.current
    val activeVotes by viewModel.activeVotes.collectAsState()
    val historyVotes by viewModel.historyVotes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedPoll by viewModel.selectedVote.collectAsState()
    val voteSheetState = rememberAppSheetState()

    LaunchedEffect(Unit) { viewModel.loadPolls() }
    VotingAppSheet(
        navigate = navigate,
        voteSheetState = voteSheetState,
        selectedPoll = selectedPoll
    )

    var showQrScan by remember { mutableStateOf(false) }
    if (showQrScan) {
        ScanQrScreen(
            innerPaddings = innerPaddings,
            onBack = { showQrScan = false },
            onScan = {
                mainViewModel.setExtIntDataURI(it.toUri())
                showQrScan = false
            }
        )
    } else {
        FreedomtoolExpandedWidgetContent(
            widgetProps = expandedWidgetProps,
            modifier = modifier,
            innerPaddings = innerPaddings,
            activeVotes = activeVotes,
            historyVotes = historyVotes,
            isLoading = isLoading,
            onScan = { showQrScan = true },
            onVoteClick = {
                viewModel.setSelectedPoll(it)
                voteSheetState.show()
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FreedomtoolExpandedWidgetContent(
    modifier: Modifier = Modifier,
    widgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    activeVotes: List<Poll>,
    historyVotes: List<Poll>,
    isLoading: Boolean,
    onScan: () -> Unit,
    onVoteClick: (Poll) -> Unit
) {
    with(widgetProps) {
        with(sharedTransitionScope) {
            BaseExpandedWidget(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) }
                    ),
                header = {
                    Header(
                        layoutId = layoutId,
                        onCollapse = onCollapse,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        innerPaddings = innerPaddings
                    )
                },
                body = {
                    Body(
                        layoutId = layoutId,
                        activeVotes = activeVotes,
                        historyVotes = historyVotes,
                        isLoading = isLoading,
                        onScan = onScan,
                        onVoteClick = onVoteClick,
                        innerPaddings = innerPaddings,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                },
                background = {
                    Background(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Header(
    layoutId: Int,
    onCollapse: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    innerPaddings: Map<ScreenInsets, Number>,
) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.header(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp),
            horizontalArrangement = Arrangement.End
        ) {

            IconButton(
                onClick = onCollapse,
                modifier = Modifier
                    .padding(20.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = RarimeTheme.colors.componentPrimary)
            ) {
                AppIcon(
                    id = R.drawable.ic_close_fill,
                    tint = RarimeTheme.colors.textPrimary,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Body(
    layoutId: Int,
    activeVotes: List<Poll>,
    historyVotes: List<Poll>,
    isLoading: Boolean,
    onScan: () -> Unit,
    onVoteClick: (Poll) -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val uriHandler = LocalUriHandler.current
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.content(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )

                .fillMaxSize()
        ) {

            Spacer(Modifier.height((BG_DOT_MAP_HEIGHT - 80).dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.backgroundPrimary,
                        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                    )
                    .padding(20.dp)
                    .fillMaxWidth()

            ) {
                BaseWidgetTitle(
                    title = stringResource(R.string.freedomtool_title),
                    accentTitle = stringResource(R.string.fredomtool_accent_title),
                    titleStyle = RarimeTheme.typography.h1.copy(color = RarimeTheme.colors.invertedDark),
                    titleModifier =
                        Modifier.sharedBounds(
                            rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        ),
                    accentTitleStyle = RarimeTheme.typography.h1.copy(brush = RarimeTheme.colors.gradient15),
                    accentTitleModifier =
                        Modifier.sharedBounds(
                            rememberSharedContentState(
                                HomeSharedKeys.accentTitle(
                                    layoutId
                                )
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                        ),

                    )
                Text(
                    text = "Cast secure, anonymous votes from anywhere, just using your passport",
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textSecondary
                )
                VoteActionRow(
                    onLink = { uriHandler.openUri(BaseConfig.VOTING_WEBSITE_URL) },
                    onScan = onScan
                )
                HorizontalDivider(
                    modifier = Modifier
                        .background(RarimeTheme.colors.componentPrimary)
                        .fillMaxWidth()

                )
                VoteTabs(
                    activeVotes = activeVotes,
                    historyVotes = historyVotes,
                    isLoading = isLoading,
                    onVoteClick = onVoteClick,
                    innerPaddings = innerPaddings
                )
            }
        }
    }
}

@Composable
private fun VoteActionRow(
    onLink: () -> Unit,
    onScan: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SecondaryButton(
            onClick = onLink,
            size = ButtonSize.Large,
            text = "Create a poll", modifier = Modifier.weight(1f)

        )
        Spacer(modifier = Modifier.width(16.dp))
        PrimaryButton(
            onClick = onScan,
            size = ButtonSize.Large,
            text = "Scan a QR",
            leftIcon = R.drawable.ic_qr_scan_2_line,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun VoteTabs(
    activeVotes: List<Poll>,
    historyVotes: List<Poll>,
    isLoading: Boolean,
    onVoteClick: (Poll) -> Unit,
    innerPaddings: Map<ScreenInsets, Number>
) {
    val tabs = listOf("Active", "Finished")
    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = 0)
    val scope = rememberCoroutineScope()

    Row {
        tabs.forEachIndexed { idx, title ->
            val sel = pagerState.currentPage == idx
            val counter = if(idx==0) activeVotes.size else historyVotes.size
            Text(
                text = counter.toString() +" " + title.uppercase(),
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (sel) RarimeTheme.colors.componentPrimary else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = true)
                    ) { scope.launch { pagerState.animateScrollToPage(idx) } }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                style = RarimeTheme.typography.overline2,
                color = if (sel) RarimeTheme.colors.textPrimary else RarimeTheme.colors.textSecondary
            )
        }
    }
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.padding(bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp + 20.dp),
        pageSpacing = 12.dp,
        verticalAlignment = Alignment.Top
    ) { page ->
        when {
            isLoading -> VotesLoadingSkeleton()
            page == 0 && activeVotes.isEmpty() -> EmptyState("No active polls")
            page == 0 -> ActiveVotesList(votes = activeVotes, onClick = onVoteClick)
            page == 1 && historyVotes.isEmpty() -> EmptyState("No polls")
            else -> HistoryVotesList(historyVotes) { onVoteClick(it) }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(text = text, color = RarimeTheme.colors.textSecondary, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Background(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.gradient5)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_bg_freedomtool_voting),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BG_DOT_MAP_HEIGHT.dp)
                    .sharedBounds(
                        rememberSharedContentState(
                            HomeSharedKeys.image(
                                layoutId
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun FreedomtoolExpandedWidgetPreview_light() {
    AppTheme {
        PrevireSharedAnimationProvider { sts, avs ->
            FreedomtoolExpandedWidgetContent(
                widgetProps = BaseWidgetProps.Expanded(
                    onCollapse = {},
                    layoutId = WidgetType.FREEDOMTOOL.layoutId,
                    animatedVisibilityScope = avs,
                    sharedTransitionScope = sts
                ),
                modifier = Modifier.fillMaxSize(),
                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
                activeVotes = listOf(MOCKED_POLL_ITEM, MOCKED_POLL_ITEM),
                historyVotes = emptyList(),
                isLoading = false,
                onScan = {},
                onVoteClick = {}
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun FreedomtoolExpandedWidgetPreview_dark() {
    AppTheme {
        PrevireSharedAnimationProvider { sts, avs ->
            FreedomtoolExpandedWidgetContent(
                widgetProps = BaseWidgetProps.Expanded(
                    onCollapse = {},
                    layoutId = WidgetType.FREEDOMTOOL.layoutId,
                    animatedVisibilityScope = avs,
                    sharedTransitionScope = sts
                ),
                modifier = Modifier.fillMaxSize(),
                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
                activeVotes = listOf(MOCKED_POLL_ITEM, MOCKED_POLL_ITEM),
                historyVotes = emptyList(),
                isLoading = false,
                onScan = {},
                onVoteClick = {}
            )
        }
    }
}
