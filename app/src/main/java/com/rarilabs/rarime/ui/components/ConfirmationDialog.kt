package com.rarilabs.rarime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun ConfirmationDialog(
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},

    iconId: Int = R.drawable.ic_trash_simple,
    iconContainerColor: Color = RarimeTheme.colors.errorLight,
    cancelButtonText: String = "Cancel",

    title: String,
    subtitle: String,

    confirmButtonText: String = "Confirm",
    confirmButtonColors: ButtonColors = ButtonDefaults.buttonColors(
        contentColor = RarimeTheme.colors.errorDarker,
        containerColor = RarimeTheme.colors.errorLight,
    ),
) {
    AlertDialog(
        containerColor = RarimeTheme.colors.backgroundPure,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(
                    id = iconId,
                    size = 32.dp,
                    tint = RarimeTheme.colors.errorDarker
                )
            }
        },
        title = {
            Text(
                text = title,
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = subtitle,
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        },
        onDismissRequest = onCancel,
        confirmButton = {
            BaseButton(
                onClick = onConfirm,
                colors = confirmButtonColors,
            ) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            SecondaryTextButton(
                modifier = Modifier.padding(12.dp),
                text = cancelButtonText,
                onClick = onCancel,
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmationDialogPreview() {
    ConfirmationDialog(
        title = stringResource(R.string.delete_profile_title),
        subtitle = stringResource(R.string.delete_profile_desc),
    )
}