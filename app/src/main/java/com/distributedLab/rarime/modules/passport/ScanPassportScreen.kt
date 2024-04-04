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
    ScanMRZ,
    ReadNFC,
    SelectData,
    GenerateProof
}

@Composable
fun ScanPassportScreen(onClose: () -> Unit) {
    var state by remember { mutableStateOf(ScanPassportState.ScanMRZ) }

    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            ScanPassportState.ScanMRZ -> {
                ScanMRZStep(
                    onNext = { state = ScanPassportState.ReadNFC },
                    onClose = onClose
                )
            }

            ScanPassportState.ReadNFC -> {
                ReadNFCStep(
                    onNext = { state = ScanPassportState.SelectData },
                    onClose = onClose
                )
            }

            ScanPassportState.SelectData -> {
                SelectDataStep(
                    onNext = { state = ScanPassportState.GenerateProof },
                    onClose = onClose
                )
            }

            ScanPassportState.GenerateProof -> {
                GenerateProofStep(
                    onNext = { onClose() },
                    onClose = onClose
                )
            }
        }
    }
}

@Composable
fun GenerateProofStep(onNext: () -> Unit, onClose: () -> Unit) {
    ScanPassportLayout(
        step = 4,
        title = "Generate Proof",
        text = "Generating proof of your Passport data",
        onClose = onClose
    )
}

@Composable
fun SelectDataStep(onNext: () -> Unit, onClose: () -> Unit) {
    ScanPassportLayout(
        step = 3,
        title = "Select Data",
        text = "Select data to share",
        onClose = onClose
    )
}

@Preview
@Composable
private fun ScanPassportScreenPreview() {
    ScanPassportScreen(onClose = {})
}