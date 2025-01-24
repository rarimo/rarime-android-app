package com.rarilabs.rarime.ui.components

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.rarilabs.rarime.R

@Composable
fun AppAnimation(modifier: Modifier = Modifier, @RawRes id: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(id))
    val animationModifier = remember { modifier }
    LottieAnimation(
        modifier = animationModifier,
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
}

@Preview(showBackground = true)
@Composable
private fun AppAnimationPreview() {
    AppAnimation(id = R.raw.anim_intro_incognito)
}
