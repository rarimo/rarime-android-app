package com.distributedLab.rarime.modules.passport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.camera.CameraScanPassport
import com.distributedLab.rarime.ui.components.AppAlertDialog
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.jmrtd.lds.icao.MRZInfo

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanMRZStep(onNext: (MRZInfo) -> Unit, onClose: () -> Unit) {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)

    var isAlertVisible by remember {
        mutableStateOf(false)
    }

    ScanPassportLayout(
        step = 1,
        title = stringResource(R.string.scan_your_passport_title),
        text = stringResource(R.string.scan_your_passport_text),
        onClose = onClose
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (cameraPermissionState.status.isGranted) {
                CameraScanPassport(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(RarimeTheme.colors.baseBlack), onMrzDetected = { onNext(it) })
            } else {
                isAlertVisible = true
            }

            if (isAlertVisible) {
                AppAlertDialog(
                    title = stringResource(R.string.camera_permission_title),
                    text = stringResource(R.string.camera_permission_description),
                    onConfirm = {
                        cameraPermissionState.launchPermissionRequest()
                        isAlertVisible = false
                    },
                    onDismiss = { onClose() }
                )
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