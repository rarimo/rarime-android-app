package com.rarilabs.rarime.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun TransparentButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: ButtonSize = ButtonSize.Medium,
    text: String? = null,
    @DrawableRes leftIcon: Int? = null,
    @DrawableRes rightIcon: Int? = null,
    alpha: Float = 0.05f,
    baseColor: Color = RarimeTheme.colors.componentPrimary,
    content: @Composable RowScope.() -> Unit = {},
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = baseColor.copy(alpha),
        contentColor = RarimeTheme.colors.textPrimary,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = RarimeTheme.colors.textDisabled
    )
) {
    BaseButton(
        modifier = Modifier
            .background(
                baseColor,
                RoundedCornerShape(20.dp)
            )
            .then(modifier),
        onClick = onClick,
        enabled = enabled,
        size = size,
        colors = colors,
        text = text,
        leftIcon = leftIcon,
        rightIcon = rightIcon,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun TransparentButtonPreview() {
    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransparentButton(
            enabled = true,
            size = ButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            leftIcon = R.drawable.ic_arrow_left,
            text = "Enabled",
            onClick = { })
    }
}