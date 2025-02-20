package com.rarilabs.rarime.modules.home.v2

import android.util.Log
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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v2.details.CreateIdentityDetails
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CircledBadgeWithCounter
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.components.VerticalDivider
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import kotlin.math.abs

enum class CardType {
    YOUR_IDENTITY,
    INVITE_OTHERS,
    CLAIM,
    UNFORGETTABLE_WALLET,
    FREEDOMTOOL,
    OTHER
}

data class CardContent(
    val type: CardType,
    val properties: CardProperties,
    val onCardClick: () -> Unit = {},
    val footer: @Composable () -> Unit
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    navigate: (String) -> Unit,
) {

    var selectedPageId by remember { mutableStateOf<Int?>(null) }

    val cardContent = remember {
        listOf(
            CardContent(

                type = CardType.YOUR_IDENTITY,
                properties = CardProperties(
                    header = "Your Device",
                    subTitle = "Your Identity",
                    icon = R.drawable.ic_rarime,
                    image = R.drawable.drawable_hand_phone,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9AFE8A), Color(0xFF8AFECC)
                        )
                    )
                ),
                onCardClick = {},
                footer = {
                    Column(
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        Text("Hello World")
                    }
                }
            ),
            CardContent(
                type = CardType.INVITE_OTHERS,
                properties = CardProperties(
                    header = "Invite",
                    subTitle = "Others",
                    icon = R.drawable.ic_rarimo,
                    image = R.drawable.invite_groupe_image,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFCBE7EC), Color(0xFFF2F8EE)
                        )
                    )
                ),
                onCardClick = {},
                footer = {
                    Column(
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RarimeTheme.colors.baseWhite)
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "149250-1596", style = RarimeTheme.typography.h5)
                            VerticalDivider(
                                modifier = Modifier
                                    .height(24.dp)
                                    .padding(horizontal = 16.dp)
                            )
                            AppIcon(id = R.drawable.ic_copy_simple)
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(text = "*Nothing leaves thi devise")
                    }
                }
            ),
            CardContent(
                type = CardType.UNFORGETTABLE_WALLET,
                properties = CardProperties(
                    header = "An Unforgettable",
                    subTitle = "Wallet",
                    icon = R.drawable.ic_rarime,
                    image = R.drawable.no_more_seed_image,
                    backgroundGradient = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF6F3D6),
                            Color(0xFFBCEB3D)
                        )
                    )
                ),
                onCardClick = {},
                footer = {
                    TransparentButton(
                        modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        onClick = {},
                        text = "Join early waitlist"
                    )

                }
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { cardContent.size })

    AnimatedContent(selectedPageId, label = "asd") { it ->
        if (it == null) {
            Column(modifier = modifier.fillMaxSize()) {
                Row(
                    Modifier.padding(start = 20.dp, top = 26.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hi Stranger", style = RarimeTheme.typography.subtitle4)
                    Spacer(modifier = Modifier.weight(1f))
                    CircledBadgeWithCounter(
                        modifier = Modifier.clickable { },
                        iconId = R.drawable.ic_bell,
                        containerSize = 40,
                        containerColor = RarimeTheme.colors.backgroundPrimary,
                        contentSize = 20,
                        badgeSize = 16,
                        contentColor = RarimeTheme.colors.textPrimary
                    )
                }

                Column(modifier = Modifier.padding(start = 22.dp, end = 22.dp)) {
                    VerticalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(top = 63.dp, bottom = 100.dp)
                    ) { page ->
                        val pageOffset =
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                        val absoluteOffset = abs(pageOffset).coerceIn(0f, 1f)
                        val targetScale = lerp(0.8f, 1f, 1f - absoluteOffset)


                        val scale by animateFloatAsState(
                            targetValue = targetScale,
                            animationSpec = spring(
                                dampingRatio = 0.5f,
                                stiffness = 300f
                            )
                        )

                        HomeCard(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                },
                            cardProperties = cardContent[page].properties,
                            footer = cardContent[page].footer,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = this@AnimatedContent,
                            id = page,
                            onCardClick = {
                                Log.i(
                                    "CardClick", page.toString()
                                )
                                cardContent[page].onCardClick; selectedPageId = page
                            }
                        )
                    }

                }
            }
        } else {
            BackHandler {
                Log.i(
                    "CardClick", selectedPageId.toString()
                )
                selectedPageId = null
            }

            CreateIdentityDetails(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = this@AnimatedContent,
                id = it,
                onBack = { selectedPageId = null }
            )
//            when (cardContent[it].type) {
//                CardType.YOUR_IDENTITY -> {
//
//                }
//
//                CardType.INVITE_OTHERS -> {
//
//                }
//
//                CardType.CLAIM -> {
//
//                }
//
//                CardType.UNFORGETTABLE_WALLET -> {
//
//                }
//
//                CardType.FREEDOMTOOL -> {
//
//                }
//
//                CardType.OTHER -> {
//
//                }
//            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun HomeScreenPreview() {
    PrevireSharedAnimationProvider { transform, animated ->
        Surface {
            HomeScreen(
                sharedTransitionScope = transform,
                navigate = {}
            )
        }
    }
}