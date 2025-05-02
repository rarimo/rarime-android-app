package com.rarilabs.rarime.modules.home.v3

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.FreedomtoolCollapsedCard
import com.rarilabs.rarime.modules.home.v3.ui.components.HomeHeader
import com.rarilabs.rarime.modules.home.v3.ui.components.VerticalPageIndicator
import com.rarilabs.rarime.modules.home.v3.ui.expanded.FreedomtoolExpandedCard
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.store.room.notifications.models.NotificationEntityData
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import com.rarilabs.rarime.util.Screen
import kotlin.math.abs

enum class CardType(val layoutId: Int) {
    FREEDOMTOOL(0), ANOTHER_ONE(1)
}

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
    val notifications: List<NotificationEntityData> by viewModel.notifications.collectAsState()
    val notificationsCount by remember(notifications) {
        derivedStateOf { notifications.count { it.isActive } }
    }

    HomeScreenContent(
        userPassportName = passport?.personDetails?.name,
        notificationsCount = notificationsCount,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPaddings[ScreenInsets.TOP]?.toFloat()?.dp ?: 0.dp,
                bottom = innerPaddings[ScreenInsets.BOTTOM]?.toFloat()?.dp ?: 0.dp
            ),
        navigate = navigate,
        sharedTransitionScope = sharedTransitionScope,
        setVisibilityOfBottomBar = setVisibilityOfBottomBar,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    setVisibilityOfBottomBar: (Boolean) -> Unit,
    userPassportName: String?,
    notificationsCount: Int?
) {
    var selectedCard by remember { mutableStateOf<CardType?>(null) }
    LaunchedEffect(selectedCard) {
        setVisibilityOfBottomBar(selectedCard == null)
    }

    // Hoist pagerState to remember scroll position across recompositions
    val pagerState = rememberPagerState(pageCount = { CardType.entries.size })

    Box(modifier = modifier) {
        AnimatedContent(targetState = selectedCard) { targetState ->
            if (targetState == null) {
                Column {
                    HomeHeader(
                        notificationsCount = notificationsCount,
                        name = userPassportName,
                        onNotificationClick = { navigate(Screen.NotificationsList.route) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VerticalPager(
                            modifier = Modifier.weight(1f),
                            state = pagerState,
                            contentPadding = PaddingValues(bottom = 8.dp),
                        ) { page ->
                            val cardType = CardType.entries[page]

                            val pageOffset by remember(
                                pagerState.currentPage, pagerState.currentPageOffsetFraction
                            ) {
                                derivedStateOf {
                                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                }
                            }

                            val absoluteOffset = abs(pageOffset).coerceIn(0f, 1f)
                            val targetScale = lerp(0.8f, 1f, 1f - absoluteOffset)

                            val scale by animateFloatAsState(
                                targetValue = targetScale, animationSpec = spring(
                                    dampingRatio = 0.5f, stiffness = 300f
                                )
                            )

                            // Common props for every collapsed card
                            val collapsedCardProps = BaseCardProps.Collapsed(
                                onExpand = { selectedCard = cardType },
                                layoutId = cardType.layoutId,
                                animatedVisibilityScope = this@AnimatedContent,
                                sharedTransitionScope = sharedTransitionScope
                            )

                            when (cardType) {
                                CardType.FREEDOMTOOL -> FreedomtoolCollapsedCard(
                                    collapsedCardProps = collapsedCardProps,
                                    modifier = Modifier.graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        alpha = lerp(0.8f, 1f, 1f - absoluteOffset)
                                    },
                                )

                                CardType.ANOTHER_ONE -> FreedomtoolCollapsedCard(
                                    collapsedCardProps = collapsedCardProps,
                                    modifier = Modifier.graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        alpha = lerp(0.8f, 1f, 1f - absoluteOffset)
                                    },
                                )
                            }
                        }
                        VerticalPageIndicator(
                            numberOfPages = pagerState.pageCount,
                            selectedPage = pagerState.currentPage,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            defaultRadius = 6.dp,
                            selectedColor = RarimeTheme.colors.primaryMain,
                            defaultColor = RarimeTheme.colors.primaryLight,
                            selectedLength = 16.dp,
                            space = 8.dp
                        )
                    }
                }

            } else {
                // Expanded: one card is visible on top
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                ) {
                    // Common props for every expanded card
                    val expandedCardProps = BaseCardProps.Expanded(
                        onCollapse = { selectedCard = null },
                        layoutId = targetState.layoutId,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = sharedTransitionScope
                    )

                    when (targetState) {
                        CardType.FREEDOMTOOL -> FreedomtoolExpandedCard(
                            expandedCardProps = expandedCardProps
                        )

                        CardType.ANOTHER_ONE -> FreedomtoolExpandedCard(
                            expandedCardProps = expandedCardProps
                        )
                    }
                }
            }
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
            )
        }
    }
}
