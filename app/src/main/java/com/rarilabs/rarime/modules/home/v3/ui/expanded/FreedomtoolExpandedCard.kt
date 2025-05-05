package com.rarilabs.rarime.modules.home.v3.ui.expanded

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_DOT_MAP_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedCard
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FreedomtoolExpandedCard(
    expandedCardProps: BaseCardProps.Expanded,
    modifier: Modifier = Modifier,
) {
    val systemBarsPadding = WindowInsets.systemBars
        .asPaddingValues()

    with(expandedCardProps) {
        with(sharedTransitionScope) {
            BaseExpandedCard(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = ANIMATION_DURATION_MS)
                        },
                    )
                ,
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
                            .padding(vertical = systemBarsPadding.calculateBottomPadding())
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onCollapse) {
                            AppIcon(id = R.drawable.ic_close)
                        }
                    }
                },
                body = {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .sharedBounds(
                                rememberSharedContentState(HomeSharedKeys.content(layoutId)),
                                animatedVisibilityScope = animatedVisibilityScope,
                                renderInOverlayDuringTransition = false,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                            )
                    ) {
                        Spacer(Modifier.height((BG_DOT_MAP_HEIGHT - 150).dp))
                        BaseCardTitle(
                            title = "RariMe",
                            gradientTitle = "Learn More",
                            gradient = RarimeTheme.colors.gradient6,
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
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "An identification and privacy solution that revolutionizes polling, surveying and election processes",
                            color = RarimeTheme.colors.textSecondary
                        )
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