package com.distributedLab.rarime.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun AlertDialogCard(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onClose: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    vector: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_warning)
) {
    AlertDialog(containerColor = RarimeTheme.colors.backgroundPure, title = {
        Text(
            text = dialogTitle,
            style = RarimeTheme.typography.subtitle2,
            color = RarimeTheme.colors.textSecondary,
        )
    }, icon = { Icon(imageVector = vector, contentDescription = null) }, text = {
        Text(
            text = dialogText, style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary,
        )
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        PrimaryTextButton(onClick = {
            onConfirmation()
            onClose()
        }) {
            Text(
                stringResource(R.string.confirm), style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
            )
        }
    }, dismissButton = {
        PrimaryTextButton(onClick = {
            onDismissRequest()
        }) {
            Row {

                Text(
                    stringResource(R.string.dismiss), style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                )
                Spacer(modifier = Modifier.width(24.dp))

            }
        }
    })
}
