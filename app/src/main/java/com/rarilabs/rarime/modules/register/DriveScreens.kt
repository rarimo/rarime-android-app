package com.rarilabs.rarime.modules.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CircledBadge
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.TertiaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme

@Composable
fun RestoreScreen(
    modifier: Modifier = Modifier,
    isDriveButtonEnabled: Boolean,
    signInAccount: GoogleSignInAccount? = null,
    signIn: () -> Unit,
    onBack: () -> Unit,
    onDriveRestore: () -> Unit,
    onManualRestore: () -> Unit,
) {
    val context = LocalContext.current

    fun resolveButtonMessage(): String {
        return if (!isDriveButtonEnabled) {
            context.getString(R.string.drive_loading)
        } else if (signInAccount == null) {
            context.getString(R.string.google_drive_backup_sign_in)
        } else {
            context.getString(R.string.drive_restore_using_google_drive)
        }
    }

    Column(
        modifier = modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .padding(top = 10.dp, bottom = 16.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                modifier = Modifier
                    .clip(CircleShape),
                onClick = { onBack.invoke() },
                enabled = isDriveButtonEnabled,
                content = {
                    AppIcon(
                        modifier = Modifier.padding(10.dp),
                        id = R.drawable.ic_arrow_left,
                        size = 20.dp,
                        tint = RarimeTheme.colors.textPrimary
                    )
                }
            )
        }


        Spacer(modifier = Modifier.height(40.dp))
        CircledBadge(
            iconId = R.drawable.ic_backup,
            contentSize = 80,
            containerSize = 160,
            contentColor = RarimeTheme.colors.textPrimary,
            containerColor = RarimeTheme.colors.componentPrimary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            stringResource(R.string.drive_title_restore_your_account),
            style = RarimeTheme.typography.h2,
            color = RarimeTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            stringResource(R.string.drive_restore_description),
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = resolveButtonMessage(),
                enabled = isDriveButtonEnabled,
                onClick = {
                    if (signInAccount == null) signIn.invoke() else onDriveRestore.invoke()
                })
            TertiaryButton(
                text = stringResource(R.string.restore_using_key),
                enabled = isDriveButtonEnabled,
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onManualRestore.invoke() }
            )
        }
    }

}


@Composable
fun BackUpScreen(
    modifier: Modifier = Modifier,
    signInAccount: GoogleSignInAccount? = null,
    signIn: () -> Unit,
    onDriveBackup: (privateKey: String) -> Unit,
    onManualBackup: () -> Unit,
    isDriveButtonEnabled: Boolean,
    onBack: () -> Unit,
    privateKey: String
) {

    val context = LocalContext.current

    fun resolveButtonMessage(): String {
        return if (!isDriveButtonEnabled) {
            context.getString(R.string.drive_loading)
        } else if (signInAccount == null) {
            context.getString(R.string.google_drive_backup_sign_in)
        } else {
            context.getString(R.string.drive_back_up_to_google_drive)
        }
    }

    Column(
        modifier = modifier
            .background(RarimeTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .padding(top = 10.dp, bottom = 16.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                modifier = Modifier
                    .clip(CircleShape),
                onClick = { onBack.invoke() },
                enabled = isDriveButtonEnabled,
                content = {
                    AppIcon(
                        modifier = Modifier.padding(10.dp),
                        id = R.drawable.ic_arrow_left,
                        size = 20.dp,
                        tint = RarimeTheme.colors.textPrimary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        CircledBadge(
            iconId = R.drawable.ic_backup,
            contentSize = 80,
            containerSize = 160,
            contentColor = RarimeTheme.colors.textPrimary,
            containerColor = RarimeTheme.colors.componentPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            stringResource(R.string.drive_title_back_up_your_account),
            style = RarimeTheme.typography.h2,
            color = RarimeTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            stringResource(R.string.drive_backup_description),
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            PrimaryButton(
//                modifier = Modifier.fillMaxWidth(),
//                size = ButtonSize.Large,
//                text = resolveButtonMessage(),
//                enabled = isDriveButtonEnabled,
//                onClick = {
//                    if (signInAccount == null) signIn.invoke() else onDriveBackup.invoke(privateKey)
//                })
            PrimaryButton(
                text = stringResource(R.string.drive_continue_without_backup),
                enabled = isDriveButtonEnabled,
                size = ButtonSize.Large,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onManualBackup.invoke() }
            )
        }
    }

}

@Preview
@Composable
private fun BackUpScreenPreview() {
    BackUpScreen(Modifier, null, {}, {}, {}, false, {}, "")
}

@Preview
@Composable
private fun RestoreScreenPreview() {
    RestoreScreen(Modifier, isDriveButtonEnabled = false, null, {}, {}, {}, {})
}