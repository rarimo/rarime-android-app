package com.rarilabs.rarime.modules.you

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun IdentityCardTypeItem(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    imageId: Int,
    name: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .clickable {
                if (isActive) {
                    onClick()
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(

            modifier = if (isActive) {
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(brush = RarimeTheme.colors.gradient1)
            } else {
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(RarimeTheme.colors.componentDisabled)

            },
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                id = imageId,
                tint = if (isActive) RarimeTheme.colors.textPrimary else RarimeTheme.colors.textDisabled,
                size = 24.dp,
                alpha = if (isActive) 1f else 0.28f,
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            name,
            style = RarimeTheme.typography.buttonLarge,
            color = if (isActive) RarimeTheme.colors.textPrimary else RarimeTheme.colors.textDisabled
        )
        Spacer(modifier = Modifier.weight(1f))

        if (isActive) {
            AppIcon(
                id = R.drawable.ic_caret_right,
                size = 16.dp,
                tint = RarimeTheme.colors.textSecondary
            )
        } else {
            Text(
                text = "SOON",
                color = RarimeTheme.colors.textSecondary,
                style = RarimeTheme.typography.overline2
            )
        }

    }
}

@Preview
@Composable
private fun IdentityCardTypeItemPreview() {
    Surface {
        Column {
            IdentityCardTypeItem(
                isActive = true,
                imageId = R.drawable.ic_rarimo,
                name = "Identity Card",
                onClick = {}
            )
            Spacer(Modifier.height(16.dp))
            IdentityCardTypeItem(
                isActive = false,
                imageId = R.drawable.ic_rarimo,
                name = "Identity Card",
                onClick = {}
            )
        }

    }
}