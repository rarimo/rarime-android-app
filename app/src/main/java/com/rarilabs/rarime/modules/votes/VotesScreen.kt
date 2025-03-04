package com.rarilabs.rarime.modules.votes

import android.net.Uri
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
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v2.details.BaseDetailsScreen
import com.rarilabs.rarime.modules.home.v2.details.DetailsProperties
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.qr.ScanQrScreen
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VotesScreen(
    id: Int,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: VotesScreenViewModel = hiltViewModel()
) {
    val mainViewModel = LocalMainViewModel.current
    val screenInsets by mainViewModel.screenInsets.collectAsState()

    val activeVotes by viewModel.activeVotes.collectAsState()
    val activeVotesLoading by viewModel.isLoadingActive.collectAsState()

    val historyVotes by viewModel.historyVotes.collectAsState()
    val historyVotesLoading by viewModel.isLoadingHistory.collectAsState()

    val props = DetailsProperties(
        id = id,
        header = "Freedomtool",
        subTitle = "Voting",
        imageId = R.drawable.freedomtool_bg,
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFD5FEC8),
                Color(0xFF80ED99)
            )
        )
    )

    VotesScreenContent(
        modifier = Modifier,
        props = props,
        onBack = onBack,
        screenInsets = mapOf(
            ScreenInsets.TOP to screenInsets.get(ScreenInsets.TOP),
            ScreenInsets.BOTTOM to screenInsets.get(ScreenInsets.BOTTOM)
        ),
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,

        activeVotes = activeVotes,
        activeVotesLoading = activeVotesLoading,
        historyVotes = historyVotes,
        historyVotesLoading = historyVotesLoading,

        qrCodeScanner = { onBackCb, onScanCb ->
            ScanQrScreen(
                onBack = { onBackCb.invoke() },
                onScan = { onScanCb.invoke(it) }
            )
        },
        onProposalScanned = {
            mainViewModel.setExtIntDataURI(Uri.parse(it))
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VotesScreenContent(
    modifier: Modifier = Modifier,
    props: DetailsProperties,
    onBack: () -> Unit,
    screenInsets: Map<ScreenInsets, Number?>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,

    activeVotes: List<VoteData>,
    activeVotesLoading: Boolean,
    historyVotes: List<VoteData>,
    historyVotesLoading: Boolean,

    qrCodeScanner: @Composable (onBackCb: () -> Unit, onScanCb: (String) -> Unit) -> Unit = { _, _ -> },
    onProposalScanned: (String) -> Unit,
) {
    var isQrCodeViewShown by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        pageCount = { 2 },
        initialPage = 0
    )
    val tabs = listOf("Active", "History")
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
            modifier = modifier
                .absolutePadding(
                    top = (screenInsets.get(ScreenInsets.TOP)?.toFloat() ?: 0f).dp,
                    bottom = (screenInsets.get(ScreenInsets.BOTTOM)?.toFloat() ?: 0f).dp,
                )
                .verticalScroll(rememberScrollState()),
            properties = props,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            onBack = onBack,
            footer = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Text(
                        style = RarimeTheme.typography.body3,
                        color = RarimeTheme.colors.textSecondary,
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
                            onClick = {},
                        ) {
                            AppIcon(id = R.drawable.ic_plus)
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

                    Column() {
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
                                            RarimeTheme.colors.baseBlack else RarimeTheme.colors.baseBlackOp40,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = 12.dp,
                            verticalAlignment = Alignment.Top,
                        ) { page ->
                            when (page) {
                                0 -> if (activeVotesLoading)
                                    VotesLoadingSkeleton()
                                else ActiveVotesList(
                                    votes = activeVotes
                                )

                                1 -> if (historyVotesLoading)
                                    VotesLoadingSkeleton()
                                else HistoryVotesList(
                                    votes = historyVotes
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
    votes: List<VoteData>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        votes.forEach {
            VoteResultsCard(it)
        }
    }
}

@Composable
fun HistoryVotesList(
    votes: List<VoteData>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        votes.forEach {
            VoteResultsCard(it)
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
                backgroundGradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD5FEC8),
                        Color(0xFF80ED99)
                    )
                )
            ),
            onBack = {},
            screenInsets = mapOf(ScreenInsets.TOP to 12, ScreenInsets.BOTTOM to 12),
            sharedTransitionScope = state,
            animatedContentScope = anim,
            activeVotes = listOf(
                VoteData(
                    title = "Protocol Update Proposal",
                    description = "Vote on the proposed update to the network protocol",
                    durationMillis = 86400000 * 3, // 3 days
                    participantsCount = 320,
                    questions = listOf(
                        VoteQuestion(
                            "2",
                            "Question 2",
                            listOf(
                                QuestionAnswerVariant("10", "Do", 1000.0),
                                QuestionAnswerVariant("11", "Eiusmod", 1100.0),
                                QuestionAnswerVariant("12", "Tempor", 1200.0),
                                QuestionAnswerVariant("13", "Incididunt", 1300.0),
                                QuestionAnswerVariant("14", "Labore", 1400.0),
                                QuestionAnswerVariant("15", "Et", 1500.0),
                                QuestionAnswerVariant("16", "Dolore", 1600.0),
                                QuestionAnswerVariant("17", "Magna", 1700.0),
                                QuestionAnswerVariant("18", "Aliqua", 1800.0),
                                QuestionAnswerVariant("19", "Ut", 1900.0),
                                QuestionAnswerVariant("20", "Enim", 2000.0),
                            )
                        ),
                        VoteQuestion(
                            "2",
                            "Question 2",
                            listOf(
                                QuestionAnswerVariant("10", "Do", 1000.0),
                                QuestionAnswerVariant("11", "Eiusmod", 1100.0),
                                QuestionAnswerVariant("12", "Tempor", 1200.0),
                                QuestionAnswerVariant("13", "Incididunt", 1300.0),
                                QuestionAnswerVariant("14", "Labore", 1400.0),
                                QuestionAnswerVariant("15", "Et", 1500.0),
                                QuestionAnswerVariant("16", "Dolore", 1600.0),
                                QuestionAnswerVariant("17", "Magna", 1700.0),
                                QuestionAnswerVariant("18", "Aliqua", 1800.0),
                                QuestionAnswerVariant("19", "Ut", 1900.0),
                                QuestionAnswerVariant("20", "Enim", 2000.0),
                            )
                        ),
                        VoteQuestion(
                            "2",
                            "Question 2",
                            listOf(
                                QuestionAnswerVariant("10", "Do", 1000.0),
                                QuestionAnswerVariant("11", "Eiusmod", 1100.0),
                                QuestionAnswerVariant("12", "Tempor", 1200.0),
                                QuestionAnswerVariant("13", "Incididunt", 1300.0),
                                QuestionAnswerVariant("14", "Labore", 1400.0),
                                QuestionAnswerVariant("15", "Et", 1500.0),
                                QuestionAnswerVariant("16", "Dolore", 1600.0),
                                QuestionAnswerVariant("17", "Magna", 1700.0),
                                QuestionAnswerVariant("18", "Aliqua", 1800.0),
                                QuestionAnswerVariant("19", "Ut", 1900.0),
                                QuestionAnswerVariant("20", "Enim", 2000.0),
                            )
                        ),
                    ),
                    endDate = System.currentTimeMillis() + 86400000 * 3
                )
            ),
            activeVotesLoading = false,
            historyVotes = listOf(
                VoteData(
                    title = "Treasury Allocation",
                    description = "Vote on allocating treasury funds for development",
                    durationMillis = 86400000 * 7, // 7 days
                    participantsCount = 412,
                    questions = listOf(
                        VoteQuestion(
                            "2",
                            "Question 2",
                            listOf(
                                QuestionAnswerVariant("10", "Do", 1000.0),
                                QuestionAnswerVariant("11", "Eiusmod", 1100.0),
                                QuestionAnswerVariant("12", "Tempor", 1200.0),
                                QuestionAnswerVariant("13", "Incididunt", 1300.0),
                                QuestionAnswerVariant("14", "Labore", 1400.0),
                                QuestionAnswerVariant("15", "Et", 1500.0),
                                QuestionAnswerVariant("16", "Dolore", 1600.0),
                                QuestionAnswerVariant("17", "Magna", 1700.0),
                                QuestionAnswerVariant("18", "Aliqua", 1800.0),
                                QuestionAnswerVariant("19", "Ut", 1900.0),
                                QuestionAnswerVariant("20", "Enim", 2000.0),
                            )
                        ),
                        VoteQuestion(
                            "2",
                            "Question 2",
                            listOf(
                                QuestionAnswerVariant("10", "Do", 1000.0),
                                QuestionAnswerVariant("11", "Eiusmod", 1100.0),
                                QuestionAnswerVariant("12", "Tempor", 1200.0),
                                QuestionAnswerVariant("13", "Incididunt", 1300.0),
                                QuestionAnswerVariant("14", "Labore", 1400.0),
                                QuestionAnswerVariant("15", "Et", 1500.0),
                                QuestionAnswerVariant("16", "Dolore", 1600.0),
                                QuestionAnswerVariant("17", "Magna", 1700.0),
                                QuestionAnswerVariant("18", "Aliqua", 1800.0),
                                QuestionAnswerVariant("19", "Ut", 1900.0),
                                QuestionAnswerVariant("20", "Enim", 2000.0),
                            )
                        ),
                        VoteQuestion(
                            "2",
                            "Question 2",
                            listOf(
                                QuestionAnswerVariant("10", "Do", 1000.0),
                                QuestionAnswerVariant("11", "Eiusmod", 1100.0),
                                QuestionAnswerVariant("12", "Tempor", 1200.0),
                                QuestionAnswerVariant("13", "Incididunt", 1300.0),
                                QuestionAnswerVariant("14", "Labore", 1400.0),
                                QuestionAnswerVariant("15", "Et", 1500.0),
                                QuestionAnswerVariant("16", "Dolore", 1600.0),
                                QuestionAnswerVariant("17", "Magna", 1700.0),
                                QuestionAnswerVariant("18", "Aliqua", 1800.0),
                                QuestionAnswerVariant("19", "Ut", 1900.0),
                                QuestionAnswerVariant("20", "Enim", 2000.0),
                            )
                        ),
                        VoteQuestion(
                            "2",
                            "Question 2",
                            listOf(
                                QuestionAnswerVariant("10", "Do", 1000.0),
                                QuestionAnswerVariant("11", "Eiusmod", 1100.0),
                                QuestionAnswerVariant("12", "Tempor", 1200.0),
                                QuestionAnswerVariant("13", "Incididunt", 1300.0),
                                QuestionAnswerVariant("14", "Labore", 1400.0),
                                QuestionAnswerVariant("15", "Et", 1500.0),
                                QuestionAnswerVariant("16", "Dolore", 1600.0),
                                QuestionAnswerVariant("17", "Magna", 1700.0),
                                QuestionAnswerVariant("18", "Aliqua", 1800.0),
                                QuestionAnswerVariant("19", "Ut", 1900.0),
                                QuestionAnswerVariant("20", "Enim", 2000.0),
                            )
                        ),
                    ),
                    endDate = System.currentTimeMillis() + 86400000 * 5
                )
            ),
            historyVotesLoading = false,
            onProposalScanned = {},
        )
    }
}