package com.distributedLab.rarime.modules.rewards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun RewardAmountPreview(
    modifier: Modifier = Modifier,
    amount: Long = 0,
    tokenIconId: Int = R.drawable.ic_rarimo,
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = RarimeTheme.colors.textSecondary,
                shape = RoundedCornerShape(100.dp),
            )
            .padding(vertical = 2.dp, horizontal = 8.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "+${amount}",
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.textSecondary,
            )
            AppIcon(id = tokenIconId, size = 14.dp, tint = RarimeTheme.colors.textSecondary)
        }
    }
}

@Preview
@Composable
fun RewardAmountPreviewPreview() {
    Column(
        modifier = Modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(16.dp)
    ) {
        RewardAmountPreview(amount = 100)
    }
}