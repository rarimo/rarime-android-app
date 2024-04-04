package com.distributedLab.rarime.modules.passport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun GenerateProofStep(onClose: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(top = 70.dp, bottom = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(RarimeTheme.colors.successLighter, CircleShape)
                    .padding(28.dp)

            ) {
                AppIcon(
                    id = R.drawable.ic_check,
                    size = 24.dp,
                    tint = RarimeTheme.colors.successMain
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.width(150.dp)
            ) {
                Text(
                    text = "All Done!",
                    style = RarimeTheme.typography.h6,
                    color = RarimeTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Your passport proof is ready",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            HorizontalDivider()
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProcessingItem(label = "Document class model")
                ProcessingItem(label = "Issuing state code")
                ProcessingItem(label = "Document number")
                ProcessingItem(label = "Expiry date")
                ProcessingItem(label = "Nationality")
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            HorizontalDivider()
            PrimaryButton(
                text = "Back to Rewards",
                onClick = onClose,
                size = ButtonSize.Large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun ProcessingItem(label: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textPrimary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(RarimeTheme.colors.successLighter, CircleShape)
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            AppIcon(
                id = R.drawable.ic_check,
                size = 16.dp,
                tint = RarimeTheme.colors.successDark
            )
            Text(
                text = "DONE",
                style = RarimeTheme.typography.overline3,
                color = RarimeTheme.colors.successDark
            )
        }
    }
}

@Preview
@Composable
private fun GenerateProofStepPreview() {
    GenerateProofStep(onClose = {})
}
