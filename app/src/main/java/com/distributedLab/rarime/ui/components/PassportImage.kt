package com.distributedLab.rarime.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun PassportImage(
    modifier: Modifier = Modifier,
    image: Bitmap? = null,
    size: Dp = 56.dp,
    color: Color = RarimeTheme.colors.textPrimary,
    backgroundColor: Color = RarimeTheme.colors.backgroundPrimary,
) {
    if (image == null) {
        AppIcon(
            id = R.drawable.ic_user,
            size = size.times(0.5f),
            tint = color,
            modifier = modifier
                .background(backgroundColor, CircleShape)
                .border(1.dp, RarimeTheme.colors.componentPrimary, CircleShape)
                .padding(size.times(0.25f))
        )
    } else {
        Image(
            bitmap = image.asImageBitmap(),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            modifier = modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White, CircleShape)
                .border(1.dp, RarimeTheme.colors.componentPrimary, CircleShape)
        )
    }
}

@Composable
@Preview
fun PassportImagePreview() {
    PassportImage()
}
