package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DottedProgressBar(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    length: Int,
    offset: Dp = 12.dp,
    currentStep: Int
) {
    Row(modifier) {
        for (i in 0..<length) {
            if (i != 0) {
                Spacer(modifier = Modifier.width(offset))
            }
            DottedProgressItem(isActive = currentStep > i, size = size)
        }
    }
}

@Preview
@Composable
private fun DottedProgressBarPreview() {
    Surface {
        DottedProgressBar(length = 5, currentStep = 3)
    }
}