package com.distributedLab.rarime.ui.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.UiIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme

enum class ButtonSize {
    Small, Medium, Large
}

@Composable
fun BaseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    text: String? = null,
    @DrawableRes leftIcon: Int? = null,
    @DrawableRes rightIcon: Int? = null,
    content: @Composable RowScope.() -> Unit = {}
) {
    val height = when (size) {
        ButtonSize.Small -> 24.dp
        ButtonSize.Medium -> 40.dp
        ButtonSize.Large -> 48.dp
    }

    val padding = when (size) {
        ButtonSize.Small -> PaddingValues(16.dp, 0.dp)
        ButtonSize.Medium -> PaddingValues(24.dp, 0.dp)
        ButtonSize.Large -> PaddingValues(32.dp, 0.dp)
    }

    val textStyle = when (size) {
        ButtonSize.Small -> RarimeTheme.typography.buttonSmall
        ButtonSize.Medium -> RarimeTheme.typography.buttonMedium
        ButtonSize.Large -> RarimeTheme.typography.buttonLarge
    }

    val iconSize = when (size) {
        ButtonSize.Small -> 16.dp
        ButtonSize.Medium, ButtonSize.Large -> 20.dp
    }

    Button(
        onClick = onClick,
        contentPadding = padding,
        modifier = modifier
            .height(height)
            .defaultMinSize(minWidth = 96.dp, minHeight = height),
        enabled = enabled,
        colors = colors,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            leftIcon?.let {
                UiIcon(
                    id = it,
                    size = iconSize,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            text?.let {
                Text(
                    text = it,
                    style = textStyle,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            rightIcon?.let {
                UiIcon(
                    id = it,
                    size = iconSize,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BaseButtonPreview() {
    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BaseButton(
            size = ButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = R.drawable.ic_arrow_left,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Large",
            onClick = { })
        BaseButton(enabled = false,
            size = ButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = R.drawable.ic_arrow_left,
            text = "Disabled",
            onClick = { })
        BaseButton(
            size = ButtonSize.Medium,
            leftIcon = R.drawable.ic_arrow_left,
            text = "Medium",
            onClick = { })
        BaseButton(
            size = ButtonSize.Small,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Small",
            onClick = { })
        BaseButton(
            modifier = Modifier
                .height(64.dp)
                .width(220.dp), onClick = { }) {
            Text(
                text = "Custom content",
                color = RarimeTheme.colors.errorDark,
                style = RarimeTheme.typography.subtitle3
            )
        }
    }
}