package com.rarilabs.rarime.modules.home.v3.ui.expanded

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_HAND_PHONE_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetTitle
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import com.rarilabs.rarime.util.Screen

@Composable
fun IdentityExpandedWidget(
    modifier: Modifier = Modifier,
    expandedWidgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    setVisibilityOfBottomBar: (Boolean) -> Unit
) {
    IdentityExpandedWidgetContent(
        modifier = modifier,
        widgetProps = expandedWidgetProps,
        innerPaddings = innerPaddings,
        navigate = navigate,
        setVisibilityOfBottomBar = setVisibilityOfBottomBar
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun IdentityExpandedWidgetContent(
    modifier: Modifier = Modifier,
    widgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    setVisibilityOfBottomBar: (Boolean) -> Unit
) {
    with(widgetProps) {
        with(sharedTransitionScope) {
            BaseExpandedWidget(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) }
                    ),
                header = {
                    Header(
                        layoutId = layoutId,
                        onCollapse = onCollapse,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        innerPaddings = innerPaddings
                    )
                },
                body = {
                    Body(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        innerPaddings = innerPaddings,
                    )
                },
                footer = {
                    Footer(
                        onNavigate = {
                            setVisibilityOfBottomBar(true)
                            navigate(Screen.Main.Identity.route)
                        },
                        layoutId = layoutId,
                        innerPaddings = innerPaddings,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope

                    )
                },
                background = {
                    Background(
                        layoutId = layoutId,
                        innerPaddings = innerPaddings,
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
    onCollapse: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    innerPaddings: Map<ScreenInsets, Number>,
) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.header(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .fillMaxWidth()
                .padding(
                    top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp,
                    bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp
                )
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onCollapse) {
                AppIcon(id = R.drawable.ic_close)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Body(
    layoutId: Int,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
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
            Spacer(Modifier.height((BG_HAND_PHONE_HEIGHT - 100).dp))
            BaseWidgetTitle(
                title = "Your Device",
                accentTitle = "Your Identity",
                titleStyle = RarimeTheme.typography.h1.copy(color = RarimeTheme.colors.baseBlack),
                accentTitleStyle = RarimeTheme.typography.additional1.copy(color = RarimeTheme.colors.baseBlackOp40),
                titleModifier = Modifier.sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                ),
                accentTitleModifier = Modifier.sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.accentTitle(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                ),
            )
            Text(
                text = "This app is where you privately store your digital identities, enabling you to go incognito across the web.",
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.baseBlackOp50
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Footer(
    layoutId: Int,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: () -> Unit
) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .padding(
                    bottom = (innerPaddings[ScreenInsets.BOTTOM]!!.toInt() + 10).dp,
                    top = 10.dp
                )
                .padding(horizontal = 16.dp)
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.footer(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
        ) {
            TransparentButton(
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                text = "Let’s Start",
                onClick = onNavigate
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
    innerPaddings: Map<ScreenInsets, Number>,
) {
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.gradient1)
        ) {
            Image(
                painter = painterResource(R.drawable.drawable_hand_phone),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BG_HAND_PHONE_HEIGHT.dp)
                    .offset(y = 40.dp)
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
@Preview(showBackground = true)
@Composable
fun IdentityExpandedWidgetPreview() {
    PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
        IdentityExpandedWidgetContent(
            widgetProps = BaseWidgetProps.Expanded(
                layoutId = WidgetType.IDENTITY.layoutId,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
                onCollapse = {}
            ),
            innerPaddings = mapOf(ScreenInsets.TOP to 40, ScreenInsets.BOTTOM to 20),
            navigate = {},
            setVisibilityOfBottomBar = {}
        )
    }
}
