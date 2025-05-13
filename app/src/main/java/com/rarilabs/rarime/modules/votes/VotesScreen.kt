package com.rarilabs.rarime.modules.votes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.MOCKED_POLL_ITEM
import com.rarilabs.rarime.api.voting.models.Poll
import com.rarilabs.rarime.modules.home.v2.details.BaseDetailsScreen
import com.rarilabs.rarime.modules.home.v2.details.DetailsProperties
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.qr.ScanQrScreen
import com.rarilabs.rarime.modules.votes.voteProcessScreen.VotingAppSheet
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import kotlinx.coroutines.launch


enum class VotingStatus {
    LOADING,
    ALREADY_VOTED,
    ALLOWED,
    NOT_STARTED
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VotesScreen(
    id: Int,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
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

    val props = DetailsProperties(
        id = id,
        header = stringResource(R.string.freedomtool),
        subTitle = stringResource(R.string.voting),
        imageId = R.drawable.freedomtool_bg,
        imageModifier = Modifier.padding(horizontal = 50.dp),
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFD5FEC8),
                Color(0xFF80ED99)
            )
        )
    )

    VotingAppSheet(
        navigate = navigate,
        voteSheetState = voteSheetState,
        selectedPoll = selectedVote
    )

    VotesScreenContent(
        modifier = Modifier,
        props = props,
        onBack = onBack,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        activeVotes = activeVotes,
        isLoading = isLoading,
        historyVotes = historyVotes,
        qrCodeScanner = { onBackCb, onScanCb ->
            ScanQrScreen(
                innerPaddings = innerPaddings,
                onBack = { onBackCb.invoke() },
                onScan = { onScanCb.invoke(it) }
            )
        },
        innerPaddings = innerPaddings,
        onProposalScanned = {
            val uri = it.toUri()
            mainViewModel.setExtIntDataURI(uri)
        },
        onVoteClick = {
            viewModel.setSelectedPoll(it)
            voteSheetState.show()
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VotesScreenContent(
    modifier: Modifier = Modifier,
    props: DetailsProperties,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    innerPaddings: Map<ScreenInsets, Number>,
    activeVotes: List<Poll>,
    isLoading: Boolean,
    historyVotes: List<Poll>,
    qrCodeScanner: @Composable (onBackCb: () -> Unit, onScanCb: (String) -> Unit) -> Unit = { _, _ -> },
    onProposalScanned: (String) -> Unit,
    onVoteClick: (Poll) -> Unit,
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
        BaseDetailsScreen(
            innerPaddings = innerPaddings,
            modifier = modifier,
            properties = props,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            onBack = onBack,
            body = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Text(
                        style = RarimeTheme.typography.body3,
                        color = RarimeTheme.colors.baseBlackOp50,
                        text = "An identification and privacy solution that revolutionizes polling, surveying and election processes"
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
                            AppIcon(id = R.drawable.ic_plus, tint = RarimeTheme.colors.baseBlack)
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
                            modifier = Modifier.padding(bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp),
                            pageSpacing = 12.dp,
                            verticalAlignment = Alignment.Top,
                        ) { page ->
                            when (page) {
                                0 -> if (isLoading)
                                    VotesLoadingSkeleton()
                                else if (activeVotes.isEmpty()) {

                                } else ActiveVotesList(
                                    votes = activeVotes,
                                    onClick = {
                                        onVoteClick.invoke(it)
                                    }
                                )

                                1 -> if (isLoading)
                                    VotesLoadingSkeleton()
                                else if (historyVotes.isEmpty()) {

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
        )
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


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun VotesScreenPreview() {
    PrevireSharedAnimationProvider { state, anim ->
        VotesScreenContent(
            props = DetailsProperties(
                id = 0,
                header = "Freedomtool",
                subTitle = "Voting",
                imageId = R.drawable.freedomtool_bg,
                imageModifier = Modifier,
                backgroundGradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD5FEC8),
                        Color(0xFF80ED99)
                    )
                )
            ),
            onBack = {},
            sharedTransitionScope = state,
            animatedContentScope = anim,
            activeVotes = listOf(
                MOCKED_POLL_ITEM,
                MOCKED_POLL_ITEM,
                MOCKED_POLL_ITEM
            ),
            isLoading = false,
            historyVotes = listOf(
                MOCKED_POLL_ITEM,
                MOCKED_POLL_ITEM,
                MOCKED_POLL_ITEM
            ),
            onProposalScanned = {},
            innerPaddings = mapOf(ScreenInsets.TOP to 23, ScreenInsets.BOTTOM to 12),
            onVoteClick = {},
        )
    }
}