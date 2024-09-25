package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun StepIndicator(
    itemsCount: Int,
    selectedIndex: Int,
    updateSelectedIndex: (Int) -> Unit = {},
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(itemsCount) { index ->
            Box(
                modifier = Modifier
                    .width(if (index == selectedIndex) 16.dp else 8.dp)
                    .height(8.dp)
                    .background(
                        color = if (index == selectedIndex) RarimeTheme.colors.primaryMain else RarimeTheme.colors.componentPrimary,
                        shape = CircleShape
                    )
                    .clickable { updateSelectedIndex(index) }
            ) {}
        }
    }
}