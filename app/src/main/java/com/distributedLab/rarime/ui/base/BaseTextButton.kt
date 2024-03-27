package com.distributedLab.rarime.ui.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun BaseTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = RarimeTheme.colors.textPrimary,
    size: ButtonSize = ButtonSize.Medium,
    text: String? = null,
    @DrawableRes leftIcon: Int? = null,
    @DrawableRes rightIcon: Int? = null,
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
    ) {
        leftIcon?.let {
            AppIcon(
                id = it,
                size = size.iconSize(),
                tint = color,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
        text?.let {
            Text(
                text = it,
                style = size.textStyle(),
                color = color,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
        rightIcon?.let {
            AppIcon(
                id = it,
                size = size.iconSize(),
                tint = color,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun TextButtonPreview() {
    Column(
        modifier = Modifier.padding(12.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BaseTextButton(
            size = ButtonSize.Large,
            leftIcon = R.drawable.ic_arrow_left,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Large",
            onClick = {
                println("Button clicked")
            })
        BaseTextButton(enabled = false,
            size = ButtonSize.Large,
            leftIcon = R.drawable.ic_arrow_left,
            text = "Disabled",
            onClick = { })
        BaseTextButton(
            size = ButtonSize.Medium,
            leftIcon = R.drawable.ic_arrow_left,
            text = "Medium",
            onClick = { })
        BaseTextButton(
            size = ButtonSize.Small,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Small",
            onClick = { })
        BaseTextButton(onClick = { }) {
            Text(
                text = "Custom content",
                color = RarimeTheme.colors.errorDark,
                style = RarimeTheme.typography.subtitle3
            )
        }
    }
}