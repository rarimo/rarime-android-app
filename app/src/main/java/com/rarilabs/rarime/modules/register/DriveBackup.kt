package com.rarilabs.rarime.modules.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.BaseButton
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppSkeleton
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.CircledBadge
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.theme.RarimeTheme


enum class DriveState {
    BACKED_UP, NOT_BACKED_UP, PKS_ARE_NOT_EQUAL, NOT_SIGNED_IN,
}

data class DriveBackupContent(
    val description: String,
    val icon: Int,
    val buttonText: String,
    val contentBadgeColor: Color,
    val containerColor: Color
)

@Composable
private fun getContentDescription(state: DriveState): DriveBackupContent {


    return when (state) {
        DriveState.BACKED_UP -> DriveBackupContent(
            description = stringResource(R.string.google_drive_backup_backed_up),
            icon = R.drawable.ic_check,
            buttonText = stringResource(R.string.google_drive_backup_delete_btn),
            contentBadgeColor = RarimeTheme.colors.successMain,
            containerColor = RarimeTheme.colors.backgroundPrimary
        )

        DriveState.NOT_BACKED_UP -> DriveBackupContent(
            description = stringResource(R.string.google_drive_backup_not_backed_up),
            icon = R.drawable.ic_backup,
            buttonText = stringResource(R.string.google_drive_backup_not_backed_uo),
            contentBadgeColor = RarimeTheme.colors.backgroundPrimary,
            containerColor = RarimeTheme.colors.primaryMain
        )

        DriveState.PKS_ARE_NOT_EQUAL -> DriveBackupContent(
            description = stringResource(R.string.google_drive_backup_pks_are_not_equal),
            icon = R.drawable.ic_warning,
            buttonText = stringResource(R.string.google_drive_backup_delete_old_key),
            contentBadgeColor = RarimeTheme.colors.warningMain,
            containerColor = RarimeTheme.colors.warningLight
        )

        DriveState.NOT_SIGNED_IN -> DriveBackupContent(
            description = stringResource(R.string.google_drive_backup_sign_in_description),
            icon = R.drawable.ic_key,
            buttonText = stringResource(R.string.google_drive_backup_sign_in),
            contentBadgeColor = RarimeTheme.colors.backgroundPrimary,
            containerColor = RarimeTheme.colors.primaryMain
        )
    }
}

@Composable
fun DriveBackup(
    state: DriveState,
    backUp:  () -> Unit,
    delete:  () -> Unit,
    sigIn:  () -> Unit,
    isDriveButtonEnabled: Boolean,
) {

    val contentDescription = getContentDescription(state)

    CardContainer {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircledBadge(
                iconId = contentDescription.icon,
                contentColor = contentDescription.contentBadgeColor,
                containerColor = contentDescription.containerColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(R.string.google_drive_backup_title),
                style = RarimeTheme.typography.h4
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = contentDescription.description,
                textAlign = TextAlign.Center,
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary,
            )
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            when (state) {
                DriveState.BACKED_UP -> {
                    BaseButton(
                        enabled = isDriveButtonEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RarimeTheme.colors.errorLighter,
                            contentColor = RarimeTheme.colors.errorDark
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {delete.invoke()},
                        leftIcon = R.drawable.ic_trash_simple,
                        text = contentDescription.buttonText,
                        size = ButtonSize.Large
                    )
                }

                DriveState.NOT_BACKED_UP -> {
                    PrimaryButton(
                        enabled = isDriveButtonEnabled,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {backUp.invoke()},
                        text = contentDescription.buttonText,
                        size = ButtonSize.Large
                    )
                }

                DriveState.PKS_ARE_NOT_EQUAL -> {
                    BaseButton(
                        enabled = isDriveButtonEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RarimeTheme.colors.errorLighter,
                            contentColor = RarimeTheme.colors.errorDark
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {delete.invoke()},
                        leftIcon = R.drawable.ic_trash_simple,
                        text = contentDescription.buttonText,
                        size = ButtonSize.Large
                    )
                }

                DriveState.NOT_SIGNED_IN -> {
                    PrimaryButton(
                        enabled = isDriveButtonEnabled,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { sigIn.invoke()},
                        text = contentDescription.buttonText,
                        size = ButtonSize.Large
                    )
                }
            }

        }
    }
}

@Composable
fun DriveBackupSkeleton(modifier: Modifier = Modifier) {
    CardContainer {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppSkeleton(Modifier.size(80.dp), cornerRadius = 200f)
            Spacer(modifier = Modifier.size(16.dp))
            AppSkeleton(
                Modifier
                    .height(28.dp)
                    .fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            AppSkeleton(
                Modifier
                    .height(12.dp)
                    .fillMaxWidth())
            Spacer(modifier = Modifier.height(4.dp))
            AppSkeleton(
                Modifier
                    .height(12.dp)
                    .fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            AppSkeleton(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp))

        }
    }
}



@Preview
@Composable
private fun DriveBackupPreview() {
    DriveBackup(DriveState.BACKED_UP, {}, {}, {}, false)
}

@Preview
@Composable
private fun DriveBackupSkeletonPreview() {
    DriveBackupSkeleton()
}