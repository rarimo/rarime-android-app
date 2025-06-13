package com.rarilabs.rarime.modules.home.v3.ui.expanded

import android.Manifest
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppColorScheme
import com.rarilabs.rarime.manager.LikenessRule
import com.rarilabs.rarime.manager.LivenessProcessingStatus
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessCamera
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessProcessing
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessRuleSheet
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessViewModel
import com.rarilabs.rarime.modules.home.v3.model.ALREADY_SET_AMOUNT
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetTitle
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.BaseTooltip
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.BackgroundRemover
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider
import kotlinx.coroutines.launch

data class RuleOptionData(
    val isSelected: Boolean,
    val type: LikenessRule,
    val title: String,
    val badgeText: String? = null,
    val iconRes: Int
)

@Composable
fun DigitalLikenessExpandedWidget(
    modifier: Modifier = Modifier,
    expandedWidgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    viewModel: DigitalLikenessViewModel = hiltViewModel()
) {
    val colorScheme by viewModel.colorScheme.collectAsState()
    val selectedRule by viewModel.selectedRule.collectAsState()
    val isRegistered by viewModel.isRegistered.collectAsState()

    val faceImage by viewModel.faceImage.collectAsState()

    val livenessState by viewModel.livenessState.collectAsState()
    val errorState by viewModel.errorState.collectAsState()

    val downloadProgress by viewModel.downloadProgress.collectAsState()

    val scope = rememberCoroutineScope()

    LikenessExpandedWidgetContent(
        modifier = modifier,
        widgetProps = expandedWidgetProps,
        innerPaddings = innerPaddings,
        navigate = navigate,
        selectedRule = selectedRule,
        setSelectedRule = {
            scope.launch {
                viewModel.setSelectedRule(it)
            }
        },
        isRegistered = isRegistered,
        setIsScanned = {

        },
        saveFaceImage = viewModel.saveFaceImage,
        faceImage = faceImage,
        downloadProgress = downloadProgress,
        processImage = viewModel::processImage,
        livenessStatus = livenessState,
        livenessError = errorState,
        colorScheme = colorScheme
    )
}

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun LikenessExpandedWidgetContent(
    modifier: Modifier = Modifier,
    widgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    selectedRule: LikenessRule?,
    setSelectedRule: suspend (LikenessRule) -> Unit,
    isRegistered: Boolean,
    setIsScanned: (Boolean) -> Unit,
    saveFaceImage: (Bitmap) -> Unit,
    processImage: suspend (Bitmap) -> Unit,
    faceImage: Bitmap?,
    livenessStatus: LivenessProcessingStatus,
    livenessError: LivenessProcessingStatus?,
    downloadProgress: Int,
    colorScheme: AppColorScheme
) {
    val isPreview = LocalInspectionMode.current
    val appSheetState = rememberAppSheetState()
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()
    var selectedBitmap: Bitmap? by remember { mutableStateOf(null) }
    val ruleSheetState = rememberAppSheetState(false)
    val cameraPermissionState = if (!isPreview) rememberPermissionState(Manifest.permission.CAMERA)
    else null

    if (isPreview || cameraPermissionState!!.status.isGranted) {
        AppBottomSheet(
            state = appSheetState,
            //shape = RectangleShape,
            fullScreen = true,
            scrimColor = Color.Transparent,
            isHeaderEnabled = false,
            disablePullClose = true,
        ) {
            if (selectedBitmap == null) {
                DigitalLikenessCamera {
                    BackgroundRemover().removeBackground(it) { img ->
                        selectedBitmap = img
                    }
                }
            } else {
                DigitalLikenessProcessing(
                    modifier = Modifier.padding(vertical = 16.dp),
                    downloadProgress = downloadProgress,
                    processing = processImage,
                    currentProcessingState = livenessStatus,
                    currentProcessingError = livenessError,
                    selectedBitmap = selectedBitmap!!,
                    onNext = {
                        saveFaceImage(selectedBitmap!!)
                        appSheetState.hide()
                        scope.launch {
                            tooltipState.show()
                        }
                    })
            }
        }
    }

    DigitalLikenessRuleSheet(
        state = ruleSheetState,
        selectedRule = selectedRule,
        onSave = { newRule ->
            scope.launch {

                setSelectedRule(newRule)

                ruleSheetState.hide()
                if (!isRegistered) {
                    appSheetState.show()
                }
            }
        })


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
                        faceImage = faceImage,
                        isScanned = isRegistered,
                        selectedRule = selectedRule,
                        tooltipState = tooltipState,
                        onRuleSheetShow = { ruleSheetState.show() },
                        onTooltipShow = {
                            scope.launch {
                                tooltipState.show(
                                    MutatePriority.Default
                                )
                            }
                        }
                    )
                },
                footer = {
                    if (!isRegistered) {
                        Footer(
                            layoutId = layoutId,
                            innerPaddings = innerPaddings,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onStart = {
                                if (cameraPermissionState!!.status.isGranted) {
                                    ruleSheetState.show()
                                } else {
                                    cameraPermissionState.launchPermissionRequest()
                                }
                            }
                        )
                    }
                },
                background = {
                    Background(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        colorScheme = colorScheme
                    )
                },
                columnModifier = Modifier
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
                .padding(horizontal = 16.dp)
                .padding(top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp),
            horizontalArrangement = Arrangement.End
        ) {

            IconButton(
                onClick = onCollapse,
                modifier = Modifier
                    .padding(20.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = RarimeTheme.colors.componentPrimary)
            ) {
                AppIcon(
                    id = R.drawable.ic_close,
                    tint = RarimeTheme.colors.textPrimary,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Body(
    layoutId: Int,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    faceImage: Bitmap?,
    isScanned: Boolean,
    selectedRule: LikenessRule?,
    tooltipState: TooltipState,
    onRuleSheetShow: () -> Unit,
    onTooltipShow: () -> Unit
) {
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
                .padding(bottom = (innerPaddings[ScreenInsets.BOTTOM]!!.toInt()).dp)
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
                    .fillMaxWidth()

            ) {
                LikenessTitle(
                    isScanned = isScanned,
                    selectedRule = selectedRule,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    layoutId = layoutId,
                    onTooltipShow = onTooltipShow,
                    onRuleSheetShow = onRuleSheetShow,
                    tooltipState = tooltipState
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.digital_likeness_description),
                    style = RarimeTheme.typography.body4.copy(color = RarimeTheme.colors.textSecondary)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
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
    tooltipState: TooltipState,
    onTooltipShow: () -> Unit,
    onRuleSheetShow: () -> Unit
) {
    with(sharedTransitionScope) {
        Column {
            if (isScanned) {
                BaseTooltip(
                    state = tooltipState, tooltipContent = {
                        Text(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(RarimeTheme.colors.baseBlack)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            text = "Success! Your rule is set. You can\nupdate it anytime by clicking the title.",
                            color = RarimeTheme.colors.baseWhite
                        )
                    }) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                        BaseWidgetTitle(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onTooltipShow
                                ),
                            title = "My rule:",
                            accentTitle = when (selectedRule) {
                                LikenessRule.USE_AND_PAY -> "Use my likeness and pay me"
                                LikenessRule.NOT_USE -> "Don't use \nmy face data"
                                LikenessRule.ASK_FIRST -> "Ask me first"
                                else -> ""
                            },
                            titleStyle = RarimeTheme.typography.subtitle5.copy(
                                color = RarimeTheme.colors.invertedDark
                            ),
                            accentTitleStyle = RarimeTheme.typography.h2.copy(
                                brush = RarimeTheme.colors.gradient14
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
                            )
                        )
                        IconButton(
                            modifier = Modifier.offset(y = (-28).dp, x = (-100).dp),
                            onClick = onRuleSheetShow,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_carret_down),
                                contentDescription = null,
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(top = 15.dp),
                    color = RarimeTheme.colors.baseBlack.copy(alpha = 0.1f)
                )
            } else
                BaseWidgetTitle(
                    title = stringResource(R.string.digital_likeness_collapsed_widget_title),
                    accentTitle = stringResource(R.string.digital_likeness_collapsed_widget_accent_title),
                    titleStyle = RarimeTheme.typography.h2.copy(
                        color = RarimeTheme.colors.invertedDark
                    ),
                    accentTitleStyle = RarimeTheme.typography.h2.copy(brush = RarimeTheme.colors.gradient14),
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
                    caption = stringResource(R.string.digital_likeness_collapsed_widget_caption),
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
                    captionStyle = RarimeTheme.typography.body4.copy(color = RarimeTheme.colors.textSecondary)

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
    onStart: () -> Unit
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.footer(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .padding(
                    bottom = (innerPaddings[ScreenInsets.BOTTOM]!!.toInt() + 20).dp
                )
                .padding(horizontal = 20.dp)
        ) {
            HorizontalDivider(color = RarimeTheme.colors.baseBlack.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = String.format(
                            "%,d",
                            ALREADY_SET_AMOUNT
                        ), style = RarimeTheme.typography.h4,
                        color = RarimeTheme.colors.textPrimary
                    )
                    Text(
                        "Others already set",
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textSecondary
                    )

                }

                BaseButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RarimeTheme.colors.invertedDark,
                        contentColor = RarimeTheme.colors.invertedLight,
                        disabledContainerColor = RarimeTheme.colors.componentDisabled,
                        disabledContentColor = RarimeTheme.colors.textDisabled
                    ),
                    text = "Set a rule", size = ButtonSize.Large, onClick = onStart
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
        if (isDark) R.drawable.ic_bg_digital_likeness_dark
        else R.drawable.ic_bg_digital_likeness_light
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
@Preview(showBackground = true)
@Composable
fun LikenessExpandedWidgetPreview() {

    var selectedRule by remember {
        mutableStateOf(
            LikenessRule.USE_AND_PAY
        )
    }

    var isRegistered by remember {
        mutableStateOf(
            false
        )
    }

    PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
        LikenessExpandedWidgetContent(
            widgetProps = BaseWidgetProps.Expanded(
                layoutId = 1,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
                onCollapse = {}
            ),
            setSelectedRule = { selectedRule = it },
            isRegistered = isRegistered,
            innerPaddings = mapOf(ScreenInsets.TOP to 20, ScreenInsets.BOTTOM to 20),
            navigate = {},
            selectedRule = null,
            setIsScanned = {},
            saveFaceImage = {},
            faceImage = null,
            livenessStatus = LivenessProcessingStatus.DOWNLOADING,
            livenessError = null,
            processImage = {},
            downloadProgress = 0,
            colorScheme = AppColorScheme.LIGHT
        )
    }
}