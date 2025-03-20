package com.rarilabs.rarime.modules.home.v2

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.api.points.models.ReferralCodeStatuses
import com.rarilabs.rarime.modules.home.v2.details.ClaimTokensScreen
import com.rarilabs.rarime.modules.home.v2.details.CreateIdentityDetails
import com.rarilabs.rarime.modules.home.v2.details.InviteOthersScreen
import com.rarilabs.rarime.modules.home.v2.details.UnforgettableWalletScreen
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.votes.VotesScreen
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CircledBadgeWithCounter
import com.rarilabs.rarime.ui.components.VerticalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import kotlin.math.abs

enum class CardType {
    YOUR_IDENTITY, INVITE_OTHERS, CLAIM, UNFORGETTABLE_WALLET, FREEDOMTOOL, OTHER
}

data class CardContent(
    val type: CardType,
    val properties: CardProperties,
    val onCardClick: () -> Unit = {},
    val footer: @Composable () -> Unit = {},
)


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    navigate: (String) -> Unit,
    setVisibilityOfBottomBar: (Boolean) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val pointsBalance by viewModel.pointsToken.collectAsState()
    val pointsEvent by viewModel.pointsEventData.collectAsState()

    val currentPointsBalance = pointsBalance?.balanceDetails?.attributes?.amount
    val firstReferralCode = remember(pointsBalance) {
        pointsBalance?.balanceDetails?.attributes?.referral_codes?.first { it.status == ReferralCodeStatuses.ACTIVE.value }?.id
    }


    LaunchedEffect(Unit) {
        try {
            viewModel.initHomeData()
        } catch (e: Exception) {
            ErrorHandler.logError("RewardsEventItemScreen", "Error loading points event", e)
        }
    }

    val innerPaddings by LocalMainViewModel.current.screenInsets.collectAsState()

    HomeScreenContent(
        modifier,
        sharedTransitionScope,
        navigate,
        setVisibilityOfBottomBar,
        innerPaddings,
        pointsEvent = pointsEvent,
        pointsBalance = pointsBalance?.balanceDetails,
        firstReferralCode = firstReferralCode,
        currentPointsBalance = currentPointsBalance
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    navigate: (String) -> Unit,
    setVisibilityOfBottomBar: (Boolean) -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    pointsEvent: PointsEventData?,
    pointsBalance: PointsBalanceData?,
    firstReferralCode: String?,
    currentPointsBalance: Long?
) {

    var selectedPageId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(selectedPageId) {
        setVisibilityOfBottomBar(selectedPageId == null)
    }

    val cardContent = remember {
        mutableListOf(

            CardContent(
                type = CardType.FREEDOMTOOL,
                properties = CardProperties(
                    header = "Freedomtool",
                    subTitle = "Voting",
                    icon = R.drawable.ic_check_unframed,
                    image = R.drawable.freedomtool_bg,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFD5FEC8), Color(0xFF80ed99)
                        )
                    )
                ),
                onCardClick = {},
            ),
            CardContent(
                type = CardType.UNFORGETTABLE_WALLET, properties = CardProperties(
                    header = "An Unforgettable",
                    subTitle = "Wallet",
                    icon = R.drawable.ic_rarime,
                    image = R.drawable.no_more_seed_image,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFCE3FC), Color(0xFFD3D1EF)
                        )
                    )
                ), onCardClick = {}, footer = {}),

            CardContent(
                type = CardType.YOUR_IDENTITY, properties = CardProperties(
                    header = "Your Device",
                    subTitle = "Your Identity",
                    icon = R.drawable.ic_rarime,
                    image = R.drawable.drawable_hand_phone,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9AFE8A), Color(0xFF8AFECC)
                        )
                    )
                ), onCardClick = {}, footer = {
                    Column(
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        Text("Hello World")
                    }
                }),
            CardContent(
                type = CardType.INVITE_OTHERS, properties = CardProperties(
                    header = "Invite",
                    subTitle = "Others",
                    icon = R.drawable.ic_rarimo,
                    image = R.drawable.invite_groupe_image,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFCBE7EC), Color(0xFFF2F8EE)
                        )
                    )
                ), onCardClick = {}, footer = {
                    Column(
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        if (firstReferralCode != null) {
                            Row(
                                Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RarimeTheme.colors.baseWhite)
                                    .padding(vertical = 8.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = firstReferralCode, style = RarimeTheme.typography.h5)
                                VerticalDivider(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .padding(horizontal = 16.dp)
                                )
                                AppIcon(id = R.drawable.ic_copy_simple)
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                        Text(text = "*Nothing leaves thi devise")
                    }
                }),
        ).also {
            if (currentPointsBalance != null && currentPointsBalance != 0L) {
                it.add(
                    CardContent(
                        type = CardType.CLAIM, properties = CardProperties(
                            header = "Claim",
                            subTitle = ("""$currentPointsBalance RMO"""),
                            icon = R.drawable.ic_rarimo,
                            image = R.drawable.claim_rmo_image,
                            backgroundGradient = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFDFFCC4), Color(0xFFF4F3F0)
                                )
                            )
                        ), onCardClick = {}, footer = {})
                )
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { cardContent.size })

    AnimatedContent(selectedPageId, label = "content") {
        if (it == null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(
                        bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp,
                        top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp
                    )
            ) {
                Row(
                    Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            "Hi",
                            style = RarimeTheme.typography.subtitle4,
                            color = RarimeTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Stranger",
                            style = RarimeTheme.typography.subtitle4,
                            color = RarimeTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    CircledBadgeWithCounter(
                        modifier = Modifier.clickable { },
                        iconId = R.drawable.ic_bell,
                        containerSize = 40,
                        containerColor = RarimeTheme.colors.componentPrimary,
                        contentSize = 20,
                        badgeSize = 16,
                        contentColor = RarimeTheme.colors.textPrimary
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))

                        VerticalPager(
                            modifier = Modifier.weight(1f), // <-- Added this
                            state = pagerState,
                            contentPadding = PaddingValues(top = 42.dp, bottom = 95.dp)
                        ) { page ->
                            val pageOffset = remember(
                                pagerState.currentPage, pagerState.currentPageOffsetFraction
                            ) {
                                derivedStateOf {
                                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                }
                            }.value

                            val absoluteOffset = abs(pageOffset).coerceIn(0f, 1f)
                            val targetScale = lerp(0.8f, 1f, 1f - absoluteOffset)


                            key(page) {
                                val scale by animateFloatAsState(
                                    targetValue = targetScale, animationSpec = spring(
                                        dampingRatio = 0.5f, stiffness = 300f
                                    )
                                )

                                HomeCard(
                                    modifier = Modifier.graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        alpha = lerp(0.8f, 1f, 1f - absoluteOffset)
                                    },
                                    cardProperties = cardContent[page].properties,
                                    footer = cardContent[page].footer,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedContentScope = this@AnimatedContent,
                                    id = page,
                                    onCardClick = {
                                        cardContent[page].onCardClick; selectedPageId = page
                                    })
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
            }
        } else {
            BackHandler {
                selectedPageId = null
            }


            when (cardContent[it].type) {
                CardType.YOUR_IDENTITY -> {
                    CreateIdentityDetails(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = this@AnimatedContent,
                        id = it,
                        onBack = { selectedPageId = null },
                        navigate = navigate,
                        innerPaddings = innerPaddings
                    )
                }

                CardType.INVITE_OTHERS -> {
                    InviteOthersScreen(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = this@AnimatedContent,
                        id = it,
                        onBack = { selectedPageId = null },
                        innerPaddings = innerPaddings,
                        pointsBalance = pointsBalance,
                        pointsEvent = pointsEvent,
                    )
                }

                CardType.CLAIM -> {
                    ClaimTokensScreen(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = this@AnimatedContent,
                        id = it,
                        onBack = { selectedPageId = null },
                        innerPaddings = innerPaddings,
                        currentPointsBalance = currentPointsBalance
                            ?: throw IllegalStateException("currentPointsBalance is Null but card was displayed")
                    )
                }

                CardType.UNFORGETTABLE_WALLET -> {
                    UnforgettableWalletScreen(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = this@AnimatedContent,
                        id = it,
                        onBack = { selectedPageId = null },
                        innerPaddings = innerPaddings
                    )
                }

                CardType.FREEDOMTOOL -> {
                    VotesScreen(
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = this@AnimatedContent,
                        id = it,
                        onBack = { selectedPageId = null },
                        navigate = navigate,
                        innerPaddings = innerPaddings
                    )
                }

                CardType.OTHER -> {

                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun HomeScreenPreview() {
    PrevireSharedAnimationProvider { transform, _ ->
        Surface {
            HomeScreenContent(
                sharedTransitionScope = transform,
                navigate = {},
                setVisibilityOfBottomBar = {},
                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
                pointsEvent = null,
                pointsBalance = null,
                firstReferralCode = "",
                currentPointsBalance = 2323.toLong()
            )
        }
    }
}