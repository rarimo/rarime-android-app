package com.rarilabs.rarime.modules.home.v3.ui.collapsed

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_HAND_PHONE_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardLogo
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCollapsedCard
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun IdentityCollapsedCard(
    collapsedCardProps: BaseCardProps.Collapsed,
    modifier: Modifier = Modifier
) {
    with(collapsedCardProps) {
        with(sharedTransitionScope) {
            BaseCollapsedCard(
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) }
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onExpand()
                    }
                    .then(modifier),
                header = {
                    Header(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                },
                footer = {
                    Footer(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                },
                background = {
                    Background(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Header(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.header(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .padding(top = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            BaseCardLogo(
                resId = R.drawable.ic_rarime,
                backgroundColor = Color.Transparent,
                size = 55,
                tint = RarimeTheme.colors.baseBlack.copy(alpha = 0.1f)
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Footer(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(start = 22.dp, bottom = 28.dp, end = 30.dp)
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.content(layoutId)),
                    renderInOverlayDuringTransition = false,
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
        ) {
            BaseCardTitle(
                title = "Your Device",
                accentTitle = "Your Identity",
                titleModifier =
                    Modifier.sharedBounds(
                        rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
                accentTitleStyle = RarimeTheme.typography.additional2.copy(color = RarimeTheme.colors.baseBlackOp40),
                accentTitleModifier =
                    Modifier.sharedBounds(
                        rememberSharedContentState(
                            HomeSharedKeys.accentTitle(
                                layoutId
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
            )
            Spacer(modifier = Modifier.weight(1f))
            AppIcon(id = R.drawable.ic_arrow_right_up_line)
        }
        // Ensure the footer has a shared element in both states for smooth open/close animation.
        Row(
            modifier = Modifier.sharedBounds(
                rememberSharedContentState(HomeSharedKeys.footer(layoutId)),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
            )
        ) {
            // Placeholder; no visible UI content.
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Background(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.gradient1)
        ) {
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            val yOffset = if (screenHeight < 700.dp) (-50).dp else (0).dp

            Image(
                painter = painterResource(R.drawable.drawable_hand_phone),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BG_HAND_PHONE_HEIGHT.dp)
                    .offset(y = yOffset)
                    .sharedBounds(
                        rememberSharedContentState(
                            HomeSharedKeys.image(
                                layoutId
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun IdentityCollapsedCardPreview() {
    PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
        IdentityCollapsedCard(
            collapsedCardProps = BaseCardProps.Collapsed(
                onExpand = {},
                layoutId = WidgetType.IDENTITY.layoutId,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
        )
    }
}
