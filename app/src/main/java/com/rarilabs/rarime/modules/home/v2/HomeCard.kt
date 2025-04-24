package com.rarilabs.rarime.modules.home.v2

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider


data class CardProperties(
    val header: String,
    val headerStyle: TextStyle? = null,
    val subTitle: String,
    val subTitleStyle: TextStyle? = null,
    val caption: String? = null,
    val icon: Int,
    val image: Int,
    val imageModifier: Modifier = Modifier,
    val backgroundGradient: Brush,
)


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
    cardProperties: CardProperties,
    id: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    footer: @Composable () -> Unit,
    header: (@Composable (headerKey: String, subTitleKey: String) -> Unit)? = null,
    image: (@Composable (modifier: Modifier) -> Unit)? = null,
    onCardClick: () -> Unit,
) {
    val boundKey = remember(id) { "$id-bound" }
    val backgroundKey = remember(id) { "background-$id" }
    val imageKey = remember(id) { "image-$id" }
    val headerKey = remember(id) { "header-$id" }
    val subTitleKey = remember(id) { "subTitle-$id" }
    val captionKey = remember(id) { "caption-$id" }

    with(sharedTransitionScope) {
        Card(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(32.dp))
                .animateContentSize(
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 180f)
                )
                .sharedBounds(
                    rememberSharedContentState(key = boundKey),
                    animatedVisibilityScope = animatedContentScope,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(durationMillis = 400, easing = FastOutLinearInEasing)
                    ),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                ), onClick = onCardClick, shape = RoundedCornerShape(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(32.dp))
            ) {

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(cardProperties.backgroundGradient)
                        .sharedBounds(
                            rememberSharedContentState(backgroundKey),
                            animatedVisibilityScope = animatedContentScope
                        )

                )
                if (image != null) {
                    image(cardProperties.imageModifier)
                } else {
                    Image(
                        painter = painterResource(cardProperties.image),
                        contentDescription = null,
                        modifier = cardProperties.imageModifier
                            .matchParentSize()
                            .sharedElement(
                                rememberSharedContentState(imageKey),
                                animatedVisibilityScope = animatedContentScope
                            )
                            .clip(RoundedCornerShape(32.dp)),
                        contentScale = ContentScale.Fit
                    )
                }



                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 24.dp, end = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        AppIcon(
                            id = cardProperties.icon,
                            size = 48.dp,
                            tint = RarimeTheme.colors.baseBlack.copy(alpha = 0.2f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {

                                if (header != null) {
                                    header(headerKey, subTitleKey)
                                } else {
                                    Text(
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(headerKey),
                                            animatedVisibilityScope = animatedContentScope
                                        ),
                                        color = RarimeTheme.colors.textPrimary,
                                        style = cardProperties.headerStyle
                                            ?: RarimeTheme.typography.h2,
                                        text = cardProperties.header
                                    )

                                    Text(
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(subTitleKey),
                                            animatedVisibilityScope = animatedContentScope
                                        ),
                                        color = RarimeTheme.colors.baseBlack.copy(alpha = 0.4f),
                                        style = cardProperties.subTitleStyle
                                            ?: RarimeTheme.typography.additional2,
                                        text = cardProperties.subTitle,
                                    )
                                }


                                if (cardProperties.caption != null) {
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(captionKey),
                                            animatedVisibilityScope = animatedContentScope
                                        ),
                                        color = RarimeTheme.colors.baseBlack.copy(alpha = 0.4f),
                                        style = RarimeTheme.typography.body4,
                                        text = cardProperties.caption,
                                    )
                                }

                                Column(
                                    modifier = Modifier.padding(
                                        start = 24.dp,
                                        end = 24.dp,
                                    )
                                ) {
                                    footer()
                                }
                            }
                            AppIcon(
                                id = R.drawable.ic_arrow_right_up_line,
                                size = 24.dp,
                                modifier = Modifier.align(Alignment.Bottom)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun HomeCardPreview() {

    val prop = CardContent(
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
            ),
            imageModifier = Modifier
        ),
        onCardClick = {},
    )



    PrevireSharedAnimationProvider { state, anim ->
        HomeCard(
            cardProperties = prop.properties,
            onCardClick = {},
            id = 2,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            footer = {
                prop.footer()
            })
    }
}