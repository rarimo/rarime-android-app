package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun DottedProgressItem(
    isActive: Boolean = false,
    shape: Shape = RoundedCornerShape(24.dp),
    size: Dp = 24.dp
) {
    Box(
        modifier = Modifier
            .clip(shape)
            .background(color = if (isActive) RarimeTheme.colors.primaryMain else RarimeTheme.colors.componentDisabled)
            .size(size)
    ) {

    }
}


@Preview
@Composable
private fun DottedProgressItemPreview() {
    Surface {
        Row {
            DottedProgressItem(isActive = true)
            DottedProgressItem(isActive = false)
            DottedProgressItem(isActive = true)
        }
    }

}