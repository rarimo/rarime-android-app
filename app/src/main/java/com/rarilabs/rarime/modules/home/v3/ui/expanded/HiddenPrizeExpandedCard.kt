@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.rarilabs.rarime.modules.home.v3.ui.expanded

import WinningFaceCard
import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.hiddenPrize.AddScanBottomSheet
import com.rarilabs.rarime.api.hiddenPrize.models.CelebrityStatus
import com.rarilabs.rarime.modules.hiddenPrize.HiddenPrizeCamera
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_HAND_HIDEN_PRIZE_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.model.CardType
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedCard
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.TipAlert
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

    val inviteLink: String = "invite link" //TODO add in viewModel


    
    val showFaceScan = rememberAppSheetState()

    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    val showAddScan = rememberAppSheetState()

    // TODO: Add isWrong & isSuccess flags
    AppBottomSheet(state = showFaceScan, shape = RectangleShape, isHeaderEnabled = false) {
        Box(Modifier.fillMaxSize()) {
//            TODO: Put blurred image here
//            Image(
//                painter = painterResource(R.drawable.drawable_digital_likeness),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .blur(20.dp)
//            )
//
//            HiddenPrizeSuccessScreen(
//                prizeAmount = 0.0f,
//                prizeSymbol = {
//                    Image(painterResource(R.drawable.ic_ethereum), contentDescription = "ETH")
//                },
//                onViewWallet = {},
//                onShareWallet = {}
//            )
//            HiddenPrizeWrongScreen(
//                attemptsLeft = 2,
//                tip = "Tip",
//                onRetry = {}
//            )
            HiddenPrizeCamera(
                onNext = {
                    // TODO: Add on next
                }
            )
        }
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(), onResult = {})

    AppBottomSheet(state = showAddScan) {
        AddScanBottomSheet(onShare = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "I use RareMe")
            }
            launcher.launch(Intent.createChooser(intent, "Share via"))
        }, onInvite = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, inviteLink)
            }
            launcher.launch(Intent.createChooser(intent, "Invite via"))
        })

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
            showAddScan.show()
        })

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
                        onScan = onScan
                    )
                },

                body = {
                    Body(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                        // TODO: Add props
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
                AppIcon(id = R.drawable.ic_close)
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
    onScan: () -> Unit
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
                            "3",
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

                PrimaryButton(
                    text = "Scan",
                    onClick = onScan,
                    size = ButtonSize.Large,
                    leftIcon = R.drawable.ic_user_focus
                )
            }
        }
    }
}

@Composable
fun Body(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    celebrityStatus: CelebrityStatus = CelebrityStatus.ACTIVE
) {
    with(sharedTransitionScope) {
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
            Spacer(modifier = Modifier.height(BG_HAND_HIDEN_PRIZE_HEIGHT.dp))

            BaseCardTitle(
                title = "Hidden prize",
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
            Spacer(modifier = Modifier.height(12.dp))

            when (celebrityStatus) {
                CelebrityStatus.ACTIVE -> {
                    Text(
                        stringResource(R.string.hidden_price_expanded_cart_description),
                        style = RarimeTheme.typography.body3,
                        color = RarimeTheme.colors.baseBlackOp50
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TipAlert(
                        text = "I think there's something as light as ether\n" + "in that face..."
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                CelebrityStatus.COMPLETED -> {
                    // TODO: Replace with real content
                    WinningFaceCard(
                        imageSrc = "https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg",
                        placeholderRes = R.drawable.drawable_digital_likeness,
                        name = "Vitalik Baturin",
                        description = "Ethereum co-founder",
                        winnerAddress = "0x00000...0000",
                        prizeAmount = 0.3F,
                        prizeSymbol = {
                            Image(
                                painterResource(R.drawable.ic_ethereum),
                                contentDescription = "ETH"
                            )
                        },
                    )
                }

                CelebrityStatus.MAINTENANCE -> {}
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
                    .height(BG_HAND_HIDEN_PRIZE_HEIGHT.dp)
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