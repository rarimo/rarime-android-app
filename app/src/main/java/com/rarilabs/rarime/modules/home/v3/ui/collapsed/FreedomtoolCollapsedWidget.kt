package com.rarilabs.rarime.modules.home.v3.ui.collapsed

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_DOT_MAP_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCollapsedWidget
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetLogo
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetTitle
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FreedomtoolCollapsedWidget(
    collapsedWidgetProps: BaseWidgetProps.Collapsed,
    modifier: Modifier = Modifier,
) {
    with(collapsedWidgetProps) {
        with(sharedTransitionScope) {
            BaseCollapsedWidget(
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
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                },
                footer = {
                    Footer(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                },
                background = {
                    Background(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
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
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.header(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .padding(top = 24.dp, start = 24.dp)
                .fillMaxWidth()
        ) {
            BaseWidgetLogo(
                resId = R.drawable.ic_check_unframed,
                backgroundColor = RarimeTheme.colors.componentPrimary,
                size = 40,
                iconSize = 24,
                tint = RarimeTheme.colors.textPrimary
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
            BaseWidgetTitle(
                title = stringResource(R.string.freedomtool_title),
                accentTitle = stringResource(R.string.fredomtool_accent_title),
                titleStyle = RarimeTheme.typography.h1.copy(color = RarimeTheme.colors.invertedDark),
                titleModifier =
                    Modifier.sharedBounds(
                        rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
                accentTitleStyle = RarimeTheme.typography.h1.copy(brush = RarimeTheme.colors.gradient15),
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
            AppIcon(
                id = R.drawable.ic_arrow_right_up_line,
                tint = RarimeTheme.colors.invertedDark,
                size = 24.dp
            )
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
                .background(RarimeTheme.colors.gradient5)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_bg_freedomtool_voting),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BG_DOT_MAP_HEIGHT.dp)
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun FreedomtoolCollapsedWidgetPreview_light() {
    AppTheme {
        PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
            FreedomtoolCollapsedWidget(
                collapsedWidgetProps = BaseWidgetProps.Collapsed(
                    onExpand = {},
                    layoutId = WidgetType.FREEDOMTOOL.layoutId,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp)
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun FreedomtoolCollapsedWidgetPreview_dark() {
    AppTheme {
        PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
            FreedomtoolCollapsedWidget(
                collapsedWidgetProps = BaseWidgetProps.Collapsed(
                    onExpand = {},
                    layoutId = WidgetType.FREEDOMTOOL.layoutId,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(550.dp)
            )
        }
    }
}