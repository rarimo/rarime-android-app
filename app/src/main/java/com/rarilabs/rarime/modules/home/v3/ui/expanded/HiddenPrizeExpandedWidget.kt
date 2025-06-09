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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.manager.Celebrity
import com.rarilabs.rarime.manager.CelebrityStatus
import com.rarilabs.rarime.modules.hiddenPrize.AddScanBottomSheet
import com.rarilabs.rarime.modules.hiddenPrize.HiddenPrizeCamera
import com.rarilabs.rarime.modules.hiddenPrize.HiddenPrizeViewModel
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetTitle
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HiddenPrizeExpandedWidget(
    modifier: Modifier = Modifier,
    expandedWidgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    viewModel: HiddenPrizeViewModel = hiltViewModel(),
    navigate: (String) -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
    }

    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val celebrity by viewModel.celebrity.collectAsState()
    val showFaceScan = rememberAppSheetState()
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    val scope = rememberCoroutineScope()
    val showAddScan = rememberAppSheetState()
    val referralCode by viewModel.referralCode.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val shares by viewModel.shares.collectAsState()
    val colorScheme by viewModel.colorScheme.collectAsState()


    val dayAttemptsCount by remember {
        derivedStateOf {
            userStats?.totalAttemptsCount ?: 0
        }
    }
    val totalAttemptsCount by remember {
        derivedStateOf {
            (userStats?.attemptsLeft ?: 0) + (userStats?.extraAttemptsLeft ?: 0)
        }
    }
    val isAddScanEnabled by remember {
        derivedStateOf {
            shares?.isSocialShare == false || (shares?.referralsCount
                ?: 0) != (shares?.referralsLimit ?: 0)
        }
    }
    AppBottomSheet(state = showFaceScan, shape = RectangleShape, isHeaderEnabled = false) {
        Box(Modifier.fillMaxSize()) {
            HiddenPrizeCamera(
                processML = { viewModel.generateFaceFeatures(it) },
                processZK = { bitmap, features -> viewModel.claimTokens(bitmap, features) },
                downloadProgress = downloadProgress,
                imageLink = celebrity?.image ?: "",
                colorScheme = colorScheme,
                navigate = navigate,
                attemptsLeft = totalAttemptsCount,
                onClose = {
                    showFaceScan.hide()
                })
        }
    }

    val launcherShare = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(), onResult = {
            scope.launch {
                viewModel.addExtraAttempt()
            }
        })


    val launcherInvite = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(), onResult = {})

    AppBottomSheet(state = showAddScan, backgroundColor = RarimeTheme.colors.backgroundSurface1) {
        AddScanBottomSheet(

            isShareEnable = shares?.isSocialShare == false,
            isInviteEnable = (shares?.referralsCount ?: 0) < (shares?.referralsLimit ?: 0),
            onShare = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "I use RareMe")
                }
                launcherShare.launch(Intent.createChooser(intent, "Share via"))

            },
            onInvite = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    val invitationUrl = "${BaseConfig.INVITATION_BASE_URL}/r/${referralCode}"

                    putExtra(Intent.EXTRA_TEXT, invitationUrl)
                }
                launcherInvite.launch(Intent.createChooser(intent, "Invite via"))
            })

    }

    HiddenPrizeExpandedWidgetContent(
        widgetProps = expandedWidgetProps,
        modifier = modifier,
        innerPaddings = innerPaddings,
        onScan = {
            if (!cameraPermissionState.status.isGranted) {
                cameraPermissionState.launchPermissionRequest()
            }
            showFaceScan.show()

        },
        onAddScan = {
            showAddScan.show()
        },
        celebrity = celebrity,
        isAddScanEnabled = isAddScanEnabled,
        attendsCount = totalAttemptsCount,
        dayAttemptsCount = dayAttemptsCount,
        colorScheme = colorScheme


    )


}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HiddenPrizeExpandedWidgetContent(
    modifier: Modifier = Modifier,
    widgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    onScan: () -> Unit,
    onAddScan: () -> Unit,
    celebrity: Celebrity?,
    isAddScanEnabled: Boolean,
    attendsCount: Int,
    dayAttemptsCount: Int,
    colorScheme: AppColorScheme,
) {

    val celebrityStatus = remember(celebrity?.status) {
        celebrity?.getCelebrityStatus()
    }

    with(widgetProps) {
        with(sharedTransitionScope) {
            BaseExpandedWidget(
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


                    when (celebrityStatus) {
                        CelebrityStatus.ACTIVE, null -> {
                            Footer(
                                layoutId = layoutId,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                onAddScan = onAddScan,
                                onScan = onScan,
                                attendsCount = attendsCount,
                                isAddScanEnabled = isAddScanEnabled,
                                dayAttemptsCount = dayAttemptsCount
                            )
                        }

                        CelebrityStatus.COMPLETED -> {

                        }

                        CelebrityStatus.MAINTENANCE -> {

                        }

                    }


                }, body = {
                    Body(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        celebrity = celebrity
                    )
                }, columnModifier = Modifier, background = {
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
    attendsCount: Int = 0,
    isAddScanEnabled: Boolean,
    dayAttemptsCount: Int
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

                    }
                }
                if (attendsCount <= 0 && isAddScanEnabled) {
                    BaseButton(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(RarimeTheme.colors.gradient8),
                        onClick = onAddScan,
                        enabled = true,
                        size = ButtonSize.Large,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = RarimeTheme.colors.baseWhite,
                            disabledContainerColor = RarimeTheme.colors.componentDisabled,
                            disabledContentColor = RarimeTheme.colors.textDisabled
                        ),
                        text = "Bonus scan",
                        leftIcon = R.drawable.ic_flashlight_fill,
                    )
                } else {
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
}


@Composable
private fun Body(
    layoutId: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    celebrity: Celebrity?
) {
    val celebrityStatus = remember(celebrity?.status) {
        celebrity?.getCelebrityStatus()
    }

    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.content(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
                .fillMaxSize()
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .background(
                        RarimeTheme.colors.backgroundPrimary,
                        shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                    )
                    .padding(20.dp)

                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = RarimeTheme.colors.componentPrimary,
                        ),
                        modifier = Modifier.size(width = 156.dp, height = 32.dp)


                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 16.dp, vertical = 6.dp
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
                BaseWidgetTitle(
                    title = "Hidden keys",
                    accentTitle = "Find a face",
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



                when (celebrityStatus) {
                    CelebrityStatus.ACTIVE, null -> {

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            stringResource(R.string.hidden_price_expanded_cart_description),
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textSecondary
                        )


                        Spacer(modifier = Modifier.height(24.dp))
                        if (!celebrity?.hint.isNullOrEmpty()) {
                            TipAlert(
                                text = celebrity!!.hint
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                    }

                    CelebrityStatus.COMPLETED -> {
                        Spacer(
                            modifier = Modifier.size(24.dp)
                        )
                        HorizontalDivider()
                        Spacer(
                            modifier = Modifier.size(24.dp)
                        )
                        WinningFaceCard(
                            imageSrc = celebrity!!.image,
                            placeholderRes = R.drawable.ic_dots_three_outline_rounded,
                            name = celebrity.title,
                            description = celebrity.description,
                            winnerAddress = celebrity.winner,
                            prizeAmount = stringResource(R.string.hidden_prize_prize_pool_value),
                            prizeSymbol = {
                                Image(
                                    painterResource(R.drawable.ic_ethereum),
                                    contentDescription = "ETH"
                                )
                            })
                    }

                    CelebrityStatus.MAINTENANCE -> {

                    }

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
                .fillMaxSize()
                .background(color = RarimeTheme.colors.backgroundPrimary)
        ) {
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
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
                    .clip(RoundedCornerShape(20.dp))
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HiddenPriceExpandedWidgetPreviewLightMode() {
    AppTheme {
        PrevireSharedAnimationProvider { sts, avs ->
            HiddenPrizeExpandedWidgetContent(
                widgetProps = BaseWidgetProps.Expanded(
                    onCollapse = {},
                    layoutId = WidgetType.HIDDEN_PRIZE.layoutId,
                    animatedVisibilityScope = avs,
                    sharedTransitionScope = sts
                ),
                modifier = Modifier.height(820.dp),
                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
                onScan = {},
                onAddScan = {},
                celebrity = null,
                isAddScanEnabled = false,
                attendsCount = 5,
                dayAttemptsCount = 5,
                colorScheme = AppColorScheme.LIGHT
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HiddenPriceExpandedWidgetPreviewDarkMode() {
    AppTheme {
        PrevireSharedAnimationProvider { sts, avs ->
            HiddenPrizeExpandedWidgetContent(
                widgetProps = BaseWidgetProps.Expanded(
                    onCollapse = {},
                    layoutId = WidgetType.HIDDEN_PRIZE.layoutId,
                    animatedVisibilityScope = avs,
                    sharedTransitionScope = sts
                ),
                modifier = Modifier.height(820.dp),
                innerPaddings = mapOf(ScreenInsets.TOP to 0, ScreenInsets.BOTTOM to 0),
                onScan = {},
                onAddScan = {},
                celebrity = null,
                isAddScanEnabled = false,
                attendsCount = 5,
                dayAttemptsCount = 5,
                colorScheme = AppColorScheme.DARK
            )
        }
    }
}