package com.distributedLab.rarime.modules.passport

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

private enum class ScanPassportState {
    SCAN_MRZ,
    READ_NFC,
    SELECT_DATA,
    GENERATE_PROOF
}

@Composable
fun ScanPassportScreen(onClose: () -> Unit) {
    var state by remember { mutableStateOf(ScanPassportState.SCAN_MRZ) }

    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            ScanPassportState.SCAN_MRZ -> {
                ScanMRZStep(
                    onNext = { state = ScanPassportState.READ_NFC },
                    onClose = onClose
                )
            }

            ScanPassportState.READ_NFC -> {
                ReadNFCStep(
                    onNext = { state = ScanPassportState.SELECT_DATA },
                    onClose = onClose
                )
            }

            ScanPassportState.SELECT_DATA -> {
                SelectDataStep(
                    onNext = { state = ScanPassportState.GENERATE_PROOF },
                    onClose = onClose
                )
            }

            ScanPassportState.GENERATE_PROOF -> {
                GenerateProofStep(onClose = onClose)
            }
        }
    }
}

@Preview
@Composable
private fun ScanPassportScreenPreview() {
    ScanPassportScreen(onClose = {})
}