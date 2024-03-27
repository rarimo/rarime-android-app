package com.distributedLab.rarime.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.BaseButton
import com.distributedLab.rarime.ui.base.BaseTextButton
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun SecondaryTextButton(
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
        color = RarimeTheme.colors.textSecondary,
        text = text,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun SecondaryTextButtonPreview() {
    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SecondaryTextButton(
            size = ButtonSize.Large,
            leftIcon = R.drawable.ic_arrow_left,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Large",
            onClick = { })
        SecondaryTextButton(enabled = false,
            size = ButtonSize.Large,
            leftIcon = R.drawable.ic_arrow_left,
            text = "Disabled",
            onClick = { })
        SecondaryTextButton(
            size = ButtonSize.Medium,
            leftIcon = R.drawable.ic_arrow_left,
            text = "Medium",
            onClick = { })
        SecondaryTextButton(
            size = ButtonSize.Small,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Small",
            onClick = { })
        SecondaryTextButton(onClick = { }) {
            Text(
                text = "Custom content",
                color = RarimeTheme.colors.errorDark,
                style = RarimeTheme.typography.subtitle3
            )
        }
    }
}