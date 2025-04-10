package com.rarilabs.rarime.ui.components


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun HorizontalPageIndicator(
    numberOfPages: Int,
    modifier: Modifier = Modifier,
    selectedPage: Int = 0,
    selectedColor: Color = Color.Blue,
    defaultColor: Color = Color.LightGray,
    defaultRadius: Dp = 20.dp,
    selectedLength: Dp = 60.dp,
    space: Dp = 30.dp,
    animationDurationInMillis: Int = 300,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space),
        modifier = modifier,
    ) {
        for (i in 0 until numberOfPages) {
            val isSelected = i == selectedPage
            HorizontalPageIndicatorView(
                isSelected = isSelected,
                selectedColor = selectedColor,
                defaultColor = defaultColor,
                defaultRadius = defaultRadius,
                selectedLength = selectedLength,
                animationDurationInMillis = animationDurationInMillis,
            )
        }
    }
}

@Composable
fun HorizontalPageIndicatorView(
    isSelected: Boolean,
    selectedColor: Color,
    defaultColor: Color,
    defaultRadius: Dp,
    selectedLength: Dp,
    animationDurationInMillis: Int,
    modifier: Modifier = Modifier,
) {
    // Animate the color state.
    val color: Color by animateColorAsState(
        targetValue = if (isSelected) selectedColor else defaultColor,
        animationSpec = tween(durationMillis = animationDurationInMillis)
    )
    // Animate the width.
    val width: Dp by animateDpAsState(
        targetValue = if (isSelected) selectedLength else defaultRadius,
        animationSpec = tween(durationMillis = animationDurationInMillis)
    )

    // Draw the indicator with the animated width and fixed height.
    Canvas(
        modifier = modifier.size(width = width, height = defaultRadius)
    ) {
        drawRoundRect(
            color = color,
            topLeft = Offset.Zero,
            size = Size(width = width.toPx(), height = defaultRadius.toPx()),
            cornerRadius = CornerRadius(x = defaultRadius.toPx(), y = defaultRadius.toPx())
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HorizontalPageIndicatorPreview() {
    val numberOfPages = 3
    val (selectedPage, setSelectedPage) = remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = selectedPage) {
        delay(3000)
        setSelectedPage((selectedPage + 1) % numberOfPages)
    }

    HorizontalPageIndicator(
        numberOfPages = numberOfPages,
        selectedPage = selectedPage,
        defaultRadius = 20.dp,
        selectedLength = 60.dp,
        space = 30.dp,
        animationDurationInMillis = 300,
    )
}