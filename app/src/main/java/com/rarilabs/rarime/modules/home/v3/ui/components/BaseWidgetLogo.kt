package com.rarilabs.rarime.modules.home.v3.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun BaseWidgetLogo(
    modifier: Modifier = Modifier,
    size: Int = 40,
    iconSize: Int = size,
    contentPadding: Int = 8,
    @DrawableRes
    resId: Int = R.drawable.ic_rarime,
    tint: Color = RarimeTheme.colors.textPrimary,
    shape: Shape = CircleShape,
    backgroundColor: Color = RarimeTheme.colors.backgroundBlur
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(shape)
            .background(backgroundColor)
            .padding(contentPadding.dp),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(
            id = resId,
            tint = tint,
            modifier = Modifier.size(iconSize.dp)
        )
    }
}

@Preview(showBackground = true, name = "Default Circle")
@Composable
fun BaseWidgetLogoPreview_Default() {
    BaseWidgetLogo()
}

@Preview(showBackground = true, name = "Cut Corner Shape")
@Composable
fun BaseWidgetLogoPreview_CutCorner() {
    BaseWidgetLogo(
        size = 48,
        iconSize = 32,
        contentPadding = 8,
        resId = R.drawable.ic_body_scan_fill,
        tint = RarimeTheme.colors.baseWhite,
        shape = CutCornerShape(5.dp),
        backgroundColor = RarimeTheme.colors.successDark
    )
}

@Preview(showBackground = true, name = "Rounded Rectangle")
@Composable
fun BaseWidgetLogoPreview_RoundedRect() {
    BaseWidgetLogo(
        size = 48,
        iconSize = 24,
        contentPadding = 12,
        resId = R.drawable.ic_body_scan_fill,
        tint = RarimeTheme.colors.errorDark,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = RarimeTheme.colors.errorLighter
    )
}
