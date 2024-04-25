package com.distributedLab.rarime.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun AppAlertDialog(
    title: String,
    text: String,
    confirmText: String = stringResource(R.string.confirm),
    dismissText: String = stringResource(R.string.dismiss),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        containerColor = RarimeTheme.colors.backgroundPure,
        title = {
            Text(
                text = title,
                style = RarimeTheme.typography.subtitle2,
                color = RarimeTheme.colors.textPrimary,
            )
        },
        text = {
            Text(
                text = text,
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
            )
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            PrimaryTextButton(
                text = confirmText,
                onClick = { onConfirm() }
            )
        },
        dismissButton = {
            SecondaryTextButton(
                text = dismissText,
                modifier = Modifier.padding(end = 16.dp),
                onClick = { onDismiss() }
            )
        }
    )
}

@Preview
@Composable
private fun AppAlertDialogPreview() {
    AppAlertDialog(
        title = "Are you sure?",
        text = "This action cannot be undone.",
        onConfirm = {},
        onDismiss = {},
    )
}
