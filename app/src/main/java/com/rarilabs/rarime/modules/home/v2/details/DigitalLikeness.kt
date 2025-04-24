package com.rarilabs.rarime.modules.home.v2.details

import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.LikenessRule
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessCamera
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessProcessing
import com.rarilabs.rarime.modules.digitalLikeness.DigitalLikenessViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

const val ALREADY_SET_AMOUNT = "49,421"

data class RuleOptionData(
    val isSelected: Boolean,
    val type: LikenessRule,
    val title: String,
    val badgeText: String? = null,
    val iconRes: Int
)

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun DigitalLikeness(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navigate: (String) -> Unit = {},
    viewModel: DigitalLikenessViewModel = hiltViewModel()
) {

    val selectedRule by viewModel.selectedRule.collectAsState()
    val isScanned by viewModel.isLivenessScanned.collectAsState()

    DigitalLikenessContent(
        modifier,
        id,
        onBack,
        innerPaddings,
        sharedTransitionScope,
        animatedContentScope,
        selectedRule,
        viewModel.setSelectedRule,
        isScanned,
        viewModel.setIsLivenessScanned
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalPermissionsApi::class)
@Composable
fun DigitalLikenessContent(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    selectedRule: LikenessRule,
    setSelectedRule: (LikenessRule) -> Unit,
    isScanned: Boolean,
    setIsScanned: (Boolean) -> Unit
) {

    val isPreview = LocalInspectionMode.current
    val appSheetState = rememberAppSheetState()


    val cameraPermissionState = if (!isPreview) rememberPermissionState(Manifest.permission.CAMERA)
    else null
    val ruleSheetState = rememberAppSheetState(false)



    if (isPreview || cameraPermissionState!!.status.isGranted) {
        AppBottomSheet(
            state = appSheetState,
            fullScreen = true,
            scrimColor = Color.Transparent,
            isHeaderEnabled = false,
            isWindowInsetsEnabled = true,
        ) {
            var selectedBitmap: Bitmap? by remember { mutableStateOf(null) }

            if (selectedBitmap == null) {
                DigitalLikenessCamera {
                    selectedBitmap = it
                }
            } else {
                DigitalLikenessProcessing(
                    modifier = Modifier.padding(vertical = 16.dp),
                    onNext = { setIsScanned(true); appSheetState.hide() })
            }
        }
    }

    RuleSheet(state = ruleSheetState, selectedRule = selectedRule, onRuleSelect = { newRule ->
        setSelectedRule(newRule)
    }, onSave = {
        ruleSheetState.hide()
        if (!isScanned) {
            appSheetState.show()
        }

    })


    val props = DetailsProperties(
        id = id,
        header = stringResource(R.string.digital_likeness),
        subTitle = stringResource(R.string.set_a_rule),
        caption = if (isScanned) null else stringResource(R.string.first_human_ai_contract),
        imageId = R.drawable.drawable_digital_likeness,
        backgroundGradient = RarimeTheme.colors.gradient7,
        imageModifier = Modifier
            .padding(horizontal = 52.dp)
            .padding(bottom = 40.dp)
    )

    BaseDetailsScreen(
        properties = props,
        innerPaddings = innerPaddings,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onBack = onBack,
        header = if (isScanned) { headerKey, subTitleKey ->
            with(sharedTransitionScope) {
                Text(
                    style = RarimeTheme.typography.h5,
                    color = RarimeTheme.colors.baseBlack,
                    text = "My Rules:",
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(
                            headerKey
                        ), animatedVisibilityScope = animatedContentScope
                    )
                )

                val selectedRuleText =
                    when (selectedRule) {
                        LikenessRule.ALWAYS_ALLOW -> {
                            stringResource(R.string.use_my_likeness_and_pay_me)
                        }

                        LikenessRule.REJECT -> {
                            stringResource(R.string.don_t_sell_my_face_data)
                        }

                        LikenessRule.ASK_EVERYTIME -> {
                            stringResource(R.string.ask_me_every_time)
                        }
                    }



                PrimaryTextButton(
                    modifier = Modifier.skipToLookaheadSize(),
                    rightIcon = R.drawable.ic_carret_down,
                    onClick = { ruleSheetState.show(); Log.i("XD", "HERE") },
                    content = {
                        Text(
                            style = RarimeTheme.typography.additional1,
                            text = selectedRuleText,
                            color = RarimeTheme.colors.baseBlack.copy(alpha = 0.4f),
                            modifier = Modifier
                                .sharedBounds(
                                    rememberSharedContentState(
                                        subTitleKey
                                    ), animatedVisibilityScope = animatedContentScope
                                )
                                .skipToLookaheadSize(),
                        )
                    })
            }
        } else {
            null
        },
        body = {
            Column {
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseBlack.copy(alpha = 0.5f),
                    text = stringResource(R.string.digital_likeness_description_1)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseBlack.copy(alpha = 0.5f),
                    text = stringResource(R.string.digital_likeness_description_2)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.baseBlack.copy(alpha = 0.5f),
                    text = stringResource(R.string.digital_likeness_description_3)
                )
            }
        },
        footer = if (isScanned) null else {
            {
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
                            stringResource(R.string.digital_likeness_registered_lbl),
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textSecondary
                        )

                    }

                    PrimaryButton(
                        onClick = {
                            if (cameraPermissionState!!.status.isGranted) {
                                ruleSheetState.show()
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }

                        }, text = stringResource(R.string.set_a_rule), size = ButtonSize.Large
                    )
                }
            }
        },
    )
}

@Composable
private fun RuleSheet(
    state: AppSheetState,
    selectedRule: LikenessRule,
    onRuleSelect: (LikenessRule) -> Unit,
    modifier: Modifier = Modifier,
    onSave: () -> Unit,
) {
    val rules = listOf(
        RuleOptionData(
            isSelected = LikenessRule.ALWAYS_ALLOW == selectedRule,
            type = LikenessRule.ALWAYS_ALLOW,
            title = stringResource(R.string.use_my_likeness_and_pay_me),
            badgeText = stringResource(R.string.soon),
            iconRes = R.drawable.money_dollar_circle_line
        ), RuleOptionData(
            isSelected = LikenessRule.REJECT == selectedRule,
            type = LikenessRule.REJECT,
            title = stringResource(R.string.don_t_sell_my_face_data),
            badgeText = stringResource(R.string.soon),
            iconRes = R.drawable.subtract_fill
        ), RuleOptionData(
            isSelected = LikenessRule.ASK_EVERYTIME == selectedRule,
            type = LikenessRule.ASK_EVERYTIME,
            title = stringResource(R.string.ask_me_every_time),
            badgeText = stringResource(R.string.soon),
            iconRes = R.drawable.ic_question
        )
    )


    AppBottomSheet(modifier, state, isHeaderEnabled = false) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
                ) {
                    Text(stringResource(R.string.set_the_rule), style = RarimeTheme.typography.h2)
                    // TODO: Recheck the text below
                    Text(
                        "All of those options are demo and....",
                        color = RarimeTheme.colors.textSecondary
                    )
                }
                IconButton(onClick = { state.hide() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close), contentDescription = "Close"
                    )
                }
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                state = rememberLazyListState(),
                contentPadding = PaddingValues(vertical = 17.dp),
                reverseLayout = false,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                flingBehavior = ScrollableDefaults.flingBehavior(),
                userScrollEnabled = true
            ) {
                items(rules.size) { index ->
                    RuleOption(
                        item = rules[index], onClick = { likenessRule ->
                            onRuleSelect(likenessRule)
                        })
                }
            }
            Spacer(Modifier.height(17.dp))
            PrimaryButton(
                text = stringResource(R.string.save),
                size = ButtonSize.Large,
                onClick = onSave,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RuleOption(
    item: RuleOptionData, onClick: (LikenessRule) -> Unit, modifier: Modifier = Modifier
) {
    val iconBackground by animateColorAsState(
        targetValue = if (item.isSelected) RarimeTheme.colors.textPrimary else RarimeTheme.colors.componentPrimary,
        animationSpec = tween(durationMillis = 300)
    )
    val iconTint by animateColorAsState(
        targetValue = if (item.isSelected) RarimeTheme.colors.invertedLight else RarimeTheme.colors.textPrimary,
        animationSpec = tween(durationMillis = 300)
    )

    val cardBorderColor by animateColorAsState(
        targetValue = if (item.isSelected) RarimeTheme.colors.textPrimary else (RarimeTheme.colors.componentPrimary.copy(
            alpha = 0.05f
        )), animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .size(width = 160.dp, height = 160.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                ),
                onClick = { onClick(item.type) }),
        colors = CardDefaults.cardColors(
            containerColor = RarimeTheme.colors.backgroundSurface1
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackground, shape = CircleShape)
                    .padding(10.dp)
            ) {
                Icon(
                    painter = painterResource(item.iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Status badge
                item.badgeText?.let { text ->
                    Text(
                        text = text.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RarimeTheme.colors.infoDarker,
                        modifier = Modifier
                            .background(
                                color = RarimeTheme.colors.infoLighter,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = item.title, style = RarimeTheme.typography.subtitle6, modifier = Modifier
                )
            }
        }
    }
}

@Preview
@Composable
fun RuleOptionPreview() {
    RuleOption(
        item = RuleOptionData(
            isSelected = false,
            type = LikenessRule.ALWAYS_ALLOW,
            title = "Use my likeness\nand pay me.",
            badgeText = "Soon",
            iconRes = R.drawable.money_dollar_circle_line
        ), onClick = {})
}

@Preview
@Composable
fun RuleSheetPreview() {
    val ruleSheetState = rememberAppSheetState(false)
    RuleSheet(
        ruleSheetState,
        selectedRule = LikenessRule.ALWAYS_ALLOW,
        onRuleSelect = {},
        onSave = { ruleSheetState.hide() })
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun CreateIdentityDetailsPreview() {


    var selectedRule by remember {
        mutableStateOf(
            LikenessRule.ALWAYS_ALLOW
        )
    }

    var isScanned by remember {
        mutableStateOf(
            true
        )
    }

    PrevireSharedAnimationProvider { state, anim ->
        DigitalLikenessContent(
            id = 0,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            innerPaddings = mapOf(ScreenInsets.TOP to 23, ScreenInsets.BOTTOM to 12),
            onBack = {},
            selectedRule = selectedRule,
            setSelectedRule = { selectedRule = it },
            isScanned = isScanned,
            setIsScanned = { isScanned = it })
    }
}
