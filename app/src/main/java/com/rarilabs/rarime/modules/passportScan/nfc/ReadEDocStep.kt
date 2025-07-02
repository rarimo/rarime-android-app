package com.rarilabs.rarime.modules.passportScan.nfc

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.ScanNFCState
import com.rarilabs.rarime.modules.passportScan.ScanPassportLayout
import com.rarilabs.rarime.modules.passportScan.components.ScanGuidesTrigger
import com.rarilabs.rarime.modules.passportScan.components.SpecificPassportGuide
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.ReadEDocStepViewModel
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppAnimation
import com.rarilabs.rarime.ui.components.AppBottomSheet
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import okio.IOException
import org.jmrtd.lds.icao.MRZInfo


@Composable
fun ReadEDocStep(
    mrzInfo: MRZInfo,
    onNext: (eDocument: EDocument) -> Unit,
    onClose: () -> Unit,
    onError: (e: Exception) -> Unit,
    readEDocStepViewModel: ReadEDocStepViewModel = hiltViewModel(),
) {
    val state by readEDocStepViewModel.state.collectAsState()
    val scanExceptionInstance = readEDocStepViewModel.scanExceptionInstance.collectAsState()

    val currentStep by readEDocStepViewModel.currentNfcScanStep.collectAsState()

    val hintType = remember {
        when (mrzInfo.nationality) {
            "USA" -> SpecificPassportGuide.USA
            else -> SpecificPassportGuide.Other
        }

    }

    fun handleScanPassportLayoutClose() {
        readEDocStepViewModel.resetState()
        readEDocStepViewModel.resetNfcScanStep()
        onClose()
    }

    fun handleScanPassportLayoutScanned() {
        readEDocStepViewModel.resetState()
        onNext(readEDocStepViewModel.eDocument)
    }

    @Composable
    fun handleScanPassportLayoutError() {
        scanExceptionInstance.value?.let {
            readEDocStepViewModel.resetState()
            readEDocStepViewModel.resetNfcScanStep()
            val errorMessage = when (scanExceptionInstance.value) {
                is IOException -> stringResource(id = R.string.nfc_error_interrupt)
                else -> stringResource(id = R.string.nfc_error_unknown)
            }

            val context = LocalContext.current
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_SHORT
            ).show()

            onError(scanExceptionInstance.value!!)
        }
    }

    ReadEDocStepContent(
        handleScanPassportLayoutClose = { handleScanPassportLayoutClose() },
        handleScanPassportLayoutScanned = { handleScanPassportLayoutScanned() },
        handleScanPassportLayoutError = { handleScanPassportLayoutError() },
        state = state,
        startScanning = { readEDocStepViewModel.startScanning(mrzInfo) },
        stopScanning = { readEDocStepViewModel.resetState() },
        currentNfcScanStep = currentStep,
        resetNFCScanState = { readEDocStepViewModel.resetNfcScanStep() },
        hintType = hintType
    )
}

@Composable
private fun ReadEDocStepContent(
    handleScanPassportLayoutClose: () -> Unit,
    handleScanPassportLayoutScanned: () -> Unit,
    handleScanPassportLayoutError: @Composable () -> Unit,
    currentNfcScanStep: NfcScanStep,
    startScanning: () -> Unit,
    stopScanning: () -> Unit,
    state: ScanNFCState,
    resetNFCScanState: () -> Unit,
    hintType: SpecificPassportGuide
) {
    val scanSheetState = rememberAppSheetState(showSheet = false)


    AppBottomSheet(state = scanSheetState) {
        NfcScanBottomSheet(
            currentStep = currentNfcScanStep,
            onStart = startScanning,
            scanSheetState = scanSheetState,
            onClose = { scanSheetState.hide(); resetNFCScanState();stopScanning() }
        )
    }


    ScanPassportLayout(
        step = 2,
        title = stringResource(R.string.nfc_reader_title),
        text = stringResource(R.string.nfc_reader_text),
        onClose = { handleScanPassportLayoutClose() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppAnimation(
                        modifier = Modifier
                            .scale(1.4f)
                            .size(240.dp),
                        id = R.raw.anim_passport_nfc,
                    )

                    when (state) {
                        ScanNFCState.NOT_SCANNING -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.nfc_reader_hint_1),
                                    style = RarimeTheme.typography.body4,
                                    color = RarimeTheme.colors.textSecondary,
                                    modifier = Modifier.width(250.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        ScanNFCState.SCANNING -> {
//                            Text(
//                                text = stringResource(R.string.nfc_reader_scanning),
//                                style = RarimeTheme.typography.body4,
//                                color = RarimeTheme.colors.textSecondary,
//                                modifier = Modifier.width(250.dp),
//                                textAlign = TextAlign.Center
//                            )
                        }

                        ScanNFCState.SCANNED -> {
                            handleScanPassportLayoutScanned()
                        }

                        ScanNFCState.ERROR -> {
                            handleScanPassportLayoutError()
                        }
                    }

                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RarimeTheme.colors.backgroundPure)
                        .padding(bottom = 20.dp)
                        .padding(horizontal = 20.dp)
                ) {

                    ScanGuidesTrigger(
                        type = hintType,
                    )
                    PrimaryButton(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth(),
                        onClick = { scanSheetState.show() },
                        size = ButtonSize.Large,
                        text = stringResource(R.string.scan)
                    )
                }
            }
        }

    }

}

@Preview
@Composable
private fun ReadEDocStepContentPreview() {
//    val mrzInfo = MRZInfo(
//        "P", "NNN", "", "", "", "NNN", "", Gender.UNSPECIFIED, "", ""
//    )
    ReadEDocStepContent(
        handleScanPassportLayoutClose = {},
        handleScanPassportLayoutScanned = {},
        handleScanPassportLayoutError = {},
        state = ScanNFCState.NOT_SCANNING,
        currentNfcScanStep = NfcScanStep.PREPARING,
        stopScanning = {},
        startScanning = {},
        resetNFCScanState = {},
        hintType = SpecificPassportGuide.Other
    )
}