package com.rarilabs.rarime.modules.recoveryMethod

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.DriveState
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppAlertDialog
import com.rarilabs.rarime.ui.components.AppSwitch
import com.rarilabs.rarime.ui.components.CirclesLoader
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.PrimaryTextButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


@Composable
fun RecoveryMethodDetailScreen(
    onClose: () -> Unit,
    deleteBackup: () -> Unit,
    backupPrivateKey: () -> Unit,
    isSwitchEnabled: Boolean,
    signIn: () -> Unit,
    privateKey: String,
    isInit: Boolean,
    driveState: DriveState = DriveState.NOT_SIGNED_IN,
    isHeaderEnabled: Boolean = true
) {

    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(3.seconds)
            isCopied = false
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showOverwriteDialog by remember { mutableStateOf(false) }

    var cloudBackupChecked by remember { mutableStateOf(false) }


    LaunchedEffect(driveState) {
        cloudBackupChecked = driveState == DriveState.BACKED_UP
    }

    fun onSwitchClick() {
        when (driveState) {
            DriveState.BACKED_UP -> {
                showDeleteDialog = true

            }

            DriveState.NOT_BACKED_UP -> {
                backupPrivateKey()
            }

            DriveState.PKS_ARE_NOT_EQUAL -> {
                showOverwriteDialog = true
            }

            DriveState.NOT_SIGNED_IN -> {
                throw IllegalStateException("Switch should not be visible when NOT_SIGNED_IN")
            }
        }
    }


    if (showDeleteDialog) {
        AppAlertDialog(
            title = stringResource(R.string.warning),
            text = stringResource(R.string.recovery_method_delete_dialog_text),
            onConfirm = {
                deleteBackup()
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
            })
    }

    if (showOverwriteDialog) {
        AppAlertDialog(
            title = stringResource(R.string.warning),
            text = stringResource(R.string.recovery_method_overwrite_dialog_text),
            onConfirm = {
                backupPrivateKey()
                showOverwriteDialog = false
            },
            onDismiss = {
                showOverwriteDialog = false
            })
    }


    Column(modifier = Modifier.fillMaxSize()) {
        if (isHeaderEnabled) {

            Box(
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 32.dp)
            ) {
                PrimaryTextButton(
                    leftIcon = R.drawable.ic_caret_left, onClick = onClose
                )
                Text(
                    text = stringResource(R.string.recovery_method_details_screen_title),
                    style = RarimeTheme.typography.subtitle6,
                    color = RarimeTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = RarimeTheme.colors.componentPrimary)

        ) {
            Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_key_2_line),
                    contentDescription = "",
                    tint = RarimeTheme.colors.textPrimary,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .size(20.dp)
                )
                Text(
                    text = stringResource(R.string.recovery_method_detail_screen_card_holder_title),
                    style = RarimeTheme.typography.subtitle5,
                    color = RarimeTheme.colors.textPrimary,

                    )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                    ),
                shape = RoundedCornerShape(corner = CornerSize(12.dp)),
                elevation = CardDefaults.cardElevation(0.dp),
                colors = CardDefaults.cardColors(containerColor = RarimeTheme.colors.backgroundSurface1)
            ) {
                Text(
                    text = privateKey,
                    style = RarimeTheme.typography.body4,
                    color = RarimeTheme.colors.textPrimary,
                    modifier = Modifier.padding(20.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                Button(
                    onClick = {
                        isCopied = true
                        clipboardManager.setText(AnnotatedString(privateKey))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
                    modifier = Modifier.fillMaxWidth(0.95f)

                ) {
                    Icon(
                        painter = if (isCopied) painterResource(R.drawable.ic_check) else painterResource(
                            R.drawable.ic_file_copy_line
                        ),
                        contentDescription = "",
                        tint = RarimeTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = if (isCopied) stringResource(R.string.recovery_method_detail_screen_copied) else stringResource(
                            R.string.recovery_method_detail_screen_copy
                        ),
                        style = RarimeTheme.typography.buttonMedium,
                        color = RarimeTheme.colors.textPrimary,
                        modifier = Modifier
                    )
                }
            }
            Text(
                text = stringResource(R.string.recovery_method_detal_screen_card_holder_description),
                style = RarimeTheme.typography.body5,
                minLines = 3,
                color = RarimeTheme.colors.textSecondary,
                modifier = Modifier.padding(20.dp)
            )

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            RecoveryMethodCard(
                isEnabled = isSwitchEnabled,
                iconId = R.drawable.ic_cloud_line,
                isRecommended = true,
                title = stringResource(R.string.recovery_method_cloud_backup_title),
                description = stringResource(R.string.recovery_method_cloud_backup_description),
                rightContent = {

                    if (isInit) {

                        when (driveState) {
                            DriveState.NOT_SIGNED_IN -> {
                                PrimaryButton(
                                    enabled = isSwitchEnabled, text = "Sign in", onClick = {
                                        signIn()
                                    }, size = ButtonSize.Small
                                )
                            }

                            else -> {
                                AppSwitch(
                                    checked = cloudBackupChecked,
                                    onCheckedChange = {
                                        onSwitchClick()
                                    },
                                    enabled = isSwitchEnabled,
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                        .size(width = 40.dp, height = 24.dp)
                                )
                            }
                        }
                    } else {
                        CirclesLoader()
                    }

                })
            RecoveryMethodCard(
                isEnabled = false,
                iconId = R.drawable.ic_emotion_happy_line,
                isRecommended = false,
                title = stringResource(R.string.recovery_method_face_scan_title),
                description = stringResource(R.string.recovery_method_face_scan_description),
                rightContent = {
                    Text(
                        text = "SOON",
                        style = RarimeTheme.typography.overline3,
                        color = RarimeTheme.colors.baseBlack,
                        modifier = Modifier
                            .padding(vertical = 15.dp)
                            .background(
                                brush = RarimeTheme.colors.gradient12,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                })

            RecoveryMethodCard(
                isEnabled = false,
                iconId = R.drawable.ic_box_3_line,
                isRecommended = false,
                title = stringResource(R.string.recovery_method_object_scan_title),
                description = stringResource(R.string.recovery_method_object_scan_description),
                rightContent = {
                    Text(
                        text = "SOON",
                        style = RarimeTheme.typography.overline3,
                        color = RarimeTheme.colors.baseBlack,
                        modifier = Modifier
                            .padding(vertical = 15.dp)
                            .background(
                                brush = RarimeTheme.colors.gradient12,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                })
        }

    }

}

@Composable
private fun RecoveryMethodCard(
    isEnabled: Boolean,
    iconId: Int,
    isRecommended: Boolean,
    title: String,
    description: String,
    rightContent: @Composable () -> Unit = {}
) {
    val borderColor = if (!isEnabled) RarimeTheme.colors.componentDisabled
    else if (isRecommended) RarimeTheme.colors.warningBase else RarimeTheme.colors.componentPrimary
    val iconTint =
        if (isEnabled) RarimeTheme.colors.textPrimary else RarimeTheme.colors.textSecondary
    val titleColor =
        if (isEnabled) RarimeTheme.colors.textPrimary else RarimeTheme.colors.textSecondary
    val descColor = RarimeTheme.colors.textSecondary
    val verticalPadding = if (isEnabled) 8.dp else 6.dp
    val contentPadding =
        if (isEnabled) PaddingValues(start = 30.dp, end = 30.dp, bottom = 20.dp, top = 10.dp)
        else PaddingValues(vertical = 20.dp, horizontal = 30.dp)

    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = 1.dp, color = borderColor),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding)
    ) {
        if (isRecommended) {
            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "RECOMENDED",
                    style = RarimeTheme.typography.overline3,
                    color = RarimeTheme.colors.baseWhite,
                    modifier = Modifier
                        .background(
                            color = if (isEnabled) RarimeTheme.colors.warningBase else RarimeTheme.colors.componentPrimary,
                            shape = RoundedCornerShape(bottomStart = 8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        } else if (isEnabled) {
            Spacer(modifier = Modifier.width(12.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp, end = 20.dp)
                    .size(20.dp)
            )

            Column {
                Text(
                    text = title,
                    style = RarimeTheme.typography.subtitle5,
                    color = titleColor,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = description,
                    style = RarimeTheme.typography.body5,
                    color = descColor,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            rightContent()
        }
    }
}

@Preview
@Composable
fun PreviewRecoveryMethodDetailScreenPreview() {
    Surface {
        RecoveryMethodDetailScreen(
            onClose = {},
            deleteBackup = {},
            backupPrivateKey = {},
            isSwitchEnabled = true,
            signIn = {},
            privateKey = "pkfpokopkokokwfopkwopfkwopefkp",
            isInit = false,
            driveState = DriveState.NOT_SIGNED_IN
        )
    }
}