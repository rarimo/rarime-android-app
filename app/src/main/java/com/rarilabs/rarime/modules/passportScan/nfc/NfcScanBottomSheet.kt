package com.rarilabs.rarime.modules.passportScan.nfc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rarilabs.rarime.R
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppAnimation
import com.rarilabs.rarime.ui.components.AppSheetState
import com.rarilabs.rarime.ui.components.DottedProgressBar
import com.rarilabs.rarime.ui.components.SecondaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme

val currentStepDescription = mapOf(
    Pair(NfcScanStep.PREPARING, "Place your passport cover to the back of your phone"),
    Pair(NfcScanStep.SOD, "Scanning SOD"),
    Pair(NfcScanStep.DG1SCAN, "Scanning DG1"),
    Pair(NfcScanStep.DG2SCAN, "Scanning DG2"),
    Pair(NfcScanStep.DG15Scan, "Scanning DG15"),
)

@Composable
fun NfcScanBottomSheet(
    modifier: Modifier = Modifier,
    length: Int = currentStepDescription.size,
    currentStep: NfcScanStep,
    scanSheetState: AppSheetState,
    onStart: () -> Unit,
    onClose: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onStart.invoke()
    }

    LaunchedEffect(!scanSheetState.showSheet) {
        if (!scanSheetState.showSheet) {
            onClose()
        }
    }

    Surface(
        modifier = modifier,
        color = RarimeTheme.colors.backgroundPrimary
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Text(
                text = "Ready to Scan",
                style = RarimeTheme.typography.subtitle1,
                color = RarimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = currentStepDescription[currentStep]!!,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            DottedProgressBar(length = length - 1, currentStep = currentStep.step, offset = 8.dp)

            Spacer(modifier = Modifier.height(32.dp))


            AppAnimation(
                id = R.raw.anim_nfc_scan,
                modifier = Modifier.height(200.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            SecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
                onClick = onClose,
                text = "Cancel"
            )
        }
    }

}


@Preview
@Composable
private fun NfcScanBottomSheetPreview() {
    NfcScanBottomSheet(
        length = 5,
        currentStep = NfcScanStep.DG15Scan,
        onClose = {},
        onStart = {},
        scanSheetState = rememberAppSheetState()
    )
}