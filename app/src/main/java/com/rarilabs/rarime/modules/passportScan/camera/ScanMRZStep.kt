package com.rarilabs.rarime.modules.passportScan.camera

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.passportScan.ScanPassportLayout
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.ActionCard
import com.rarilabs.rarime.ui.components.AppAlertDialog
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.AppTextField
import com.rarilabs.rarime.ui.components.FieldTypeDatePicker
import com.rarilabs.rarime.ui.components.HorizontalDivider
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.TertiaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.components.rememberAppTextFieldState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.DateUtil
import org.jmrtd.lds.icao.MRZInfo
import java.util.Calendar

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanMRZStep(onNext: (MRZInfo) -> Unit, onClose: () -> Unit) {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)

    var isPermissionDialogShown by remember { mutableStateOf(true) }

    ScanMRZStepContent(
        onClose,
        onManual = { onNext(it) },
        cameraContent = {
            if (cameraPermissionState.status.isGranted) {
                CameraScanPassport(
                    modifier = Modifier
                        .background(RarimeTheme.colors.baseBlack)
                        .height(300.dp),
                    onMrzDetected = { onNext(it) }
                )
            } else {
                if (isPermissionDialogShown) {
                    AppAlertDialog(title = stringResource(R.string.camera_permission_title),
                        text = stringResource(R.string.camera_permission_description),
                        onConfirm = {
                            cameraPermissionState.launchPermissionRequest()
                        },
                        onDismiss = { isPermissionDialogShown = false })
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RarimeTheme.colors.componentPrimary)
                            .height(250.dp)
                            .padding(20.dp), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.scan_mrzstep_permission_denied),
                            style = RarimeTheme.typography.h6,
                            color = RarimeTheme.colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun ScanMRZStepContent(
    onClose: () -> Unit,
    onManual: (MRZInfo) -> Unit,
    cameraContent: @Composable () -> Unit
) {
    val manualInputSheetState = rememberAppSheetState(false)

    ScanPassportLayout(
        step = 1,
        title = stringResource(R.string.scan_your_passport_title),
        text = stringResource(R.string.scan_your_passport_text),
        onClose = onClose
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            cameraContent()

            Text(
                text = stringResource(R.string.scan_your_passport_hint),
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                modifier = Modifier.width(250.dp),
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))
                TertiaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    leftIcon = R.drawable.ic_pencil_simple_line,
                    onClick = { manualInputSheetState.show() },
                ) {
                    Text(
                        text = stringResource(id = R.string.scan_mrzstep_manual_input_show_btn).uppercase(),
                        style = RarimeTheme.typography.buttonMedium,
                        color = RarimeTheme.colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        AppBottomSheet(
            state = manualInputSheetState, isHeaderEnabled = false
        ) {
            ManualInputForm(modifier = Modifier.fillMaxWidth(), onConfirm = {
                onManual(it)
            }, onClose = { manualInputSheetState.hide() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualInputForm(
    modifier: Modifier = Modifier,
    onConfirm: (MRZInfo) -> Unit,
    onClose: () -> Unit,
) {
    val docIDFieldState = rememberAppTextFieldState(initialText = "")

    val birthDateFieldState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        yearRange = 1900..Calendar.getInstance().get(Calendar.YEAR)
    )

    val expiryDateFieldState =
        rememberDatePickerState()

    var tempMRZ by remember { mutableStateOf<MRZInfo?>(null) }
    LaunchedEffect(
        docIDFieldState.text,
        birthDateFieldState.selectedDateMillis,
        expiryDateFieldState.selectedDateMillis
    ) {
        try {
            tempMRZ = buildTempMrz(

                docIDFieldState.text, DateUtil.convertToDate(
                    birthDateFieldState.selectedDateMillis, "yyMMdd"
                ), DateUtil.convertToDate(
                    expiryDateFieldState.selectedDateMillis, "yyMMdd"
                )
            )
        } catch (e: Exception) {
            // TODO: Handle error
        }
    }

    Column(
        modifier = Modifier.then(modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.scan_mrzstep_manual_input_title),
                style = RarimeTheme.typography.h6,
                color = RarimeTheme.colors.textPrimary,
            )

            IconButton(onClick = { onClose() }) {
                AppIcon(
                    id = R.drawable.ic_close, tint = RarimeTheme.colors.textSecondary, size = 16.dp
                )
            }
        }

        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.scan_mrzstep_manual_input_subtitle),
                style = RarimeTheme.typography.subtitle4,
                color = RarimeTheme.colors.textSecondary,
            )

            AppTextField(
                state = docIDFieldState,
                placeholder = stringResource(id = R.string.scan_mrzstep_manual_input_doc_id_placeholder),
            )
            FieldTypeDatePicker(
                state = birthDateFieldState,
                placeholder = stringResource(id = R.string.scan_mrzstep_manual_input_birth_placeholder),
            )
            FieldTypeDatePicker(
                state = expiryDateFieldState,
                placeholder = stringResource(id = R.string.scan_mrzstep_manual_input_expiry_placeholder),
            )

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                text = stringResource(id = R.string.scan_mrzstep_manual_input_confirm_btn).uppercase(),
                enabled = tempMRZ?.let { isMrzValid(it) } ?: false,
                onClick = { onConfirm(tempMRZ!!) })

        }
    }
}

@Preview
@Composable
private fun ScanMRZStepPreview() {
    ScanMRZStepContent(
        onClose = {},
        onManual = {},
        cameraContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RarimeTheme.colors.componentPrimary)
                    .height(250.dp)
                    .padding(20.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.scan_mrzstep_permission_denied),
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ManualInputFormPreview() {
    ManualInputForm(onConfirm = {}, onClose = {})
}
