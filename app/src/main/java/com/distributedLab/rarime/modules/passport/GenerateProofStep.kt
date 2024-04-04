package com.distributedLab.rarime.modules.passport

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GenerateProofStep(onNext: () -> Unit, onClose: () -> Unit) {
    ScanPassportLayout(
        step = 4,
        title = "Generate Proof",
        text = "Generating proof of your Passport data",
        onClose = onClose
    )
}

@Preview
@Composable
private fun GenerateProofStepPreview() {
    GenerateProofStep(onNext = {}, onClose = {})
}
