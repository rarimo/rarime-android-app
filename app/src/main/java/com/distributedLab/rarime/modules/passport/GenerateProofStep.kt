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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.proof.ProofViewModel
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CirclesLoader
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.ProcessingChip
import com.distributedLab.rarime.ui.components.ProcessingStatus
import com.distributedLab.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.launch


@Composable
fun GenerateProofStep(
    eDocument: EDocument, onClose: () -> Unit, proofViewModel: ProofViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = null) {
        coroutineScope.launch {
            proofViewModel.generateProof(eDocument)
        }
    }

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
                ProcessingItem(
                    label = stringResource(R.string.document_class_model),
                    status = ProcessingStatus.SUCCESS
                )
                ProcessingItem(
                    label = stringResource(R.string.issuing_state_code),
                    status = ProcessingStatus.SUCCESS
                )
                ProcessingItem(
                    label = stringResource(R.string.document_number),
                    status = ProcessingStatus.SUCCESS
                )
                ProcessingItem(
                    label = stringResource(R.string.expiry_date), status = ProcessingStatus.SUCCESS
                )
                ProcessingItem(
                    label = stringResource(R.string.nationality), status = ProcessingStatus.SUCCESS
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            HorizontalDivider()
            PrimaryButton(
                text = stringResource(R.string.back_to_rewards_btn),
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
        }, label = ""
    )

    val iconColor by animateColorAsState(
        targetValue = when (status) {
            ProcessingStatus.PROCESSING -> RarimeTheme.colors.warningDark
            ProcessingStatus.SUCCESS -> RarimeTheme.colors.successDark
            ProcessingStatus.FAILURE -> RarimeTheme.colors.errorMain
        }, label = ""
    )

    val title = when (status) {
        ProcessingStatus.PROCESSING -> stringResource(R.string.processing_status_title)
        ProcessingStatus.SUCCESS -> stringResource(R.string.success_status_title)
        ProcessingStatus.FAILURE -> stringResource(R.string.failure_status_title)
    }

    val text = when (status) {
        ProcessingStatus.PROCESSING -> stringResource(R.string.processing_status_text)
        ProcessingStatus.SUCCESS -> stringResource(R.string.success_status_text)
        ProcessingStatus.FAILURE -> stringResource(R.string.failure_status_text)
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
        verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.width(150.dp)
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
    val eDocument = EDocument()
    GenerateProofStep(onClose = {}, eDocument = eDocument)
}
