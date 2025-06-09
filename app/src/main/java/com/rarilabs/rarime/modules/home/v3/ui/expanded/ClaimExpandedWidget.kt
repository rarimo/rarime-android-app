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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.home.v3.model.ANIMATION_DURATION_MS
import com.rarilabs.rarime.modules.home.v3.model.BG_ClAIM_HEIGHT
import com.rarilabs.rarime.modules.home.v3.model.BaseWidgetProps
import com.rarilabs.rarime.modules.home.v3.model.WidgetType
import com.rarilabs.rarime.modules.home.v3.model.HomeSharedKeys
import com.rarilabs.rarime.modules.home.v3.model.getClaimWidgetAccentTitle
import com.rarilabs.rarime.modules.home.v3.model.getClaimWidgetTitle
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseExpandedWidget
import com.rarilabs.rarime.modules.home.v3.ui.components.BaseWidgetTitle
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider

@Composable
fun ClaimExpandedWidget(
    modifier: Modifier = Modifier,
    expandedWidgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    currentPointsBalance: Long?
) {
    ClaimExpandedWidgetContent(
        modifier = modifier,
        widgetProps = expandedWidgetProps,
        innerPaddings = innerPaddings,
        navigate = navigate,
        currentPointsBalance = currentPointsBalance,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ClaimExpandedWidgetContent(
    modifier: Modifier = Modifier,
    widgetProps: BaseWidgetProps.Expanded,
    innerPaddings: Map<ScreenInsets, Number>,
    navigate: (String) -> Unit,
    currentPointsBalance: Long?
) {
    val isClaimed = currentPointsBalance != null && currentPointsBalance != 0L
    val title = getClaimWidgetTitle(isClaimed)
    val accentTitle = getClaimWidgetAccentTitle(currentPointsBalance)

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
                footer = {
                    Footer(
                        layoutId = layoutId,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        innerPaddings = innerPaddings,
                        title = title,
                        accentTitle = accentTitle
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
private fun Footer(
    layoutId: Int,
    innerPaddings: Map<ScreenInsets, Number>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    title: String,
    accentTitle: String
) {
    with(sharedTransitionScope) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = (innerPaddings[ScreenInsets.BOTTOM]!!.toInt() + 24).dp)
                .sharedBounds(
                    rememberSharedContentState(HomeSharedKeys.footer(layoutId)),
                    animatedVisibilityScope = animatedVisibilityScope,
                    renderInOverlayDuringTransition = false,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
        ) {
            Spacer(Modifier.height((BG_ClAIM_HEIGHT + 125).dp))
            BaseWidgetTitle(
                title = title,
                accentTitle = accentTitle,
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
            TermsAndConditionsText()
        }
    }
}

@Composable
fun TermsAndConditionsText() {
    val uriHandler = LocalUriHandler.current

    val termsAnnotation = buildAnnotatedString {
        append(stringResource(R.string.terms_check_agreement))
        pushStringAnnotation("URL", Constants.TERMS_URL)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(stringResource(R.string.rarime_general_terms_conditions))
        }
        pop()
        append(", ")
        pushStringAnnotation("URL", Constants.PRIVACY_URL)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(stringResource(R.string.rarime_privacy_notice))
        }
        pop()
        append(stringResource(R.string.and))
        pushStringAnnotation("URL", Constants.AIRDROP_TERMS_URL)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(stringResource(R.string.rarimo_airdrop_program_terms_conditions))
        }
        pop()
    }

    ClickableText(
        modifier = Modifier.fillMaxWidth(),
        text = termsAnnotation,

        style = RarimeTheme.typography.body5.copy(
            color = RarimeTheme.colors.baseBlack.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        ),
        onClick = {
            termsAnnotation
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
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
                .background(RarimeTheme.colors.gradient3)
                .sharedBounds(
                    rememberSharedContentState(
                        HomeSharedKeys.content(layoutId)
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> tween(durationMillis = ANIMATION_DURATION_MS) },
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
        ) {
            Image(
                painter = painterResource(R.drawable.claim_rmo_image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BG_ClAIM_HEIGHT.dp)
                    .offset(y = 175.dp)
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
fun ClaimExpandedWidgetPreview_Claimed() {
    PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
        ClaimExpandedWidgetContent(
            widgetProps = BaseWidgetProps.Expanded(
                layoutId = WidgetType.IDENTITY.layoutId,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
                onCollapse = {}
            ),
            innerPaddings = mapOf(ScreenInsets.TOP to 40, ScreenInsets.BOTTOM to 20),
            navigate = {},
            currentPointsBalance = 100L
        )
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun ClaimExpandedWidgetPreview_Unclaimed() {
    PrevireSharedAnimationProvider { sharedTransitionScope, animatedVisibilityScope ->
        ClaimExpandedWidgetContent(
            widgetProps = BaseWidgetProps.Expanded(
                layoutId = WidgetType.IDENTITY.layoutId,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
                onCollapse = {}
            ),
            innerPaddings = mapOf(ScreenInsets.TOP to 40, ScreenInsets.BOTTOM to 20),
            navigate = {},
            currentPointsBalance = null
        )
    }
}
