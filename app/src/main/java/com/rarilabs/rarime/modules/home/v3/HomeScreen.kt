package com.rarilabs.rarime.modules.home.v3

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.modules.home.v2.VerticalPageIndicator
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.FreedomtoolCollapsedCard
import com.rarilabs.rarime.modules.home.v3.ui.expanded.FreedomtoolExpandedCard
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

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
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()
    HomeScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPaddings[ScreenInsets.TOP]?.toFloat()?.dp ?: 0.dp,
                bottom = innerPaddings[ScreenInsets.BOTTOM]?.toFloat()?.dp ?: 0.dp
            ),
        sharedTransitionScope = sharedTransitionScope,
        setVisibilityOfBottomBar = setVisibilityOfBottomBar
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    setVisibilityOfBottomBar: (Boolean) -> Unit
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VerticalPager(
                        modifier = Modifier.weight(1f),
                        state = pagerState,
                        contentPadding = PaddingValues(vertical = 32.dp),
                    ) { page ->
                        val cardType = CardType.entries[page]
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
                            )

                            CardType.ANOTHER_ONE -> FreedomtoolCollapsedCard(
                                collapsedCardProps = collapsedCardProps,
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
                setVisibilityOfBottomBar = {})
        }
    }
}
