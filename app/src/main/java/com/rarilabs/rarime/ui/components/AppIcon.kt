package com.rarilabs.rarime.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    @DrawableRes id: Int,
    size: Dp = 20.dp,
    description: String? = null,
    tint: Color = LocalContentColor.current
) {
    Icon(
        painter = painterResource(id),
        contentDescription = description,
        modifier = modifier.size(size),
        tint = tint,
    )
}

@Preview(showBackground = true)
@Composable
private fun AppIconPreview() {
    Row(
        modifier = Modifier.padding(12.dp, 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppIcon(id = R.drawable.ic_bell_fill)
        AppIcon(id = R.drawable.ic_qr_code, size = 24.dp)
        AppIcon(id = R.drawable.ic_cardholder, size = 32.dp, tint = RarimeTheme.colors.errorMain)
    }
}
