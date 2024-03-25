package com.distributedLab.rarime.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme

enum class UiButtonSize {
    Small, Medium, Large
}

enum class UiButtonVariant {
    Primary, Secondary, Tertiary
}

@Composable
fun UiButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: UiButtonSize = UiButtonSize.Medium,
    color: UiButtonVariant = UiButtonVariant.Primary,
    text: String? = null,
    @DrawableRes leftIcon: Int? = null,
    @DrawableRes rightIcon: Int? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit = {}
) {
    val colors = when (color) {
        UiButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = RarimeTheme.colors.primaryMain,
            contentColor = RarimeTheme.colors.baseBlack,
            disabledContainerColor = RarimeTheme.colors.componentDisabled,
            disabledContentColor = RarimeTheme.colors.textDisabled
        )
        UiButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = RarimeTheme.colors.componentPrimary,
            contentColor = RarimeTheme.colors.textPrimary,
            disabledContainerColor = RarimeTheme.colors.componentDisabled,
            disabledContentColor = RarimeTheme.colors.textDisabled
        )
        UiButtonVariant.Tertiary -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = RarimeTheme.colors.textSecondary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = RarimeTheme.colors.textDisabled
        )
    }

    val height = when (size) {
        UiButtonSize.Small -> 24.dp
        UiButtonSize.Medium -> 40.dp
        UiButtonSize.Large -> 48.dp
    }

    val padding = when (size) {
        UiButtonSize.Small -> PaddingValues(16.dp, 0.dp)
        UiButtonSize.Medium -> PaddingValues(24.dp, 0.dp)
        UiButtonSize.Large -> PaddingValues(32.dp, 0.dp)
    }

    val textStyle = when (size) {
        UiButtonSize.Small -> RarimeTheme.typography.buttonSmall
        UiButtonSize.Medium -> RarimeTheme.typography.buttonMedium
        UiButtonSize.Large -> RarimeTheme.typography.buttonLarge
    }

    val iconSize = when (size) {
        UiButtonSize.Small -> 16.dp
        UiButtonSize.Medium, UiButtonSize.Large -> 20.dp
    }

    Button(
        onClick = onClick,
        contentPadding = padding,
        modifier = modifier
            .height(height)
            .defaultMinSize(minWidth = 96.dp, minHeight = height),
        enabled = enabled,
        interactionSource = interactionSource,
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
fun UiButtonPreview() {
    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UiButton(color = UiButtonVariant.Primary,
            size = UiButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            text = "Primary Large",
            onClick = { })
        UiButton(color = UiButtonVariant.Secondary,
            size = UiButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = R.drawable.ic_arrow_left,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Secondary Large",
            onClick = { })
        UiButton(color = UiButtonVariant.Tertiary,
            size = UiButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = R.drawable.ic_arrow_left,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Tertiary Large",
            onClick = { })
        UiButton(enabled = false,
            size = UiButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = R.drawable.ic_arrow_left,
            text = "Disabled",
            onClick = { })
        UiButton(color = UiButtonVariant.Primary,
            size = UiButtonSize.Medium,
            leftIcon = R.drawable.ic_arrow_left,
            text = "Size Medium",
            onClick = { })
        UiButton(color = UiButtonVariant.Secondary,
            size = UiButtonSize.Small,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Size Small",
            onClick = { })
        UiButton(modifier = Modifier
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