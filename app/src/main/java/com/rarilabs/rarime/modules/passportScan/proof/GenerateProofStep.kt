package com.rarilabs.rarime.modules.passportScan.proof

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.registration.PassportAlreadyRegisteredByOtherPK
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.CirclesLoader
import com.rarilabs.rarime.ui.components.ProcessingChip
import com.rarilabs.rarime.ui.components.ProcessingStatus
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.data.ZkProof
import kotlinx.coroutines.launch

enum class PassportProofState(val value: Int) {
    READING_DATA(0), APPLYING_ZERO_KNOWLEDGE(1), CREATING_CONFIDENTIAL_PROFILE(2), FINALIZING(3);
}

@Composable
fun GenerateProofStep(
    eDocument: EDocument,
    onClose: (zkp: ZkProof) -> Unit,
    proofViewModel: ProofViewModel = hiltViewModel(),
    onError: (e: Exception, regProof: ZkProof?) -> Unit,
    onAlreadyRegistered: (zkp: ZkProof) -> Unit,
) {
    val currentState by proofViewModel.state.collectAsState()
    val registrationProof = proofViewModel.regProof.collectAsState()

    var processingStatus by remember { mutableStateOf(ProcessingStatus.PROCESSING) }

    val downloadProgress by proofViewModel.progress.collectAsState()
    val downloadProgressVisibility by proofViewModel.progressVisibility.collectAsState()

    val view = LocalView.current

    val scope = rememberCoroutineScope()

    suspend fun joinRewardsProgram() {
        try {
            proofViewModel.joinRewardProgram(eDocument)
        } catch (e: Exception) {
            ErrorHandler.logError("joinRewardsProgram", e.toString(), e)
            onError(e, registrationProof.value)
        }
    }

    suspend fun lightRegistration() {
        try {
            proofViewModel.lightRegistration()
        } catch (e: Exception) {
            ErrorHandler.logError("lightRegistration", e.toString(), e)
            onError(e, registrationProof.value)
        }
    }

    LaunchedEffect(view) {
        view.keepScreenOn = true
    }

    LaunchedEffect(true) {
        scope.launch {

            try {
                proofViewModel.registerCertificate(eDocument)
            } catch (e: Exception) {
                ErrorHandler.logError("Cant register certificate", "Error: $e", e)
            }

            try {
                proofViewModel.registerByDocument()
                onClose(registrationProof.value!!)
            } catch (e: PassportAlreadyRegisteredByOtherPK) {
                onAlreadyRegistered.invoke(registrationProof.value!!)
                return@launch
            } catch (e: Exception) {
                ErrorHandler.logError(
                    "registerByDocument",
                    "Error during registerByDocument, trying to use light registration",
                    e
                )
                try {
                    lightRegistration()
                } catch (e: Exception) {
                    ErrorHandler.logError(
                        "lightRegistration",
                        "Error during lightRegistration",
                        e
                    )
                    if (!Constants.NOT_ALLOWED_COUNTRIES.contains(eDocument.personDetails?.nationality)) {
                        joinRewardsProgram()
                    }
                    onError(e, registrationProof.value)
                }
            }
        }
    }

    fun getItemStatus(item: PassportProofState): ProcessingStatus {
        val isSuccess =
            processingStatus == ProcessingStatus.SUCCESS || (currentState?.value
                ?: 0) + 1 > item.value
        if (isSuccess) return ProcessingStatus.SUCCESS
        if (processingStatus == ProcessingStatus.FAILURE) return ProcessingStatus.FAILURE
        return ProcessingStatus.PROCESSING
    }

    GenerateProofStepContent(
        processingStatus = processingStatus,
        getItemStatus = ::getItemStatus,
        downloadProgress = downloadProgress,
        downloadProgressVisibility = downloadProgressVisibility
    )
}

@Composable
private fun GenerateProofStepContent(
    processingStatus: ProcessingStatus,
    getItemStatus: (item: PassportProofState) -> ProcessingStatus,
    downloadProgress: Int,
    downloadProgressVisibility: Boolean
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(top = 80.dp, bottom = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            GeneralProcessingStatus(processingStatus)

            CardContainer {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PassportProofState.entries.forEach { item ->
                        ProcessingItem(
                            item = item, status = getItemStatus(item)
                        )
                    }

                }
            }

            if (downloadProgressVisibility) {
                Text(
                    text = stringResource(R.string.downloading_status, downloadProgress.toString()),
                    color = RarimeTheme.colors.textSecondary,
                    style = RarimeTheme.typography.body3
                )
            }
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
        verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.width(200.dp)
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
private fun ProcessingItem(item: PassportProofState, status: ProcessingStatus) {
    val label = when (item) {
        PassportProofState.READING_DATA -> stringResource(R.string.reading_data_step)
        PassportProofState.APPLYING_ZERO_KNOWLEDGE -> stringResource(R.string.applying_zero_knowledge_step)
        PassportProofState.CREATING_CONFIDENTIAL_PROFILE -> stringResource(R.string.creating_confidential_profile_step)
        PassportProofState.FINALIZING -> stringResource(R.string.finalizing_step)
    }

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
    GenerateProofStepContent(
        processingStatus = ProcessingStatus.PROCESSING,
        getItemStatus = { ProcessingStatus.PROCESSING },
        downloadProgress = 0,
        downloadProgressVisibility = true
    )
}
