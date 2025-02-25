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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun InviteOthersScreen(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {

    val bottomSheetState = rememberAppSheetState()

    val properties = DetailsProperties(
        id = 1,
        header = "Invite",
        subTitle = "Others",
        imageId = R.drawable.invite_groupe_image,
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFCBE7EC),
                Color(0xFFF2F8EE)
            )
        )
    )

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
                .zIndex(123f)
                .padding(top = 12.dp)
        }
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
        Spacer(modifier = Modifier.weight(1f))



        AppBottomSheet(
            state = bottomSheetState
        ) {
            InviteOthersContent(
                modifier = Modifier
                    .padding(top = 20.dp, start = 24.dp, end = 24.dp)
                    .fillMaxSize(),
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                properties = properties,
            )
        }

    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun InviteOthersScreenPreview() {

    PrevireSharedAnimationProvider { state, anim ->
        InviteOthersScreen(
            id = 0,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            onBack = {}
        )

    }
}