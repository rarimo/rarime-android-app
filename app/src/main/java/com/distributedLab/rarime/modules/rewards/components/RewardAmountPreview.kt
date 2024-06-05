package com.distributedLab.rarime.modules.rewards.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun RewardAmountPreview(
    modifier: Modifier = Modifier
) {
    Box (
        modifier = modifier
            .border(
                width = 1.dp,
                color = RarimeTheme.colors.textSecondary,
                shape = RoundedCornerShape(100.dp),
            )
            .padding(vertical = 2.dp, horizontal = 8.dp)
    ) {
        Text (
            text = "+50 ra", // TODO: token icon
            style = RarimeTheme.typography.subtitle5,
            color = RarimeTheme.colors.textSecondary,
        )
    }
}