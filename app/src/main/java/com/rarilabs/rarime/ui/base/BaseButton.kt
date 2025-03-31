package com.rarilabs.rarime.ui.base

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

enum class ButtonSize {
    Small,
    Medium,
    Large;

    fun height(): Dp = when (this) {
        Small -> 32.dp
        Medium -> 40.dp
        Large -> 56.dp
    }

    fun padding(): PaddingValues = when (this) {
        Small -> PaddingValues(14.dp, 0.dp)
        Medium -> PaddingValues(16.dp, 0.dp)
        Large -> PaddingValues(24.dp, 0.dp)
    }

    @Composable
    fun textStyle() = when (this) {
        Small -> RarimeTheme.typography.buttonSmall
        Medium -> RarimeTheme.typography.buttonMedium
        Large -> RarimeTheme.typography.buttonLarge
    }

    fun iconSize(): Dp = when (this) {
        Small -> 16.dp
        Medium, Large -> 20.dp
    }

    fun cornerRadius(): Dp = when (this) {
        Small -> 12.dp
        Medium -> 16.dp
        Large -> 20.dp
    }
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
    Button(
        onClick = onClick,
        contentPadding = size.padding(),
        modifier = modifier
            .height(size.height())
            .defaultMinSize(minWidth = 96.dp, minHeight = size.height()),
        shape = RoundedCornerShape(size.cornerRadius()),
        enabled = enabled,
        colors = colors,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            leftIcon?.let {
                AppIcon(
                    id = it,
                    size = size.iconSize(),
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            text?.let {
                Text(
                    text = it,
                    style = size.textStyle(),
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            rightIcon?.let {
                AppIcon(
                    id = it,
                    size = size.iconSize(),
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BaseButtonPreview() {
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
                style = RarimeTheme.typography.subtitle5
            )
        }
    }
}