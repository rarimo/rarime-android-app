package com.rarilabs.rarime.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseIconButton
import com.rarilabs.rarime.ui.base.ButtonIconSize
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun SecondaryIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: ButtonIconSize = ButtonIconSize.Medium,
    @DrawableRes icon: Int,
) {
    BaseIconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        size = size,
        colors = ButtonDefaults.buttonColors(
            containerColor = RarimeTheme.colors.componentPrimary,
            contentColor = RarimeTheme.colors.textPrimary,
            disabledContainerColor = RarimeTheme.colors.componentDisabled,
            disabledContentColor = RarimeTheme.colors.textDisabled
        ),
        icon = icon,
    )
}

@Preview(showBackground = true)
@Composable
private fun SecondaryIconButtonPreview() {
    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SecondaryIconButton(
            size = ButtonIconSize.Large,
            icon = R.drawable.ic_arrow_left,
            onClick = { })
        SecondaryIconButton(
            enabled = false,
            size = ButtonIconSize.Large,
            modifier = Modifier.fillMaxWidth(),
            icon = R.drawable.ic_arrow_left,
            onClick = { })
        SecondaryIconButton(
            size = ButtonIconSize.Medium,
            icon = R.drawable.ic_arrow_left,
            onClick = { })
        SecondaryIconButton(
            size = ButtonIconSize.Small,
            icon = R.drawable.ic_arrow_right,
            onClick = { })
        SecondaryIconButton(
            modifier = Modifier.height(64.dp).width(220.dp),
            onClick = { },
            icon = R.drawable.ic_arrow_right,
        )
    }
}