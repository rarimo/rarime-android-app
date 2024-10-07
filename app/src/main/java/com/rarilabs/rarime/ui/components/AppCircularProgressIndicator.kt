package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun AppCircularProgressIndicator(modifier: Modifier = Modifier) {

    CircularProgressIndicator(
        modifier = modifier
            .width(64.dp)
            .height(64.dp),
        color = RarimeTheme.colors.textPrimary,
        trackColor = RarimeTheme.colors.textDisabled,
    )


}

@Preview
@Composable
private fun AppCircularProgressIndicatorPreview() {
    AppCircularProgressIndicator()
}