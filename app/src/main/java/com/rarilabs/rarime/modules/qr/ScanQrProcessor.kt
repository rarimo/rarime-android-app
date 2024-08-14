package com.rarilabs.rarime.modules.qr

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import qrscanner.QrScanner

@Composable
fun ScanQrProcessor(
    modifier: Modifier = Modifier,
    onCompletion: (String) -> Unit
) {
    QrScanner(
        modifier = Modifier.fillMaxSize().then(modifier),
        flashlightOn = false,
        openImagePicker = false,
        onCompletion = onCompletion,
        imagePickerHandler = {},
        onFailure = {}
    )
}