package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun AppBackgroundGradient(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0x66A5E87D),
        Color(0x6695F1CD),
        Color.Transparent,
        Color.Transparent
    ),
    center: Offset = Offset(10f, 10f),
    radius: Float = 1000f,
    scaleX: Float = 3f,
    height: Int = 300
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .graphicsLayer(scaleX = scaleX)
            .background(
                brush = Brush.radialGradient(
                    colors = colors,
                    center = center,
                    radius = radius
                )
            )
    )
}