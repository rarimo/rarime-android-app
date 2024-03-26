package com.distributedLab.rarime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun AppSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    Switch(
        modifier = modifier,
        enabled = enabled,
        checked = checked,
        onCheckedChange = onCheckedChange,
        thumbContent = {},
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = RarimeTheme.colors.baseWhite,
            uncheckedTrackColor = RarimeTheme.colors.componentHovered,
            uncheckedBorderColor = Color.Transparent,

            checkedThumbColor = RarimeTheme.colors.baseWhite,
            checkedTrackColor = RarimeTheme.colors.successMain,
            checkedBorderColor = Color.Transparent,

            disabledUncheckedThumbColor = RarimeTheme.colors.componentHovered,
            disabledUncheckedTrackColor = RarimeTheme.colors.componentDisabled,
            disabledUncheckedBorderColor = Color.Transparent,

            disabledCheckedThumbColor = RarimeTheme.colors.componentHovered,
            disabledCheckedTrackColor = RarimeTheme.colors.componentDisabled,
            disabledCheckedBorderColor = Color.Transparent,
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AppSwitchPreview() {
    var checkedValue by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppSwitch(
                checked = checkedValue,
                onCheckedChange = { checkedValue = it },
            )
            Text(
                text = "Regular",
                modifier = Modifier.padding(8.dp, 0.dp),
                style = RarimeTheme.typography.subtitle4,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppSwitch(
                checked = checkedValue,
                enabled = false,
                onCheckedChange = { checkedValue = it },
            )
            Text(
                text = "Disabled",
                modifier = Modifier.padding(8.dp, 0.dp),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textSecondary
            )
        }
    }
}
