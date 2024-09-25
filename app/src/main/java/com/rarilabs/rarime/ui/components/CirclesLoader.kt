package com.rarilabs.rarime.ui.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun CirclesLoader(
    size: Dp = 24.dp,
    color: Color = RarimeTheme.colors.textPrimary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.size(size)
    ) {
        repeat(3) { index ->
            val circleSize = size / 6
            val offsetValue by infiniteTransition.animateValue(
                initialValue = -circleSize,
                targetValue = circleSize,
                typeConverter = Dp.VectorConverter,
                label = "",
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, easing = EaseInOut),
                    initialStartOffset = StartOffset(150 * index),
                    repeatMode = RepeatMode.Reverse,
                ),
            )

            Box(
                modifier = Modifier
                    .padding(circleSize / 4)
                    .size(circleSize)
                    .offset(y = offsetValue)
                    .background(color, CircleShape),
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CirclesLoaderPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CirclesLoader()
        CirclesLoader(size = 32.dp, color = Color.Red)
        CirclesLoader(size = 48.dp, color = Color.Blue)
    }
}