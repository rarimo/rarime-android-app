package com.rarilabs.rarime.modules.passportScan.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.rarilabs.rarime.ui.components.AppAlertDialog
import com.rarilabs.rarime.ui.theme.RarimeTheme
import org.jmrtd.lds.icao.MRZInfo

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanMRZStep(onNext: (MRZInfo) -> Unit, onClose: () -> Unit) {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)

    ScanPassportLayout(
        step = 1,
        title = stringResource(R.string.scan_your_passport_title),
        text = stringResource(R.string.scan_your_passport_text),
        onClose = onClose
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
                    CameraScanPassport(modifier = Modifier
                        .background(RarimeTheme.colors.baseBlack),
                        onMrzDetected = { onNext(it) })
                }

                else -> {
                    AppAlertDialog(
                        title = stringResource(R.string.camera_permission_title),
                        text = stringResource(R.string.camera_permission_description),
                        onConfirm = {
                            cameraPermissionState.launchPermissionRequest()
                        },
                        onDismiss = { onClose() }
                    )
                }
            }

            Text(
                text = stringResource(R.string.scan_your_passport_hint),
                style = RarimeTheme.typography.body3,
                color = RarimeTheme.colors.textSecondary,
                modifier = Modifier.width(250.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun ScanMRZStepPreview() {
    ScanMRZStep(onNext = {}, onClose = {})
}