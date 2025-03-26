package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme


@Preview(showBackground = true)
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    radius: Int = 48,
    wrapperSize: Int = 187,
    iconSize: Int = 96
) {
    Box(
        modifier = modifier
            .size(wrapperSize.dp)
            .background(
                RarimeTheme.colors.textPrimary,
                RoundedCornerShape(radius.dp)
            ),
        contentAlignment = Alignment.Center,
    ) {
        AppIconWithGradient(
            modifier = Modifier
                .scale(scale),
            id = R.drawable.ic_rarime,
            size = iconSize.dp,
        )
    }
}
