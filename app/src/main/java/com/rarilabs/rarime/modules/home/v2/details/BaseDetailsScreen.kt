package com.rarilabs.rarime.modules.home.v2.details

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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


data class DetailsProperties(
    val id: Int,
    val header: String,
    val subTitle: String,
    val backgroundGradient: Brush,
    val imageId: Int,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BaseDetailsScreen(
    modifier: Modifier = Modifier,
    properties: DetailsProperties,
    footer: @Composable () -> Unit,
    onBack: () -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val boundKey = remember(properties.id) { "${properties.id}-bound" }
    val backgroundKey = remember(properties.id) { "background-${properties.id}" }
    val imageKey = remember(properties.id) { "image-${properties.id}" }
    val headerKey = remember(properties.id) { "header-${properties.id}" }
    val subTitleKey = remember(properties.id) { "subTitle-${properties.id}" }

    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .background(properties.backgroundGradient)
                .sharedElement(
                    state = rememberSharedContentState(
                        backgroundKey
                    ), animatedVisibilityScope = animatedContentScope
                )
                .sharedBounds(
                    rememberSharedContentState(key = boundKey),
                    animatedVisibilityScope = animatedContentScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
                .fillMaxSize()
                .zIndex(123f)
                .padding(
                    top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp + 12.dp,
                    bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp
                )
                .then(modifier)

        ) {
            Row(
                modifier = Modifier
                    .padding(start = 24.dp, end = 12.dp)
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
                        .clickable { onBack.invoke() }
                ) {
                    AppIcon(
                        id = R.drawable.ic_close,
                        size = 20.dp,
                        tint = RarimeTheme.colors.textPrimary.also { it.alpha },
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(properties.imageId),
                contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(
                            imageKey
                        ), animatedVisibilityScope = animatedContentScope
                    )
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )


            Spacer(modifier = Modifier.weight(1f))

            // TODO: Recheck scroll for voting screen
            Column(
                modifier = Modifier
                    .padding(
                        top = 20.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    style = RarimeTheme.typography.h1,
                    color = RarimeTheme.colors.textPrimary,
                    text = properties.header,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(
                            headerKey
                        ), animatedVisibilityScope = animatedContentScope
                    )
                )

                Text(
                    style = RarimeTheme.typography.additional1,
                    text = properties.subTitle,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(
                                subTitleKey
                            ), animatedVisibilityScope = animatedContentScope
                        )
                        .skipToLookaheadSize(),
                )

                Spacer(modifier = Modifier.height(24.dp))
                footer()
                Spacer(modifier = Modifier.height(24.dp))
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
        subTitle = "Wallet",
        imageId = R.drawable.no_more_seed_image,
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFF6F3D6),
                Color(0xFFBCEB3D)
            )
        )
    )

    PrevireSharedAnimationProvider { state, anim ->
        BaseDetailsScreen(
            properties = properties,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            footer = {
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    text = "Say goodbye to seed phrases! ZK Face Wallet leverages cutting-edge zero-knowledge (ZK) cryptography and biometric authentication to give you a seamless, secure, and self-sovereign crypto experience."
                )

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