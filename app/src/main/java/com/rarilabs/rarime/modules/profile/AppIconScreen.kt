package com.rarilabs.rarime.modules.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.enums.AppIcon
import com.rarilabs.rarime.data.enums.toLocalizedString
import com.rarilabs.rarime.ui.components.AppRadioButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun AppIconScreen(
    appIcon: AppIcon,
    onAppIconChange: (AppIcon) -> Unit,
    onBack: () -> Unit
) {
    ProfileRouteLayout(title = stringResource(R.string.app_icon), onBack = onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppIcon.entries.forEach { icon ->
                AppIconItem(
                    appIcon = icon,
                    isSelected = icon == appIcon,
                    onClick = { onAppIconChange(icon) }
                )
            }
        }
    }
}

@Composable
private fun AppIconItem(
    appIcon: AppIcon,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    AppRadioButton(isSelected = isSelected, onClick = onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(appIcon.iconId),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = 1.dp,
                        color = RarimeTheme.colors.componentPrimary,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
            Text(
                text = appIcon.toLocalizedString(),
                style = RarimeTheme.typography.buttonMedium,
                color = RarimeTheme.colors.textPrimary
            )
        }
    }
}

@Preview
@Composable
private fun AppIconScreenPreview() {
    AppIconScreen(
        appIcon = AppIcon.BLACK_AND_WHITE,
        onAppIconChange = {},
        onBack = {}
    )
}
