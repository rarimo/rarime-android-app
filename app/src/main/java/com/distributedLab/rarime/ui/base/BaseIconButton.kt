package com.distributedLab.rarime.ui.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme

enum class ButtonIconSize {
    Small,
    Medium,
    Large;

    fun size(): Dp = when (this) {
        Small -> 24.dp
        Medium -> 40.dp
        Large -> 48.dp
    }

    fun padding(): PaddingValues = when (this) {
        Small -> PaddingValues(4.dp, 0.dp)
        Medium -> PaddingValues(6.dp, 0.dp)
        Large -> PaddingValues(8.dp, 0.dp)
    }

    fun iconSize(): Dp = when (this) {
        Small -> 12.dp
        Medium, Large -> 16.dp
    }
}

@Composable
fun BaseIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: ButtonIconSize = ButtonIconSize.Medium,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    @DrawableRes icon: Int
) {
    Button(
        onClick = onClick,
        contentPadding = size.padding(),
        modifier = modifier.width(size.size()).height(size.size()),
        enabled = enabled,
        colors = colors,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            icon.let {
                AppIcon(
                    id = it,
                    size = size.iconSize(),
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BaseIconButtonPreview() {
    Column(
        modifier = Modifier.padding(12.dp, 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BaseIconButton(
            size = ButtonIconSize.Large,
            icon = R.drawable.ic_arrow_left,
            onClick = { })
        BaseIconButton(enabled = false,
            size = ButtonIconSize.Large,
            modifier = Modifier.fillMaxWidth(),
            icon = R.drawable.ic_arrow_left,
            onClick = { })
        BaseIconButton(
            size = ButtonIconSize.Medium,
            icon = R.drawable.ic_arrow_left,
            onClick = { })
        BaseIconButton(
            size = ButtonIconSize.Small,
            icon = R.drawable.ic_arrow_right,
            onClick = { })
        BaseIconButton(
            modifier = Modifier.height(64.dp).width(220.dp),
            onClick = { },
            icon = R.drawable.ic_arrow_right,
        )


        BaseIconButton(
            modifier = Modifier.height(64.dp).width(220.dp),
            onClick = { },
            icon = R.drawable.ic_arrow_right,
            colors = ButtonColors(
                containerColor = RarimeTheme.colors.primaryMain,
                contentColor = RarimeTheme.colors.textPrimary,
                disabledContainerColor = RarimeTheme.colors.componentDisabled,
                disabledContentColor = RarimeTheme.colors.textPrimary.copy(alpha = 0.5f),
            ),
            enabled = false
        )


    }
}