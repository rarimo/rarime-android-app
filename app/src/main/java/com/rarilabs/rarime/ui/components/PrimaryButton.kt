package com.rarilabs.rarime.ui.components

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
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium,
    text: String? = null,
    @DrawableRes leftIcon: Int? = null,
    @DrawableRes rightIcon: Int? = null,
    content: @Composable RowScope.() -> Unit = {}
) {
    BaseButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        size = size,
        colors = ButtonDefaults.buttonColors(
            containerColor = RarimeTheme.colors.primaryMain,
            contentColor = RarimeTheme.colors.baseWhite,
            disabledContainerColor = RarimeTheme.colors.componentDisabled,
            disabledContentColor = RarimeTheme.colors.textDisabled
        ),
        text = text,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryButton(
            size = ButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = R.drawable.ic_arrow_left,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Large",
            onClick = { })
        PrimaryButton(enabled = false,
            size = ButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = R.drawable.ic_arrow_left,
            text = "Disabled",
            onClick = { })
        PrimaryButton(
            size = ButtonSize.Medium,
            leftIcon = R.drawable.ic_arrow_left,
            text = "Medium",
            onClick = { })
        PrimaryButton(
            size = ButtonSize.Small,
            rightIcon = R.drawable.ic_arrow_right,
            text = "Small",
            onClick = { })
        PrimaryButton(
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