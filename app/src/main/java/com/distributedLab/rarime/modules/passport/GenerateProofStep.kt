package com.distributedLab.rarime.modules.passport

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.R
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CirclesLoader
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.ProcessingChip
import com.distributedLab.rarime.ui.components.ProcessingStatus
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
            GeneralProcessingStatus(ProcessingStatus.SUCCESS)
            HorizontalDivider()
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProcessingItem(label = "Document class model", status = ProcessingStatus.SUCCESS)
                ProcessingItem(label = "Issuing state code", status = ProcessingStatus.SUCCESS)
                ProcessingItem(label = "Document number", status = ProcessingStatus.SUCCESS)
                ProcessingItem(label = "Expiry date", status = ProcessingStatus.SUCCESS)
                ProcessingItem(label = "Nationality", status = ProcessingStatus.SUCCESS)
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
private fun GeneralProcessingStatus(status: ProcessingStatus) {
    val bgColor by animateColorAsState(
        targetValue = when (status) {
            ProcessingStatus.PROCESSING -> RarimeTheme.colors.warningLighter
            ProcessingStatus.SUCCESS -> RarimeTheme.colors.successLighter
            ProcessingStatus.FAILURE -> RarimeTheme.colors.errorLighter
        },
        label = ""
    )

    val iconColor by animateColorAsState(
        targetValue = when (status) {
            ProcessingStatus.PROCESSING -> RarimeTheme.colors.warningDark
            ProcessingStatus.SUCCESS -> RarimeTheme.colors.successDark
            ProcessingStatus.FAILURE -> RarimeTheme.colors.errorMain
        },
        label = ""
    )

    val title = when (status) {
        ProcessingStatus.PROCESSING -> "Please Wait..."
        ProcessingStatus.SUCCESS -> "All Done!"
        ProcessingStatus.FAILURE -> "Error"
    }

    val text = when (status) {
        ProcessingStatus.PROCESSING -> "Creating anonymized identity proof"
        ProcessingStatus.SUCCESS -> "Your passport proof is ready"
        ProcessingStatus.FAILURE -> "Please try again later"
    }

    Box(
        modifier = Modifier
            .background(bgColor, CircleShape)
            .padding(28.dp)
    ) {
        if (status == ProcessingStatus.PROCESSING) {
            CirclesLoader(size = 24.dp, color = iconColor)
        } else {
            AppIcon(
                id = if (status == ProcessingStatus.SUCCESS) R.drawable.ic_check else R.drawable.ic_close,
                size = 24.dp,
                tint = iconColor
            )
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.width(150.dp)
    ) {
        Text(
            text = title,
            style = RarimeTheme.typography.h6,
            color = RarimeTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = text,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProcessingItem(label: String, status: ProcessingStatus) {
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
        ProcessingChip(status = status)
    }
}

@Preview
@Composable
private fun GenerateProofStepPreview() {
    GenerateProofStep(onClose = {})
}
