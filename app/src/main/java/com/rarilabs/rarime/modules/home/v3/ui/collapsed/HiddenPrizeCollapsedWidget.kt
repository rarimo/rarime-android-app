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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
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
fun HiddenPrizeCollapsedWidget(
    collapsedWidgetProps: BaseWidgetProps.Collapsed,
    modifier: Modifier = Modifier,
    colorScheme: AppColorScheme
) {
    with(collapsedWidgetProps) {
        with(sharedTransitionScope) {
            BaseCollapsedWidget(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(RarimeTheme.colors.backgroundPrimary)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .sharedElement(
                        state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) })
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onExpand()
                    }
                    .then(modifier), header = {
                    Header(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }, footer = {
                    Footer(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }, background = {
                    Background(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        colorScheme = colorScheme
                    )
                })
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
            horizontalArrangement = Arrangement.Start, modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.header(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .padding(top = 20.dp, start = 20.dp)
                .fillMaxWidth()
        ) {
            BaseWidgetLogo(
                resId = R.drawable.ic_rarime,
                backgroundColor = RarimeTheme.colors.componentPrimary,
                size = 40,
                tint = RarimeTheme.colors.textSecondary.copy(alpha = 1f),
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
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .padding(start = 22.dp, bottom = 32.dp, end = 30.dp)
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.content(layoutId)),
                    renderInOverlayDuringTransition = false,
                    animatedVisibilityScope = animatedVisibilityScope,
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .fillMaxSize()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = RarimeTheme.colors.componentPrimary,
                ),
                modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(HomeSharedKeys.label(layoutId)),
                        renderInOverlayDuringTransition = true,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )


            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 6.dp
                    )
                ) {
                    Text(
                        "Prize-pool: ",
                        style = RarimeTheme.typography.subtitle6.copy(color = RarimeTheme.colors.textPrimary)
                    )
                    Text(
                        text = stringResource(R.string.hidden_prize_prize_pool_value), //TODO Maybe give this from backend in future
                        style = RarimeTheme.typography.h6.copy(color = RarimeTheme.colors.textPrimary),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Image(
                        painterResource(R.drawable.ic_ethereum),
                        contentDescription = "ETH",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                BaseWidgetTitle(
                    title = "Hidden keys",
                    accentTitle = "Find a face",
                    titleModifier = Modifier.sharedBounds(
                        rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
                    titleStyle = RarimeTheme.typography.h1.copy(RarimeTheme.colors.textPrimary),
                    accentTitleStyle = RarimeTheme.typography.additional1.copy(brush = RarimeTheme.colors.gradient8),
                    accentTitleModifier = Modifier.sharedBounds(
                        rememberSharedContentState(
                            HomeSharedKeys.accentTitle(
                                layoutId
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
                    captionStyle = RarimeTheme.typography.body4.copy(RarimeTheme.colors.textSecondary),
                    captionModifier = Modifier.sharedBounds(
                        rememberSharedContentState(
                            HomeSharedKeys.caption(
                                layoutId
                            )
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
                )

                Spacer(modifier = Modifier.weight(1f))
                AppIcon(
                    id = R.drawable.ic_arrow_right_up_line,
                    modifier = Modifier.align(Alignment.Bottom),
                    tint = RarimeTheme.colors.textPrimary,
                    size = 24.dp
                )
            }
        }

    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Background(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    colorScheme: AppColorScheme
) {
    val isDark = when (colorScheme) {
        AppColorScheme.SYSTEM -> isSystemInDarkTheme()
        AppColorScheme.DARK -> true
        AppColorScheme.LIGHT -> false
    }

    val backgroundRes = remember(isDark) {
        if (isDark) R.drawable.ic_bg_hidden_prize_dark
        else R.drawable.ic_bg_hidden_prize_light
    }
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .background(color = RarimeTheme.colors.backgroundPrimary)
                .clip(RoundedCornerShape(40.dp))
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedBounds(
                        rememberSharedContentState(
                            HomeSharedKeys.image(
                                layoutId
                            )
                        ),
                        clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(40.dp)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
                    .clip(RoundedCornerShape(40.dp))


            )


        }

    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HiddenPrizeCollapsedWidgetPreview_DarkMode() {
    AppTheme {
        PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
            HiddenPrizeCollapsedWidget(
                colorScheme = AppColorScheme.DARK,
                collapsedWidgetProps = BaseWidgetProps.Collapsed(
                    onExpand = {},
                    layoutId = WidgetType.HIDDEN_PRIZE.layoutId,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),

                )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HiddenPrizeCollapsedWidgetPreview_LightMode() {
    AppTheme {
        PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
            HiddenPrizeCollapsedWidget(
                colorScheme = AppColorScheme.LIGHT,
                collapsedWidgetProps = BaseWidgetProps.Collapsed(
                    onExpand = {},
                    layoutId = WidgetType.HIDDEN_PRIZE.layoutId,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope
                ), modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            )
        }
    }
}