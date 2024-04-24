package com.distributedLab.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun ActionCard(title: String, description: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    CardContainer(
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = RarimeTheme.typography.subtitle3,
                    color = RarimeTheme.colors.textPrimary
                )
                Text(
                    text = description,
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary
                )
            }
            AppIcon(
                id = R.drawable.ic_caret_right,
                size = 16.dp,
                modifier = Modifier
                    .background(RarimeTheme.colors.primaryMain, CircleShape)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ActionCardPreview() {
    ActionCard(
        title = "Scan passport",
        description = "Scan your passport to verify your identity",
        onClick = {}
    )
}
