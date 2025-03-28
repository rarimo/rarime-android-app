package com.rarilabs.rarime.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun UiLinearProgressBar(
    backgroundModifier: Modifier = Modifier,
    trackModifier: Modifier = Modifier,
    percentage: Float = 0f,
    backgroundColor: Color = RarimeTheme.colors.backgroundPrimary,
    trackColors: List<Color> = listOf(
        RarimeTheme.colors.primaryMain,
        RarimeTheme.colors.primaryMain
    ),
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(backgroundColor)
            .then(backgroundModifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(100.dp))
                .background(
                    Brush.horizontalGradient(
                        trackColors,
                    )
                )
                .fillMaxWidth(percentage)
                .then(trackModifier)
        )
    }
}

@Preview
@Composable
fun UiLinearProgressBarPreview() {
    CardContainer(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp),
    ) {
        Column {
            UiLinearProgressBar(
                percentage = 0.1f
            )

            Spacer(modifier = Modifier.height(24.dp))

            UiLinearProgressBar(
                percentage = 0.25f
            )

            Spacer(modifier = Modifier.height(24.dp))

            UiLinearProgressBar(
                percentage = 0.5f
            )

            Spacer(modifier = Modifier.height(24.dp))

            UiLinearProgressBar(
                percentage = 0.75f
            )

            Spacer(modifier = Modifier.height(24.dp))

            UiLinearProgressBar(
                percentage = 0.75f,
                trackColors = listOf(
                    RarimeTheme.colors.primaryMain,
                    RarimeTheme.colors.warningDarker,
                    RarimeTheme.colors.errorLighter,
                    RarimeTheme.colors.successDarker,
                )
            )
        }
    }
}