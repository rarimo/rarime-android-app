package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun AppRadioButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(RarimeTheme.colors.componentPrimary, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        label()
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(
                    width = 1.dp,
                    color = if (isSelected) RarimeTheme.colors.secondaryMain else RarimeTheme.colors.componentHovered,
                    shape = CircleShape
                )
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.Center)
                    .background(
                        color = if (isSelected) RarimeTheme.colors.secondaryMain else Color.Transparent,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppRadioButtonPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        AppRadioButton(isSelected = true, onClick = {}) {
            Text(
                text = "Selected label",
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.textPrimary
            )
        }
        AppRadioButton(isSelected = false, onClick = {}) {
            Text(
                text = "Unselected label",
                style = RarimeTheme.typography.subtitle5,
                color = RarimeTheme.colors.textPrimary
            )
        }
    }
}
