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
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.EarnCollapsedWidget
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.FreedomtoolCollapsedWidget
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.HiddenPrizeCollapsedWidget
import com.rarilabs.rarime.modules.home.v3.ui.collapsed.RecoveryMethodCollapsedWidget
import com.rarilabs.rarime.modules.home.v3.ui.components.HomeHeader
import com.rarilabs.rarime.modules.home.v3.ui.components.VerticalPageIndicator
import com.rarilabs.rarime.modules.home.v3.ui.expanded.EarnExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.expanded.FreedomtoolExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.expanded.HiddenPrizeExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.expanded.RecoveryMethodExpandedWidget
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.manageWidgets.ManageWidgetsBottomSheet
import com.rarilabs.rarime.modules.manageWidgets.ManageWidgetsButton
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.rememberAppSheetState
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
    val notificationsCount by remember(notifications) {
        derivedStateOf { notifications.count { it.isActive } }
    }
    val colorScheme by viewModel.colorScheme.collectAsState()
    val visibleCards by viewModel.visibleWidgets.collectAsState()
    val isWelcomeVisible by remember {
        derivedStateOf { !viewModel.getIsShownWelcome() }
    }

    val welcomeAppSheetState = rememberAppSheetState(isWelcomeVisible)

    LaunchedEffect(Unit) {
        viewModel.initHomeData()
    }

    LaunchedEffect(welcomeAppSheetState.showSheet) {
        if (welcomeAppSheetState.showSheet) {
            viewModel.saveIsShownWelcome(true)
        }
    }
    val sheetManageWidgets = rememberAppSheetState()
    AppBottomSheet(
        state = sheetManageWidgets,
        backgroundColor = RarimeTheme.colors.backgroundPrimary,
        isHeaderEnabled = false,
        fullScreen = false,
    ) {

        ManageWidgetsBottomSheet(onClose = { sheetManageWidgets.hide() })

    }



    HomeScreenContent(
        visibleWidgets = visibleCards,
        userPassportName = passport?.personDetails?.name,
        notificationsCount = notificationsCount,
        innerPaddings = innerPaddings,
        modifier = Modifier.fillMaxSize(),
        navigate = navigate,
        sharedTransitionScope = sharedTransitionScope,
        setVisibilityOfBottomBar = setVisibilityOfBottomBar,
        colorScheme = colorScheme,
        onClick = { sheetManageWidgets.show() },
    )

    AppBottomSheet(
        state = welcomeAppSheetState, isHeaderEnabled = false
    ) {
        WelcomeBottomSheet {
            welcomeAppSheetState.hide()
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    setVisibilityOfBottomBar: (Boolean) -> Unit,
    visibleWidgets: List<WidgetType>,
    userPassportName: String?,
    notificationsCount: Int?,
    colorScheme: AppColorScheme,
    onClick: () -> Unit
) {
    var selectedWidgetType by remember { mutableStateOf<WidgetType?>(null) }
    LaunchedEffect(selectedWidgetType) {
        setVisibilityOfBottomBar(selectedWidgetType == null)
    }

    // Hoist pagerState to remember scroll position across recompositions
    val pagerState = rememberPagerState(pageCount = { visibleWidgets.size })

    Box(modifier = modifier) {
        // Temporarily disable pager scrolling while the expand/collapse animation runs
        var pagerScrollEnabled by remember { mutableStateOf(true) }
        LaunchedEffect(selectedWidgetType) {
            pagerScrollEnabled = false
            delay((ANIMATION_DURATION_MS + 200).toLong())
            pagerScrollEnabled = true
        }

        AnimatedContent(selectedWidgetType) { targetCardType ->
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

                            val widgetType = visibleWidgets[page]
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

                            val collapsedWidgetProps = BaseWidgetProps.Collapsed(
                                onExpand = {
                                    if (pagerScrollEnabled) {
                                        selectedWidgetType = widgetType

                                    }
                                },
                                layoutId = widgetType.layoutId,
                                animatedVisibilityScope = this@AnimatedContent,
                                sharedTransitionScope = sharedTransitionScope
                            )

                            val baseCollapsedModifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                alpha = lerp(0.8f, 1f, 1f - absoluteOffset)
                            }
                            when (widgetType) {
                                WidgetType.EARN -> EarnCollapsedWidget(
                                    collapsedWidgetProps = collapsedWidgetProps,
                                    colorScheme = colorScheme

                                )


                                WidgetType.FREEDOMTOOL -> FreedomtoolCollapsedWidget(
                                    collapsedWidgetProps = collapsedWidgetProps,
                                    modifier = baseCollapsedModifier,
                                )

//                                WidgetType.LIKENESS -> LikenessCollapsedWidget(
//                                    collapsedWidgetProps = collapsedWidgetProps,
//
//                                    modifier = baseCollapsedModifier,
//                                )

                                WidgetType.HIDDEN_PRIZE -> HiddenPrizeCollapsedWidget(
                                    collapsedWidgetProps = collapsedWidgetProps,
                                    modifier = baseCollapsedModifier,
                                    colorScheme = colorScheme
                                )

                                WidgetType.RECOVERY_METHOD -> RecoveryMethodCollapsedWidget(
                                    collapsedWidgetProps = collapsedWidgetProps,
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

                if (pagerState.currentPage == pagerState.pageCount - 1) {
                    ManageWidgetsButton(innerPaddings = innerPaddings, onClick = onClick)
                }

            } else {
                // Expanded: one card is visible on top
                BackHandler {
                    selectedWidgetType = null
                }

                Box(
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    // Common props for every expanded card
                    val expandedCardProps = BaseWidgetProps.Expanded(
                        onCollapse = { selectedWidgetType = null },
                        layoutId = targetCardType.layoutId,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = sharedTransitionScope
                    )

                    when (targetCardType) {
                        WidgetType.FREEDOMTOOL -> FreedomtoolExpandedWidget(
                            expandedWidgetProps = expandedCardProps,
                            innerPaddings = innerPaddings,
                            navigate = navigate
                        )

//                        WidgetType.LIKENESS -> DigitalLikenessExpandedWidget(
//                            expandedWidgetProps = expandedCardProps,
//                            innerPaddings = innerPaddings,
//                            navigate = navigate
//                        )

                        WidgetType.EARN -> EarnExpandedWidget(

                            expandedWidgetProps = expandedCardProps,
                            innerPaddings = innerPaddings,
                            navigate = navigate,

                            )

                        WidgetType.HIDDEN_PRIZE -> HiddenPrizeExpandedWidget(
                            expandedWidgetProps = expandedCardProps,
                            innerPaddings = innerPaddings,
                            navigate = navigate,
                        )

                        WidgetType.RECOVERY_METHOD -> RecoveryMethodExpandedWidget(
                            expandedWidgetProps = expandedCardProps,
                            innerPaddings = innerPaddings,
                            navigate = navigate
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
                visibleWidgets = WidgetType.entries,
                colorScheme = AppColorScheme.SYSTEM,
                onClick = {})
        }
    }
}
