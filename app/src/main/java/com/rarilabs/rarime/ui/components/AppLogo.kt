package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
    Card(
        colors = CardDefaults.cardColors(containerColor = RarimeTheme.colors.baseBlack.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(radius.dp),
        modifier = modifier
            .size(wrapperSize.dp)
            .shadow(12.dp, RoundedCornerShape(radius.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(wrapperSize.dp)
        ) {
            AppIconWithGradient(
                modifier = Modifier.scale(scale),
                id = R.drawable.ic_rarime,
                size = iconSize.dp,
            )
        }
    }
}


