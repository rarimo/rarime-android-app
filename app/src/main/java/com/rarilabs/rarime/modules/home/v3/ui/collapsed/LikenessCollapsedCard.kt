package com.rarilabs.rarime.modules.home.v3.ui.collapsed

import DigitalLikenessFrame
import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.LikenessRule
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessViewModel
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.model.CardType
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.model.IMG_LIKENESS_HEIGHT
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardLogo
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCollapsedCard
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

@Composable
fun LikenessCollapsedCard(
    collapsedCardProps: BaseCardProps.Collapsed,
    modifier: Modifier = Modifier,
    viewModel: DigitalLikenessViewModel = hiltViewModel()
) {
    val selectedRule by viewModel.selectedRule.collectAsState()
    val isScanned by viewModel.isLivenessScanned.collectAsState()
    val faceImage by viewModel.faceImage.collectAsState()

    LikenessCollapsedCardContent(
        collapsedCardProps = collapsedCardProps,
        modifier = modifier,
        faceImage = faceImage,
        selectedRule = selectedRule,
        isScanned = isScanned,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LikenessCollapsedCardContent(
    collapsedCardProps: BaseCardProps.Collapsed,
    modifier: Modifier = Modifier,
    faceImage: Bitmap?,
    isScanned: Boolean,
    selectedRule: LikenessRule?,
) {
    with(collapsedCardProps) {
        with(sharedTransitionScope) {
            BaseCollapsedCard(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState(
                            HomeSharedKeys.background(
                                layoutId
                            )
                        ),
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
                body = {
                    Body(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        faceImage = faceImage,
                    )
                },
                footer = {
                    Footer(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        isScanned = isScanned,
                        selectedRule = selectedRule
                    )
                },
                background = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(RarimeTheme.colors.gradient7)
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
                resId = R.drawable.ic_body_scan_fill,
                backgroundColor = Color.Transparent,
                size = 50,
                tint = RarimeTheme.colors.baseBlack.copy(alpha = 0.1f)
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Body(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    faceImage: Bitmap?,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val yOffset = if (screenHeight < 700.dp) (-50).dp else (0).dp

    with(sharedTransitionScope) {
        if (faceImage != null) {
            DigitalLikenessFrame(
                faceImage = faceImage.asImageBitmap(),
                frameRes = R.drawable.drawable_likeness_face_bg,
                modifier = Modifier
                    .fillMaxWidth()
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
                    ),
                frameSize = 270.dp,
                faceSize = 255.dp
            )
            Spacer(modifier = Modifier.height(10.dp))
        } else {
            Image(
                painter = painterResource(R.drawable.drawable_digital_likeness),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IMG_LIKENESS_HEIGHT.dp)
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
@Composable
private fun Footer(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isScanned: Boolean,
    selectedRule: LikenessRule?
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
            LikenessTitle(
                layoutId = layoutId,
                isScanned = isScanned,
                selectedRule = selectedRule,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
            )
            Spacer(modifier = Modifier.weight(1f))
            AppIcon(id = R.drawable.ic_arrow_right_up_line)
        }
        // Ensure the footer has a shared element in both states for smooth open/close animation.
        Column(
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

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LikenessTitle(
    modifier: Modifier = Modifier,
    layoutId: Int,
    isScanned: Boolean,
    selectedRule: LikenessRule?,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    with(sharedTransitionScope) {
        Column {
            if (isScanned) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    BaseCardTitle(
                        title = "My rule:",
                        accentTitle = when (selectedRule) {
                            LikenessRule.ALWAYS_ALLOW -> "Use my likeness and pay me"
                            LikenessRule.REJECT -> "Don't use \nmy face data"
                            LikenessRule.ASK_EVERYTIME -> "Ask me first"
                            else -> ""
                        },
                        titleStyle = RarimeTheme.typography.h5.copy(
                            color = RarimeTheme.colors.baseBlack
                        ),
                        accentTitleStyle = RarimeTheme.typography.additional2.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF655CA4), Color(0xFF7E66B2)
                                ), start = Offset(0f, 0f), end = Offset(100f, 0f)
                            )
                        ),
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
                        caption = "First human-AI Contract",
                        captionModifier =
                            Modifier.sharedBounds(
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
                }
            } else {
                BaseCardTitle(
                    title = "Digital likeness",
                    accentTitle = "Set a rule",
                    titleStyle = RarimeTheme.typography.h2.copy(
                        color = RarimeTheme.colors.baseBlack
                    ),
                    accentTitleStyle = RarimeTheme.typography.additional2.copy(color = RarimeTheme.colors.baseBlackOp40),
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
                    caption = "First human-AI Contract",
                    captionModifier =
                        Modifier.sharedBounds(
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
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun LikenessCollapsedCardPreview() {
    PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
        LikenessCollapsedCardContent(
            collapsedCardProps = BaseCardProps.Collapsed(
                onExpand = {},
                layoutId = CardType.LIKENESS.layoutId,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope
            ),
            modifier = Modifier.height(450.dp),
            faceImage = null,
            isScanned = false,
            selectedRule = null
        )
    }
}
