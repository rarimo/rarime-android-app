package com.rarilabs.rarime.modules.home.v3.ui.collapsed

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v3.CardType
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_DOT_MAP_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardLogo
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCollapsedCard
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FreedomtoolCollapsedCard(
    collapsedCardProps: BaseCardProps.Collapsed,
    modifier: Modifier = Modifier,
) {
    with(collapsedCardProps) {
        with(sharedTransitionScope) {
            BaseCollapsedCard(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) }
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .padding(start = 16.dp, bottom = 8.dp, top = 8. dp)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onExpand()
                    },
                header = {
                    Row(
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(HomeSharedKeys.header(layoutId)),
                                animatedVisibilityScope = animatedVisibilityScope,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                            )
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        BaseCardLogo()
                    }
                },
                footer = {
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
                            title = "RariMe",
                            gradientTitle = "Learn More",
                            caption = "* Nothing leaves this device",
                            titleModifier =
                                Modifier.sharedElement(
                                    state = rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) }
                                ),
                            gradientTitleModifier =
                                Modifier.sharedElement(
                                    state = rememberSharedContentState(
                                        HomeSharedKeys.gradientTitle(
                                            layoutId
                                        )
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) }
                                ),
                            captionModifier =
                                Modifier.sharedElement(
                                    state = rememberSharedContentState(
                                        HomeSharedKeys.caption(
                                            layoutId
                                        )
                                    ),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) }
                                )

                        )
                        Spacer(modifier = Modifier.weight(1f))
                        AppIcon(id = R.drawable.ic_arrow_right_up_line)
                    }
                },
                overlay = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(RarimeTheme.colors.gradient5)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.freedomtool_bg),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(BG_DOT_MAP_HEIGHT.dp)
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun FreedomtoolCollapsedCardPreview() {
    PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
        FreedomtoolCollapsedCard(
            collapsedCardProps = BaseCardProps.Collapsed(
                onExpand = {},
                layoutId = CardType.FREEDOMTOOL.layoutId,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}