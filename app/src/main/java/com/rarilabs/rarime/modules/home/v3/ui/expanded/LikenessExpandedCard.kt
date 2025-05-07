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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.LikenessRule
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessCamera
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessProcessing
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessRuleSheet
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessViewModel
import com.rarilabs.rarime.modules.home.v3.model.ALREADY_SET_AMOUNT
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BaseCardProps
import com.rarilabs.rarime.modules.home.v3.model.CardType
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.model.IMG_LIKENESS_HEIGHT
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseCardTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedCard
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.BaseButton
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
fun LikenessExpandedCard(
    modifier: Modifier = Modifier,
    expandedCardProps: BaseCardProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    viewModel: DigitalLikenessViewModel = hiltViewModel()
) {
    val selectedRule by viewModel.selectedRule.collectAsState()
    val isScanned by viewModel.isLivenessScanned.collectAsState()
    val faceImage by viewModel.faceImage.collectAsState()

    LikenessExpandedCardContent(
        modifier = modifier,
        cardProps = expandedCardProps,
        innerPaddings = innerPaddings,
        navigate = navigate,
        selectedRule = selectedRule,
        setSelectedRule = viewModel.setSelectedRule,
        isScanned = isScanned,
        setIsScanned = viewModel.setIsLivenessScanned,
        saveFaceImage = viewModel.saveFaceImage,
        faceImage = faceImage
    )
}

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun LikenessExpandedCardContent(
    modifier: Modifier = Modifier,
    cardProps: BaseCardProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    selectedRule: LikenessRule?,
    setSelectedRule: (LikenessRule) -> Unit,
    isScanned: Boolean,
    setIsScanned: (Boolean) -> Unit,
    saveFaceImage: (Bitmap) -> Unit,
    faceImage: Bitmap?
) {
    val isPreview = LocalInspectionMode.current
    val appSheetState = rememberAppSheetState()

    val tooltipState = rememberTooltipState()

    val scope = rememberCoroutineScope()
    var selectedBitmap: Bitmap? by remember { mutableStateOf(null) }

    val cameraPermissionState = if (!isPreview) rememberPermissionState(Manifest.permission.CAMERA)
    else null

    val ruleSheetState = rememberAppSheetState(false)

    if (isPreview || cameraPermissionState!!.status.isGranted) {
        AppBottomSheet(
            state = appSheetState,
            shape = RectangleShape,
            fullScreen = true,
            scrimColor = Color.Transparent,
            isHeaderEnabled = false,
            disableScrollClose = true,
            isWindowInsetsEnabled = true,
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
                    onNext = {
                        setIsScanned(true)
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
            setSelectedRule(newRule)
            ruleSheetState.hide()
            if (!isScanned) {
                appSheetState.show()
            }
        })


    with(cardProps) {
        with(sharedTransitionScope) {
            BaseExpandedCard(
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
                .padding(bottom = 20.dp)
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.content(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    renderInOverlayDuringTransition = false,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
        ) {
            Image(
                painter = painterResource(R.drawable.drawable_digital_likeness),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IMG_LIKENESS_HEIGHT.dp)
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
            Spacer(modifier = Modifier.height(40.dp))
            BaseCardTitle(
                title = "Digital likeness",
                accentTitle = "Set a rule",
                titleStyle = RarimeTheme.typography.h1.copy(color = RarimeTheme.colors.baseBlack),
                accentTitleStyle = RarimeTheme.typography.additional1.copy(color = RarimeTheme.colors.baseBlackOp40),
                titleModifier = Modifier.sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.title(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                ),
                accentTitleModifier = Modifier.sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.gradientTitle(layoutId)),
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
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Text(
                    text = "AI can now replicate your face, voice, and identity without asking for your permission. But you never agreed to that, raising a fundamental question: who owns your likeness?",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseBlackOp50
                )
                Text(
                    text = "Rarimo is building the infrastructure to give you back that control. With this app, you can create a private, verifiable record that defines how your likeness can and can’t be used.",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseBlackOp50
                )
                Text(
                    text = "Your face stays on your device. No company owns it. And over time, no AI model will be able to ignore your rule.",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseBlackOp50
                )
            }
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
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(ALREADY_SET_AMOUNT, style = RarimeTheme.typography.h4)
                    Text(
                        "Already registered",
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.baseBlack.copy(alpha = 0.5f)
                    )

                }

                BaseButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RarimeTheme.colors.baseBlack,
                        contentColor = RarimeTheme.colors.baseWhite,
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
@Preview(showBackground = true)
@Composable
fun LikenessExpandedCardPreview() {
    PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
        LikenessExpandedCardContent(
            cardProps = BaseCardProps.Expanded(
                layoutId = CardType.IDENTITY.layoutId,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
                onCollapse = {}
            ),
            innerPaddings = mapOf(ScreenInsets.TOP to 20, ScreenInsets.BOTTOM to 20),
            navigate = {},
            selectedRule = null,
            setSelectedRule = {},
            isScanned = false,
            setIsScanned = {},
            saveFaceImage = {},
            faceImage = null
        )
    }
}
