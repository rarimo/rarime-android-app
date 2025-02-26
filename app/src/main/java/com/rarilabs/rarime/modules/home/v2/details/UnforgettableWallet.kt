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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.TransparentButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.PrevireSharedAnimationProvider


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun UnforgettableWalletScreen(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {

    val props = DetailsProperties(
        id = id,
        header = "An Unforgettable",
        subTitle = "Wallet",
        imageId = R.drawable.no_more_seed_image,
        backgroundGradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFCE3FC),
                Color(0xFFD3D1EF)
            )
        )
    )

    BaseDetailsScreen(
        properties = props,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onBack = onBack,
        footer = {
            Text(
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                text = "Say goodbye to seed phrases! ZK Face Wallet leverages cutting-edge zero-knowledge (ZK) cryptography and biometric authentication to give you a seamless, secure, and self-sovereign crypto experience."
            )

            Spacer(modifier = Modifier.height(24.dp))
            TransparentButton(
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                text = "Join Waitlist",
                onClick = {})

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = RarimeTheme.typography.body5,
                color = RarimeTheme.colors.textSecondary,
                text = "49,421 other already joined",
            )
            Spacer(modifier = Modifier.height(16.dp))
        },
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun UnforgettableWalletScreenPreview() {

    PrevireSharedAnimationProvider { state, anim ->
        UnforgettableWalletScreen(
            id = 0,
            sharedTransitionScope = state,
            animatedContentScope = anim,
            onBack = {}
        )

    }
}