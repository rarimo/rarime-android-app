package com.rarilabs.rarime.modules.home.v2.details

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import kotlinx.coroutines.delay

data class DetailsProperties(
    val id: Int,
    val header: String,
    val subTitle: String,
    val subTitleStyle: TextStyle? = null,
    val caption: String? = null,
    val modifier: Modifier = Modifier,
    val backgroundGradient: Brush,
    val imageId: Int,
    val imageModifier: Modifier = Modifier
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BaseDetailsScreen(
    modifier: Modifier = Modifier,
    properties: DetailsProperties,
    header: (@Composable (headerKey: String, subTitleKey: String) -> Unit)? = null,
    body: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    onBack: () -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val footerVisible = remember { mutableStateOf(false) }

    val boundKey = remember(properties.id) { "${properties.id}-bound" }
    val backgroundKey = remember(properties.id) { "background-${properties.id}" }

    val imageKey = remember(properties.id) { "image-${properties.id}" }
    val headerKey = remember(properties.id) { "header-${properties.id}" }
    val subTitleKey = remember(properties.id) { "subTitle-${properties.id}" }
    val captionKey = remember(properties.id) { "caption-${properties.id}" }

    LaunchedEffect(Unit) {
        delay(200)
        footerVisible.value = true
    }

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(properties.backgroundGradient)
                .zIndex(1f)
                .then(modifier)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .sharedElement(
                            state = rememberSharedContentState(backgroundKey),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .sharedBounds(
                            rememberSharedContentState(key = boundKey),
                            animatedVisibilityScope = animatedContentScope,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(
                                top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp + 12.dp,
                                start = 24.dp, end = 12.dp
                            )
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(40.dp)
                                .clip(CircleShape)
                                .height(40.dp)
                                .background(RarimeTheme.colors.componentPrimary, CircleShape)
                                .clickable { onBack.invoke() }) {
                            AppIcon(
                                id = R.drawable.ic_close,
                                size = 20.dp,
                                tint = RarimeTheme.colors.baseBlack,
                                modifier = Modifier
                                    .background(RarimeTheme.colors.baseBlack.copy(alpha = 0.05f))
                                    .padding(10.dp)
                            )
                        }
                    }

                    // Default body part
                    // Keeping image outside body container
                    Image(
                        painter = painterResource(properties.imageId),
                        contentDescription = null,
                        modifier = properties.imageModifier
                            .sharedElement(
                                state = rememberSharedContentState(imageKey),
                                animatedVisibilityScope = animatedContentScope
                            )
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                    ) {

                        if (header != null) {
                            header(headerKey, subTitleKey)
                        } else {
                            Text(
                                style = RarimeTheme.typography.h1,
                                color = RarimeTheme.colors.baseBlack,
                                text = properties.header,
                                modifier = Modifier.sharedBounds(
                                    rememberSharedContentState(
                                        headerKey
                                    ), animatedVisibilityScope = animatedContentScope
                                )
                            )

                            Text(
                                style = properties.subTitleStyle ?: RarimeTheme.typography.additional1,
                                text = properties.subTitle,
                                color = RarimeTheme.colors.baseBlack.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .sharedBounds(
                                        rememberSharedContentState(
                                            subTitleKey
                                        ), animatedVisibilityScope = animatedContentScope
                                    )
                                    .skipToLookaheadSize(),
                            )
                        }


                        if (properties.caption != null) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                style = RarimeTheme.typography.body4,
                                text = properties.caption,
                                color = RarimeTheme.colors.baseBlack.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .sharedBounds(
                                        rememberSharedContentState(
                                            captionKey
                                        ), animatedVisibilityScope = animatedContentScope
                                    )
                                    .skipToLookaheadSize(),
                            )
                            Spacer(Modifier.height(24.dp))
                        }

                        if (body != null) body()
                    }
                }

                // Fixed footer
                // Appears with delay
                if (footer != null) {
                    AnimatedVisibility(
                        visible = footerVisible.value,
                        enter = slideInVertically(
                            animationSpec = tween(durationMillis = 400),
                            initialOffsetY = { it }
                        ) + fadeIn(tween(400)),
                        exit = fadeOut(tween(100))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    start = 24.dp,
                                    end = 24.dp,
                                    bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp + 24.dp,
                                )
                        ) {
                            footer()
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
private fun BaseDetailsScreenPreview() {


    val properties = DetailsProperties(
        id = 1,
        header = "An Unforgettable",
        modifier = Modifier.fillMaxSize(),
        subTitle = "Wallet",
        imageId = R.drawable.no_more_seed_image,
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFF6F3D6), Color(0xFFBCEB3D)
            )
        )
    )

    PrevireSharedAnimationProvider { state, anim ->
        BaseDetailsScreen(
            properties = properties,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            body = {
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    text = "Say goodbye to seed phrases! ZK Face Wallet leverages cutting-edge zero-knowledge (ZK) cryptography and biometric authentication to give you a seamless, secure, and self-sovereign crypto experience."
                )
            },
            //  Check footer in interactive mode
            footer = {
                Spacer(modifier = Modifier.height(24.dp))
                TransparentButton(
                    size = ButtonSize.Large,
                    modifier = Modifier.fillMaxWidth(),
                    text = "Join Waitlist",
                    onClick = {})

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = RarimeTheme.typography.body5,
                    color = RarimeTheme.colors.textSecondary,
                    text = "49,421 other already joined",
                )
                Spacer(modifier = Modifier.height(16.dp))
            },
            onBack = {},
            innerPaddings = mapOf(ScreenInsets.TOP to 23, ScreenInsets.BOTTOM to 12)
        )
    }
}