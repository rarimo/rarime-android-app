package com.rarilabs.rarime.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseTextButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.base.TextButtonColors
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun PrimaryTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium,
    text: String? = null,
    @DrawableRes leftIcon: Int? = null,
    @DrawableRes rightIcon: Int? = null,
    content: @Composable RowScope.() -> Unit = {}
) {
    BaseTextButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        size = size,
        colors = TextButtonColors(
            contentColor = RarimeTheme.colors.textPrimary,
            pressedColor = RarimeTheme.colors.textPlaceholder,
            disabledColor = RarimeTheme.colors.textDisabled
        ),
        text = text,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun PrimaryTextButtonPreview() {
    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryTextButton(
            size = ButtonSize.Large,
            leftIcon = R.drawable.ic_arrow_left,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Large",
            onClick = { })
        PrimaryTextButton(enabled = false,
            size = ButtonSize.Large,
            leftIcon = R.drawable.ic_arrow_left,
            text = "Disabled",
            onClick = { })
        PrimaryTextButton(
            size = ButtonSize.Medium,
            leftIcon = R.drawable.ic_arrow_left,
            text = "Medium",
            onClick = { })
        PrimaryTextButton(
            size = ButtonSize.Small,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Small",
            onClick = { })
        PrimaryTextButton(onClick = { }) {
            Text(
                text = "Custom content",
                color = RarimeTheme.colors.errorDark,
                style = RarimeTheme.typography.subtitle3
            )
        }
    }
}