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
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_ClAIM_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.model.getClaimWidgetAccentTitle
import com.rarilabs.rarime.modules.home.v3.model.getClaimWidgetTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetLogo
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCollapsedWidget
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EarnCollapsedWidget(
    collapsedWidgetProps: BaseWidgetProps.Collapsed,
    modifier: Modifier = Modifier,
    colorScheme: AppColorScheme
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

                footer = {
                    Footer(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        title = stringResource(R.string.earn),
                        accentTitle = stringResource(R.string.rmo)
                    )
                },
                background = {
                    Background(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        colorScheme = colorScheme
                    )
                }
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
    title: String,
    accentTitle: String
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
                title = title,
                titleStyle = RarimeTheme.typography.h1.copy(color = RarimeTheme.colors.invertedDark),
                accentTitle = accentTitle,
                titleModifier =
                    Modifier.sharedBounds(
                        rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    ),
                accentTitleStyle = RarimeTheme.typography.additional2.copy(brush = RarimeTheme.colors.gradient13),
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
                caption = stringResource(R.string.earn_collapsed_widget_caption),
                captionStyle = RarimeTheme.typography.body4.copy(color = RarimeTheme.colors.textSecondary),
                captionModifier = Modifier.sharedBounds(
                    rememberSharedContentState(
                        HomeSharedKeys.caption(
                            layoutId
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                ),


            )
            Spacer(modifier = Modifier.weight(1f))
            AppIcon(id = R.drawable.ic_arrow_right_up_line,
                tint = RarimeTheme.colors.invertedDark)
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
        if (isDark) R.drawable.ic_bg_earn_widget_dark
        else R.drawable.ic_bg_earn_widget_light
    }
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
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
fun ClaimCollapsedCardPreviewLight() {
    PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
        EarnCollapsedWidget(
            collapsedWidgetProps = BaseWidgetProps.Collapsed(
                onExpand = {},
                layoutId = WidgetType.EARN.layoutId,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope
            ),
            colorScheme = AppColorScheme.LIGHT,
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun ClaimCollapsedCardPreviewDark() {
    AppTheme {
        PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
            EarnCollapsedWidget(
                collapsedWidgetProps = BaseWidgetProps.Collapsed(
                    onExpand = {},
                    layoutId = WidgetType.EARN.layoutId,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope
                ),
                colorScheme = AppColorScheme.DARK,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            )
        }
    }
}