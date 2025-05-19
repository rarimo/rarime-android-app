@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.rarilabs.rarime.modules.home.v3.ui.expanded

import android.Manifest
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.Celebrity
import com.rarilabs.rarime.manager.UserStats
import com.rarilabs.rarime.modules.hiddenPrize.HiddenPrizeCamera
import com.rarilabs.rarime.modules.hiddenPrize.HiddenPrizeViewModel
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_HAND_HIDDEN_PRIZE_HEIGHT
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
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.TipAlert
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.AppTheme
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HiddenPrizeExpandedCard(
    modifier: Modifier = Modifier,
    expandedCardProps: BaseCardProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    viewModel: HiddenPrizeViewModel = hiltViewModel()
) {

    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val celebrity by viewModel.celebrity.collectAsState()
    val showFaceScan = rememberAppSheetState()
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    val userData by viewModel.userStats.collectAsState()


    AppBottomSheet(state = showFaceScan, shape = RectangleShape, isHeaderEnabled = false) {
        Box(Modifier.fillMaxSize()) {
            HiddenPrizeCamera(
                processML = { viewModel.generateFaceFeatures(it) },
                processZK = { bitmap, features -> viewModel.claimTokens(bitmap, features) },
                downloadProgress = downloadProgress
            )
        }
    }

    HiddenPrizeExpandedCardContent(
        cardProps = expandedCardProps,
        modifier = modifier,
        innerPaddings = innerPaddings,
        onScan = {
            if (!cameraPermissionState.status.isGranted) {
                cameraPermissionState.launchPermissionRequest()
            } else {
                showFaceScan.show()
            }

        },
        onAddScan = {
            //TODO
        },
        celebrity = celebrity,
        userData = userData
    )


}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HiddenPrizeExpandedCardContent(
    modifier: Modifier = Modifier,
    cardProps: BaseCardProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    onScan: () -> Unit,
    onAddScan: () -> Unit,
    celebrity: Celebrity?,
    userData: UserStats?
) {
    with(cardProps) {
        with(sharedTransitionScope) {
            BaseExpandedCard(
                modifier = modifier
                    .sharedElement(
                        state = rememberSharedContentState(HomeSharedKeys.background(layoutId)),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) })
                    .padding(
                        bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp
                    ), header = {
                    Header(
                        layoutId = layoutId,
                        onCollapse = onCollapse,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        innerPaddings = innerPaddings
                    )
                }, footer = {
                    Footer(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onAddScan = onAddScan,
                        onScan = onScan,
                        attendsCount = if(userData?.totalAttemptsCount != null) userData.totalAttemptsCount else 0,
                        userStats = userData
                    )
                },
                body = {
                    Body(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        tip = celebrity?.hint.toString()
                    )
                },
                background = {
                    Background(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                })
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
                .padding(horizontal = 16.dp)
                .padding(top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onCollapse) {
                AppIcon(id = R.drawable.ic_close, tint = RarimeTheme.colors.textSecondary)
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Footer(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onAddScan: () -> Unit,
    onScan: () -> Unit,
    attendsCount :Int = 0,
    userStats: UserStats?

) {

    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.footer(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .background(RarimeTheme.colors.backgroundPrimary)
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)

        ) {
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Available",
                            style = RarimeTheme.typography.subtitle6,
                            color = RarimeTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        AppIcon(id = R.drawable.ic_info)
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = attendsCount.toString(),
                            color = RarimeTheme.colors.textPrimary,
                            style = TextStyle(
                                brush = RarimeTheme.colors.gradient8,
                                fontSize = RarimeTheme.typography.h4.fontSize,
                                fontWeight = RarimeTheme.typography.h4.fontWeight
                            ),
                        )
                        Text(
                            "/3 daily scans",
                            style = RarimeTheme.typography.body3,
                            color = RarimeTheme.colors.textSecondary
                        )
                    }
                }
                if(attendsCount > 0 && userStats!!.extraAttemptsLeft <= 10 ){
                    PrimaryButton(
                        text = "Scan",
                        onClick = onScan,
                        size = ButtonSize.Large,
                        leftIcon = R.drawable.ic_user_focus
                    )
                }else{
                    BaseButton(
                        modifier = Modifier,
                        onClick = onAddScan,
                        enabled = true,
                        size = ButtonSize.Large,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = RarimeTheme.colors.invertedLight,
                            disabledContainerColor = RarimeTheme.colors.componentDisabled,
                            disabledContentColor = RarimeTheme.colors.textDisabled
                        ),
                        text = "Bonus scan",
                        leftIcon = R.drawable.ic_flashlight_fill,
                    )
                }


            }
        }
    }
}


@Composable
fun Body(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    tip: String?
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.content(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    renderInOverlayDuringTransition = false,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
        ) {

            Spacer(modifier = Modifier.height((BG_HAND_HIDDEN_PRIZE_HEIGHT - 50).dp))
            Box(
                modifier = Modifier
                    .offset(y = (-5).dp)
                    .width(36.dp)
                    .height(5.dp)
                    .background(
                        RarimeTheme.colors.componentPressed,
                        shape = RoundedCornerShape(100.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )
            Column(
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.backgroundPrimary,
                        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                    )
                    .padding(20.dp)
            ) {
                BaseCardTitle(
                    title = "Hidden keys",
                    accentTitle = "Find a face",
                    caption = "Found hidden prize $1000",
                    titleStyle = RarimeTheme.typography.h1.copy(RarimeTheme.colors.textPrimary),
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
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    stringResource(R.string.hidden_price_expanded_cart_description),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )



                if (tip != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    TipAlert(
                        text = tip
                    )
                    Spacer(modifier = Modifier.height(24.dp))
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
            Image(
                painter = painterResource(R.drawable.drawable_hidden_prize_hand),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BG_HAND_HIDDEN_PRIZE_HEIGHT.dp)
                    .offset(y = 80.dp, x = 30.dp)
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

//@OptIn(ExperimentalSharedTransitionApi::class)
//@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
//@Composable
//fun HiddenPriceExpandedCardPreview_LightMode() {
//    AppTheme {
//        PrevireSharedAnimationProvider { sts, avs ->
//            HiddenPrizeExpandedCardContent(
//                cardProps = BaseCardProps.Expanded(
//                    onCollapse = {},
//                    layoutId = CardType.HIDDEN_PRIZE.layoutId,
//                    animatedVisibilityScope = avs,
//                    sharedTransitionScope = sts
//                ),
//                modifier = Modifier.height(820.dp),
//                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
//                onScan = {},
//                onAddScan = {},
//                celebrity = null,
//                userData = null
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalSharedTransitionApi::class)
//@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun HiddenPriceExpandedCardPreview_DarkMode() {
//    AppTheme {
//        PrevireSharedAnimationProvider { sts, avs ->
//            HiddenPrizeExpandedCardContent(
//                cardProps = BaseCardProps.Expanded(
//                    onCollapse = {},
//                    layoutId = CardType.HIDDEN_PRIZE.layoutId,
//                    animatedVisibilityScope = avs,
//                    sharedTransitionScope = sts
//                ),
//                modifier = Modifier.height(820.dp),
//                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
//                onScan = {},
//                onAddScan = {},
//                celebrity = null,
//                userData = null
//            )
//        }
//    }
//}