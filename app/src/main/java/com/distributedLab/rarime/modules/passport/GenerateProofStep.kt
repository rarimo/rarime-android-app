package com.distributedLab.rarime.modules.passport

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.models.EDocument
import com.distributedLab.rarime.modules.passport.nfc.NfcViewModel
import com.distributedLab.rarime.modules.passport.nfc.ScanNFCPassportState
import com.distributedLab.rarime.modules.passport.proof.ProofViewModel
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CirclesLoader
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.ProcessingChip
import com.distributedLab.rarime.ui.components.ProcessingStatus
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.data.ZkProof

enum class PassportProofState(val value: Int) {
    READING_DATA(0), APPLYING_ZERO_KNOWLEDGE(1), CREATING_CONFIDENTIAL_PROFILE(2), FINALIZING(3);
}

@Composable
fun GenerateProofStep(
    eDocument: EDocument,
    onClose: (zkp: ZkProof) -> Unit,
    proofViewModel: ProofViewModel = hiltViewModel(),
    nfcViewModel: NfcViewModel
) {


    val currentState by proofViewModel.state.collectAsState()
    val processingStatus by remember { mutableStateOf(ProcessingStatus.PROCESSING) }
    val scanningStatus by nfcViewModel.state.collectAsState()
    var proof by remember {
        mutableStateOf<ZkProof?>(null)
    }
    var isModalVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(true) {

        proofViewModel.registerCertificate(eDocument)

        // Generate proof
        proof = proofViewModel.generateRegisterIdentityProof(eDocument)


        Log.i("OLD SIGNATURE", eDocument.aaSignature.toString())

        // Check revocation
        val revokeChallenge = proofViewModel.checkRevokation(proof!!)
        if (revokeChallenge != null) {
            isModalVisible = true
            nfcViewModel.startRevokeNfc(revokeChallenge)
        } else {
            // Proceed with registration and saving data
            proofViewModel.register(proof!!, eDocument, false)
            proofViewModel.saveData(proof!!, eDocument)
            onClose(proofViewModel.getRegistrationProof())
        }
    }

    var title by remember {
        mutableStateOf("This is required to revoke your passport")
    }

    if (isModalVisible) {
        ScanNfcModal(title)
    }

    LaunchedEffect(scanningStatus) {
        when (scanningStatus) {
            ScanNFCPassportState.NOT_SCANNING -> {
                title = "This is required to revoke your passport"
            }

            ScanNFCPassportState.SCANNING -> {
                title = "Scanning"
            }

            ScanNFCPassportState.SCANNED -> {
                isModalVisible = false
                Log.i("New SIGNATURE", eDocument.aaSignature.toString())
                val revoke = proofViewModel.revoke(eDocument, proof!!)
                revoke.toString()
                val res = proofViewModel.register(proof!!, eDocument, true)
                res.toString()
                proofViewModel.saveData(proof!!, eDocument)
                nfcViewModel.disableNFC.invoke()
                onClose(proofViewModel.getRegistrationProof())
            }

            ScanNFCPassportState.ERROR -> {

            }
        }
    }



    fun getItemStatus(item: PassportProofState): ProcessingStatus {
        val isSuccess =
            processingStatus == ProcessingStatus.SUCCESS || currentState.value > item.value
        if (isSuccess) return ProcessingStatus.SUCCESS
        if (processingStatus == ProcessingStatus.FAILURE) return ProcessingStatus.FAILURE
        return ProcessingStatus.PROCESSING
    }

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
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RarimeTheme.colors.backgroundOpacity, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                PassportProofState.entries.forEach { item ->
                    ProcessingItem(
                        item = item, status = getItemStatus(item)
                    )
                }
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


@Composable
fun ScanNfcModal(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = RarimeTheme.colors.baseBlack.copy(alpha = 0.1f))
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .background(RarimeTheme.colors.backgroundPure, RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.confetti),
                contentDescription = null,
                modifier = Modifier
                    .width(240.dp)
                    .height(170.dp)
                    .padding(top = 10.dp)
                    .align(Alignment.TopCenter)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {

                AppIcon(
                    id = R.drawable.ic_history,
                    size = 24.dp,
                    tint = RarimeTheme.colors.backgroundPure,
                    modifier = Modifier
                        .background(RarimeTheme.colors.successMain, CircleShape)
                        .padding(28.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Please scan your NFC card",
                        style = RarimeTheme.typography.h6,
                        color = RarimeTheme.colors.textPrimary
                    )
                    Text(
                        text = text,
                        style = RarimeTheme.typography.body2,
                        color = RarimeTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(240.dp)
                    )
                }
                HorizontalDivider()
            }
        }
    }
}
