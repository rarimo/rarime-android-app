@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.rarilabs.rarime.modules.home.v3.ui.expanded

import android.Manifest
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.hiddenPrize.HiddenPrizeCamera
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.model.CardType
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedCard
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.components.VerticalDivider
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HiddenPrizeExpandedCard(
    modifier: Modifier = Modifier,
    expandedCardProps: BaseCardProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
) {


    val showQrScan = rememberAppSheetState()
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )


    AppBottomSheet(state = showQrScan) {

        HiddenPrizeCamera {

        }

    }





    HiddenPrizeExpandedCardContent(
        cardProps = expandedCardProps,
        modifier = modifier,
        innerPaddings = innerPaddings,
        onScan = {
            if (!cameraPermissionState.status.isGranted){
                cameraPermissionState.launchPermissionRequest()
            } else{
                showQrScan.show()
            }

        },
        onAddScan = {
            //TODO
        }
    )

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HiddenPrizeExpandedCardContent(
    modifier: Modifier = Modifier,
    cardProps: BaseCardProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    onScan: () -> Unit,
    onAddScan: () -> Unit
) {
    with(cardProps) {
        with(sharedTransitionScope) {
            BaseExpandedCard(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) }
                    )
                    .padding(
                        top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp,
                        bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp
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
                footer = {
                    Footer(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onAddScan = onAddScan,
                        onScan = onScan
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
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onCollapse) {
                AppIcon(id = R.drawable.ic_close)
            }
        }
    }
}


@Composable
private fun Footer(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onAddScan: () -> Unit,
    onScan: () -> Unit
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

            BaseCardTitle(
                title = "Hidden Prize",
                accentTitle = "Scan",
                caption = "Found hidden prize $1000",
                titleStyle = RarimeTheme.typography.h1.copy(color = RarimeTheme.colors.baseBlack),
                accentTitleStyle = RarimeTheme.typography.additional1.copy(brush = RarimeTheme.colors.gradient8),
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
                captionModifier = Modifier.sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.caption(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
            )
            Text(
                stringResource(R.string.hidden_price_expanded_cart_description),
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.baseBlackOp50
            )
            HorizontalDivider()


            Column(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .sharedBounds(
                        rememberSharedContentState(HomeSharedKeys.footer(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        renderInOverlayDuringTransition = false,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                BaseButton(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RarimeTheme.colors.baseBlack,
                        contentColor = RarimeTheme.colors.baseWhite,
                        disabledContainerColor = RarimeTheme.colors.componentDisabled,
                        disabledContentColor = RarimeTheme.colors.textDisabled
                    ),
                    size = ButtonSize.Large, onClick = onScan

                ) {
                    AppIcon(id = R.drawable.ic_user_focus)
                    Text(text = "Scan a Celebrity")
                    VerticalDivider(modifier = Modifier.height(24.dp))
                    Text("3/3")
                }

                Spacer(modifier = Modifier.height(8.dp))
                TransparentButton(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    size = ButtonSize.Large,
                    onClick = onAddScan,
                ) {

                    AppIcon(
                        id = R.drawable.ic_flashlight_fill,
                        tint = Color(0xFF9D4EDD)//TODO: Sync with theme
                    )
                    Text(
                        text = "Get a additional Scan",
                        color = RarimeTheme.colors.baseBlack
                    )
                }

            }


        }

    }


}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Background(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RarimeTheme.colors.gradient9)
        ) {

        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun HiddenPriceExpandedCardPreview() {
    PrevireSharedAnimationProvider { sts, avs ->
        HiddenPrizeExpandedCardContent(
            cardProps = BaseCardProps.Expanded(
                onCollapse = {},
                layoutId = CardType.HIDDEN_PRIZE.layoutId,
                animatedVisibilityScope = avs,
                sharedTransitionScope = sts
            ),
            modifier = Modifier.fillMaxSize(),
            innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
            onScan = {},
            onAddScan = {}

        )
    }
}