package com.rarilabs.rarime.modules.home.v3.ui.expanded

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_DOT_MAP_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedCard
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.qr.ScanQrScreen
import com.rarilabs.rarime.modules.votes.ActiveVotesList
import com.rarilabs.rarime.modules.votes.HistoryVotesList
import com.rarilabs.rarime.modules.votes.VoteResultsCard
import com.rarilabs.rarime.modules.votes.VotesLoadingSkeleton
import com.rarilabs.rarime.modules.votes.VotesScreenViewModel
import com.rarilabs.rarime.modules.votes.voteProcessScreen.VotingAppSheet
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.launch

@Composable
fun FreedomtoolExpandedCard(
    expandedCardProps: BaseCardProps.Expanded,
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    viewModel: VotesScreenViewModel = hiltViewModel()
) {
    val mainViewModel = LocalMainViewModel.current

    val activeVotes by viewModel.activeVotes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val historyVotes by viewModel.historyVotes.collectAsState()

    val selectedVote by viewModel.selectedVote.collectAsState()

    val voteSheetState = rememberAppSheetState()

    LaunchedEffect(Unit) {
        viewModel.loadPolls()
    }

    VotingAppSheet(
        navigate = navigate,
        voteSheetState = voteSheetState,
        selectedPoll = selectedVote
    )

    FreedomtoolExpandedCardContent(
        expandedCardProps = expandedCardProps,
        activeVotes = activeVotes,
        onProposalScanned = {
            val uri = it.toUri()
            mainViewModel.setExtIntDataURI(uri)
        },
        onVoteClick = {
            viewModel.setSelectedPoll(it)
            voteSheetState.show()
        },
        innerPaddings = innerPaddings,
        qrCodeScanner = { onBackCb, onScanCb ->
            ScanQrScreen(
                innerPaddings = innerPaddings,
                onBack = { onBackCb.invoke() },
                onScan = { onScanCb.invoke(it) }
            )
        },
        historyVotes = historyVotes,
        isLoading = isLoading,
        modifier = modifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FreedomtoolExpandedCardContent(
    modifier: Modifier = Modifier,
    innerPaddings: Map<ScreenInsets, Number> = mapOf(
        ScreenInsets.TOP to 0,
        ScreenInsets.BOTTOM to 0
    ),
    expandedCardProps: BaseCardProps.Expanded,
    activeVotes: List<Poll>,
    historyVotes: List<Poll>,
    isLoading: Boolean,
    onProposalScanned: (String) -> Unit,
    onVoteClick: (Poll) -> Unit,
    qrCodeScanner: @Composable (onBackCb: () -> Unit, onScanCb: (String) -> Unit) -> Unit = { _, _ -> },
) {

    var isQrCodeViewShown by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    val tabs = listOf("Active", "History")
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
        initialPage = 0
    )
    val scope = rememberCoroutineScope()

    if (isQrCodeViewShown) {
        qrCodeScanner(
            { isQrCodeViewShown = false },
            {
                onProposalScanned(it)
                isQrCodeViewShown = false
            }
        )
    } else {
        with(expandedCardProps) {
            with(sharedTransitionScope) {
                BaseExpandedCard(
                    modifier = modifier
                        .sharedElement(
                            state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = ANIMATION_DURATION_MS)
                            },
                        ),
                    header = {
                        Row(
                            modifier = Modifier
                                .sharedBounds(
                                    rememberSharedContentState(HomeSharedKeys.header(layoutId)),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                )
                                .padding(
                                    top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp,
                                    bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp
                                )
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = onCollapse) {
                                AppIcon(id = R.drawable.ic_close)
                            }
                        }
                    },
                    body = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .sharedBounds(
                                    rememberSharedContentState(HomeSharedKeys.content(layoutId)),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    renderInOverlayDuringTransition = false,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                                )
                        ) {
                            Spacer(Modifier.height((BG_DOT_MAP_HEIGHT - 150).dp))
                            BaseCardTitle(
                                title = "RariMe",
                                accentTitle = "Voting",
                                caption = "* Nothing leaves this device",
                                titleModifier =
                                    Modifier.sharedBounds(
                                        rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                    ),
                                titleStyle = RarimeTheme.typography.h1.copy(color = RarimeTheme.colors.baseBlack),
                                accentTitleStyle = RarimeTheme.typography.additional1.copy(color = RarimeTheme.colors.baseBlackOp40),
                                accentTitleModifier =
                                    Modifier.sharedBounds(
                                        rememberSharedContentState(
                                            HomeSharedKeys.gradientTitle(
                                                layoutId
                                            )
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                    ),
                                captionModifier =
                                    Modifier.sharedBounds(
                                        rememberSharedContentState(
                                            HomeSharedKeys.caption(
                                                layoutId
                                            )
                                        ),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                                    )

                            )
                            Text(
                                "An identification and privacy solution that revolutionizes polling, surveying and election processes",
                                style = RarimeTheme.typography.body3,
                                color = RarimeTheme.colors.baseBlackOp50,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .height(56.dp)
                                        .background(
                                            RarimeTheme.colors.componentPrimary,
                                            RoundedCornerShape(20.dp)
                                        ),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = RarimeTheme.colors.textPrimary,
                                        disabledContainerColor = RarimeTheme.colors.componentDisabled,
                                        disabledContentColor = RarimeTheme.colors.textDisabled
                                    ),
                                    onClick = {
                                        uriHandler.openUri(BaseConfig.VOTING_WEBSITE_URL)
                                    },
                                ) {
                                    AppIcon(
                                        id = R.drawable.ic_plus,
                                        tint = RarimeTheme.colors.baseBlack
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                TransparentButton(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    size = ButtonSize.Large,
                                    text = "Scan a QR",
                                    onClick = {
                                        isQrCodeViewShown = true
                                    }
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .background(RarimeTheme.colors.componentPrimary)
                                    .fillMaxWidth()
                                    .height(2.dp)
                            )

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    tabs.forEachIndexed { index, title ->
                                        Box(
                                            modifier = Modifier
                                                .padding(0.dp)
                                                .background(
                                                    color = if (pagerState.currentPage == index) RarimeTheme.colors.componentPrimary else Color.Transparent,
                                                    shape = RoundedCornerShape(100.dp)
                                                )
                                                .clip(RoundedCornerShape(100.dp))
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = rememberRipple(bounded = true)
                                                ) {
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(
                                                            index,
                                                            animationSpec = tween(
                                                                durationMillis = 500,
                                                            )
                                                        )
                                                    }
                                                }
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                        ) {
                                            Text(
                                                text = title.uppercase(),
                                                style = RarimeTheme.typography.overline2,
                                                color = if (pagerState.currentPage == index)
                                                    RarimeTheme.colors.baseBlack else RarimeTheme.colors.baseBlack.copy(
                                                    0.4f
                                                ),
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.padding(bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp + 20.dp),
                                    pageSpacing = 12.dp,
                                    verticalAlignment = Alignment.Top,
                                ) { page ->
                                    when (page) {
                                        0 -> if (isLoading)
                                            VotesLoadingSkeleton()
                                        else if (activeVotes.isEmpty()) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 10.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    "No active votes",
                                                    color = RarimeTheme.colors.baseBlackOp40,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        } else ActiveVotesList(
                                            votes = activeVotes,
                                            onClick = {
                                                onVoteClick.invoke(it)
                                            }
                                        )

                                        1 -> if (isLoading)
                                            VotesLoadingSkeleton()
                                        else if (historyVotes.isEmpty()) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 10.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    "No votes",
                                                    textAlign = TextAlign.Center,
                                                    color = RarimeTheme.colors.baseBlackOp40
                                                )
                                            }
                                        } else HistoryVotesList(
                                            votes = historyVotes,
                                            onClick = {
                                                onVoteClick.invoke(it)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    overlay = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(RarimeTheme.colors.gradient5)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.freedomtool_bg),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(BG_DOT_MAP_HEIGHT.dp)
                                    .offset(y = (-80).dp)
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
                )
            }
        }
    }
}

@Composable
fun ActiveVotesList(
    onClick: (Poll) -> Unit,
    votes: List<Poll>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        votes.forEach {
            VoteResultsCard(it, onCLick = onClick)
        }
    }
}

@Composable
fun HistoryVotesList(
    votes: List<Poll>,
    onClick: (Poll) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        votes.forEach {
            VoteResultsCard(it, onCLick = onClick)
        }
    }
}

