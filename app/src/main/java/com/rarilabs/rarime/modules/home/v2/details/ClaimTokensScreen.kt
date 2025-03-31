package com.rarilabs.rarime.modules.home.v2.details


import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ClaimTokensScreen(
    modifier: Modifier = Modifier,
    id: Int,
    innerPaddings: Map<ScreenInsets, Number>,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    currentPointsBalance: Long?
) {

    val props = DetailsProperties(
        id = id,
        header = if (currentPointsBalance != null && currentPointsBalance != 0L) stringResource(R.string.reserved) else stringResource(
            R.string.upcoming
        ),
        subTitle = if (currentPointsBalance != null && currentPointsBalance != 0L) ("$currentPointsBalance " + stringResource(
            R.string.rmo
        )) else stringResource(
            R.string.rmo
        ),
        imageId = R.drawable.claim_rmo_image,
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFF4F3F0),
                Color(0xFFDFFCC4)
            )
        )
    )

    BaseDetailsScreen(
        properties = props,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onBack = onBack,
        innerPaddings = innerPaddings,
        footer = {
            if (currentPointsBalance != null && currentPointsBalance != 0L) {
//                Text(
//                    style = RarimeTheme.typography.body3,
//                    color = RarimeTheme.colors.baseBlack.copy(alpha = 0.5f),
//                    text = "This app is where you privately store your digital identities, enabling you to go incognito across the web."
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//                TransparentButton(
//                    size = ButtonSize.Large,
//                    modifier = Modifier.fillMaxWidth(),
//                    text = "Claim",
//                    onClick = {})
//
//                Spacer(modifier = Modifier.height(16.dp))
            }
            TermsAndConditionsText()
            Spacer(modifier = Modifier.height(16.dp))
        },
    )
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
@Preview
@Composable
private fun ClaimTokensScreenPreview() {

    PrevireSharedAnimationProvider { state, anim ->
        ClaimTokensScreen(
            id = 0,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            onBack = {},
            innerPaddings = mapOf(ScreenInsets.TOP to 23, ScreenInsets.BOTTOM to 12),
            currentPointsBalance = 100
        )

    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun ClaimTokensScreenPreviewNobalance() {

    PrevireSharedAnimationProvider { state, anim ->
        ClaimTokensScreen(
            id = 0,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            onBack = {},
            innerPaddings = mapOf(ScreenInsets.TOP to 23, ScreenInsets.BOTTOM to 12),
            currentPointsBalance = null
        )

    }
}