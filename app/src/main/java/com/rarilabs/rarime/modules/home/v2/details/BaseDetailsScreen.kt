package com.rarilabs.rarime.modules.home.v2.details

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
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
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,

    ) {


    Column(
        with(sharedTransitionScope) {
            modifier
                .background(properties.backgroundGradient)
                .sharedBounds(
                    rememberSharedContentState(key = "${properties.id}-bound"),
                    animatedVisibilityScope = animatedContentScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )

                .fillMaxSize()
                .padding(top = 12.dp)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                with(sharedTransitionScope) {
                    Text(
                        style = RarimeTheme.typography.h5,
                        text = properties.header,
                        modifier = Modifier.sharedElement(
                            state = rememberSharedContentState(
                                "header-${properties.id}"
                            ), animatedVisibilityScope = animatedContentScope
                        )
                    )
                }
                with(sharedTransitionScope) {
                    Text(
                        style = RarimeTheme.typography.h4,
                        text = properties.subTitle,
                        modifier = Modifier
                            .sharedElement(
                                state = rememberSharedContentState(
                                    "subTitle-${properties.id}"
                                ), animatedVisibilityScope = animatedContentScope
                            ),
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .height(72.dp)
                    .background(RarimeTheme.colors.componentPrimary, CircleShape)
                    .clickable { onBack.invoke() }
            ) {
                AppIcon(
                    id = R.drawable.ic_arrow_left,
                    size = 32.dp,
                    tint = RarimeTheme.colors.textPrimary.also { it.alpha },
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        with(sharedTransitionScope) {
            Image(
                painter = painterResource(properties.imageId),
                contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(
                            "image-${properties.id}"
                        ), animatedVisibilityScope = animatedContentScope
                    )
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        footer()
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
            footer = {},
            onBack = {}
        )
    }
}