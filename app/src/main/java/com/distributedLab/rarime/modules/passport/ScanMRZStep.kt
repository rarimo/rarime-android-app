package com.distributedLab.rarime.modules.passport

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun ScanMRZStep(onNext: () -> Unit, onClose: () -> Unit) {
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
            // TODO: Add Passport scanner here
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(RarimeTheme.colors.baseBlack)
                    // TODO: Call onNext() when Passport is scanned
                    .clickable { onNext() }
            )
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