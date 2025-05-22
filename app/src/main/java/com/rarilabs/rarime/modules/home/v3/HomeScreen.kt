package com.rarilabs.rarime.modules.home.v3

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.model.CardType
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.ClaimCollapsedCard
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.FreedomtoolCollapsedCard
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.HiddenPrizeCollapsedCard
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.IdentityCollapsedCard
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.LikenessCollapsedCard
import com.rarilabs.rarime.modules.home.v3.ui.components.HomeHeader
import com.rarilabs.rarime.modules.home.v3.ui.components.VerticalPageIndicator
import com.rarilabs.rarime.modules.home.v3.ui.expanded.ClaimExpandedCard
import com.rarilabs.rarime.modules.home.v3.ui.expanded.FreedomtoolExpandedCard
import com.rarilabs.rarime.modules.home.v3.ui.expanded.HiddenPrizeExpandedCard
import com.rarilabs.rarime.modules.home.v3.ui.expanded.IdentityExpandedCard
import com.rarilabs.rarime.modules.home.v3.ui.expanded.LikenessExpandedCard
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenV3(
    navigate: (String) -> Unit,
    navigateWithPopUp: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    setVisibilityOfBottomBar: (Boolean) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val passport by viewModel.passport.collectAsState()
    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val hasVotes by viewModel.hasVotes.collectAsState()
    val pointsBalance by viewModel.pointsEventData.collectAsState()
    val currentPointsBalance = pointsBalance?.attributes?.balance?.attributes?.amount
    val notificationsCount by remember(notifications) {
        derivedStateOf { notifications.count { it.isActive } }
    }
    val colorScheme by viewModel.colorScheme.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initHomeData()
    }

    val visibleCards = remember(hasVotes) {
        buildList {
//            if (hasVotes) {
//                add(CardType.FREEDOMTOOL)
//            }
//            add(CardType.IDENTITY)
//            if (currentPointsBalance != null && currentPointsBalance != 0L) {
//                add(CardType.CLAIM)
//            }
//            add(CardType.CLAIM)
            // TODO: Remove after tests and uncomment code above
            add(CardType.FREEDOMTOOL)
            add(CardType.IDENTITY)
            add(CardType.LIKENESS)
            add(CardType.CLAIM)
            add(CardType.HIDDEN_PRIZE)
        }
    }

    HomeScreenContent(
        visibleCards = visibleCards,
        userPassportName = passport?.personDetails?.name,
        notificationsCount = notificationsCount,
        innerPaddings = innerPaddings,
        modifier = Modifier.fillMaxSize(),
        navigate = navigate,
        sharedTransitionScope = sharedTransitionScope,
        setVisibilityOfBottomBar = setVisibilityOfBottomBar,
        currentPointsBalance = currentPointsBalance,
        colorScheme = colorScheme
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    setVisibilityOfBottomBar: (Boolean) -> Unit,
    visibleCards: List<CardType>,
    userPassportName: String?,
    notificationsCount: Int?,
    currentPointsBalance: Long?,
    colorScheme: AppColorScheme
) {
    var selectedCardType by remember { mutableStateOf<CardType?>(null) }
    LaunchedEffect(selectedCardType) {
        setVisibilityOfBottomBar(selectedCardType == null)
    }

    // Hoist pagerState to remember scroll position across recompositions
    val pagerState = rememberPagerState(pageCount = { visibleCards.size })

    Box(modifier = modifier) {
        // Temporarily disable pager scrolling while the expand/collapse animation runs
        var pagerScrollEnabled by remember { mutableStateOf(true) }
        LaunchedEffect(selectedCardType) {
            pagerScrollEnabled = false
            delay((ANIMATION_DURATION_MS + 200).toLong())
            pagerScrollEnabled = true
        }

        AnimatedContent(selectedCardType) { targetCardType ->
            if (targetCardType == null) {
                Column(
                    modifier = Modifier.padding(
                        top = innerPaddings[ScreenInsets.TOP]?.toFloat()?.dp ?: 0.dp,
                        bottom = innerPaddings[ScreenInsets.BOTTOM]?.toFloat()?.dp ?: 0.dp
                    )
                ) {
                    HomeHeader(
                        notificationsCount = notificationsCount,
                        name = userPassportName,
                        onNotificationClick = { navigate(Screen.NotificationsList.route) })
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        VerticalPager(
                            modifier = Modifier.weight(1f),
                            userScrollEnabled = pagerScrollEnabled,
                            state = pagerState,
                            pageSpacing = 10.dp,
                            contentPadding = PaddingValues(top = 42.dp, bottom = 95.dp),
                        ) { page ->
                            val cardType = visibleCards[page]
                            val currentPage = pagerState.currentPage
                            val currentOffset = pagerState.currentPageOffsetFraction
                            val pageOffset = (currentPage - page) + currentOffset
                            val absoluteOffset = abs(pageOffset).coerceIn(0f, 1f)
                            val targetScale = lerp(0.9f, 1f, 1f - absoluteOffset)
                            val scale by animateFloatAsState(
                                targetValue = targetScale, animationSpec = spring(
                                    dampingRatio = 0.5f, stiffness = 300f
                                )
                            )

                            // Common props for every collapsed card
                            val collapsedCardProps = BaseCardProps.Collapsed(
                                onExpand = {
                                    if (pagerScrollEnabled) {
                                        selectedCardType = cardType
                                    }
                                },
                                layoutId = cardType.layoutId,
                                animatedVisibilityScope = this@AnimatedContent,
                                sharedTransitionScope = sharedTransitionScope
                            )

                            val baseCollapsedModifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                alpha = lerp(0.8f, 1f, 1f - absoluteOffset)
                            }

                            when (cardType) {
                                CardType.FREEDOMTOOL -> FreedomtoolCollapsedCard(
                                    collapsedCardProps = collapsedCardProps,
                                    modifier = baseCollapsedModifier,
                                )

                                CardType.IDENTITY -> IdentityCollapsedCard(
                                    collapsedCardProps = collapsedCardProps,
                                    modifier = baseCollapsedModifier,
                                )

                                CardType.LIKENESS -> LikenessCollapsedCard(
                                    collapsedCardProps = collapsedCardProps,
                                    modifier = baseCollapsedModifier,
                                )

                                CardType.CLAIM -> ClaimCollapsedCard(
                                    collapsedCardProps = collapsedCardProps,
                                    modifier = baseCollapsedModifier,
                                    currentPointsBalance = currentPointsBalance
                                )

                                CardType.HIDDEN_PRIZE -> HiddenPrizeCollapsedCard(
                                    collapsedCardProps = collapsedCardProps,
                                    modifier = baseCollapsedModifier,
                                    colorScheme = colorScheme
                                )
                                // TODO: Implement rest collapsed cards here
                            }
                        }
                        VerticalPageIndicator(
                            totalPages = pagerState.pageCount,
                            selectedPage = pagerState.currentPage,
                            modifier = Modifier.padding(end = 8.dp),
                            defaultSize = 6.dp,
                            selectedColor = RarimeTheme.colors.primaryMain,
                            defaultColor = RarimeTheme.colors.primaryLight,
                            selectedHeight = 16.dp,
                            space = 8.dp
                        )
                    }
                }

            } else {
                // Expanded: one card is visible on top
                BackHandler {
                    selectedCardType = null
                }

                Box(
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    // Common props for every expanded card
                    val expandedCardProps = BaseCardProps.Expanded(
                        onCollapse = { selectedCardType = null },
                        layoutId = targetCardType.layoutId,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = sharedTransitionScope
                    )

                    when (targetCardType) {
                        CardType.FREEDOMTOOL -> FreedomtoolExpandedCard(
                            expandedCardProps = expandedCardProps,
                            innerPaddings = innerPaddings,
                            navigate = navigate
                        )

                        CardType.IDENTITY -> IdentityExpandedCard(
                            expandedCardProps = expandedCardProps,
                            innerPaddings = innerPaddings,
                            navigate = navigate,
                            setVisibilityOfBottomBar = setVisibilityOfBottomBar
                        )

                        CardType.LIKENESS -> LikenessExpandedCard(
                            expandedCardProps = expandedCardProps,
                            innerPaddings = innerPaddings,
                            navigate = navigate
                        )

                        CardType.CLAIM -> ClaimExpandedCard(
                            expandedCardProps = expandedCardProps,
                            innerPaddings = innerPaddings,
                            navigate = navigate,
                            currentPointsBalance = currentPointsBalance
                        )

                        CardType.HIDDEN_PRIZE -> HiddenPrizeExpandedCard(
                            expandedCardProps = expandedCardProps, innerPaddings = innerPaddings
                        )
                        // TODO: Implement rest expanded cards here
                    }
                }
            }
        }

        // Overlay which temporarily disable pager scrolling while the expand/collapse animation runs
        if (!pagerScrollEnabled) {
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .zIndex(200f)
                    .matchParentSize()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                    })
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun HomeScreenPreview() {
    PrevireSharedAnimationProvider { sharedTransitionScope, _ ->
        Surface {
            HomeScreenContent(
                modifier = Modifier.fillMaxSize(),
                sharedTransitionScope = sharedTransitionScope,
                navigate = {},
                setVisibilityOfBottomBar = {},
                userPassportName = "Mike",
                notificationsCount = 2,
                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
                visibleCards = CardType.entries,
                currentPointsBalance = 200L,
                colorScheme = AppColorScheme.SYSTEM
            )
        }
    }
}
