package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme


@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = RarimeTheme.colors.backgroundOpacity,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(20.dp),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun CardContainerPreview() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(16.dp)
    ) {
        CardContainer {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "My Card Title",
                    style = RarimeTheme.typography.subtitle2,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = "Some card description",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(top = 12.dp)
                        .background(RarimeTheme.colors.componentPrimary, RoundedCornerShape(16.dp))
                )
            }
        }
    }
}