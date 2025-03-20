package com.rarilabs.rarime.modules.home.v2.details


import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
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
    currentPointsBalance: Long
) {

    val props = DetailsProperties(
        id = id,
        header = "Claim",
        subTitle = "$currentPointsBalance RMO",
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
            Text(
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                text = "This app is where you privately store your digital identities, enabling you to go incognito across the web."
            )

            Spacer(modifier = Modifier.height(24.dp))
            TransparentButton(
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                text = "Claim",
                onClick = {})

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = RarimeTheme.typography.body5.copy(textDecoration = TextDecoration.Underline),
                color = RarimeTheme.colors.textSecondary,
                text = "By continue, you are agreeing to RariMe General Terms & Conditions, RariMe Privacy Notice  and Rarimo Airdrop Program Terms & Conditions ",
            )
            Spacer(modifier = Modifier.height(16.dp))
        },
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