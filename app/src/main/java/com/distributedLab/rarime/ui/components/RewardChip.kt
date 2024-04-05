package com.distributedLab.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun RewardChip(reward: Int, isActive: Boolean = false) {
    val bgColor =
        if (isActive) RarimeTheme.colors.warningLight else RarimeTheme.colors.componentPrimary
    val contentColor =
        if (isActive) RarimeTheme.colors.textPrimary else RarimeTheme.colors.textSecondary

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(bgColor, CircleShape)
            .padding(vertical = 2.dp, horizontal = 6.dp)
    ) {
        Text(
            text = "+$reward",
            style = RarimeTheme.typography.subtitle5,
            color = contentColor,
        )
        AppIcon(
            id = R.drawable.ic_rarimo,
            size = 16.dp,
            tint = contentColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardChipPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RewardChip(reward = 10)
        RewardChip(reward = 20, isActive = true)
    }
}