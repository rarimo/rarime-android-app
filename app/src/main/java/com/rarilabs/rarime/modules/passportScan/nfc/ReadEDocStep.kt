package com.rarilabs.rarime.modules.passportScan.nfc

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.ScanNFCState
import com.rarilabs.rarime.modules.passportScan.ScanPassportLayout
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.ReadEDocStepViewModel
import com.rarilabs.rarime.ui.components.AppAnimation
import com.rarilabs.rarime.ui.theme.RarimeTheme
import net.sf.scuba.data.Gender
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

    LaunchedEffect(Unit) {
        readEDocStepViewModel.startScanning(mrzInfo)
    }

    fun handleScanPassportLayoutClose() {
        readEDocStepViewModel.resetState()
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

            val errorMessage = when(scanExceptionInstance.value) {
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
    )
}

@Composable
private fun ReadEDocStepContent(
    handleScanPassportLayoutClose: () -> Unit,
    handleScanPassportLayoutScanned: () -> Unit,
    handleScanPassportLayoutError: @Composable () -> Unit,
    state: ScanNFCState,
) {
    fun getNfcAnimation(): Int {
        return R.raw.anim_passport_nfc
    }

    ScanPassportLayout(
        step = 2,
        title = stringResource(R.string.nfc_reader_title),
        text = stringResource(R.string.nfc_reader_text),
        onClose = { handleScanPassportLayoutClose() }
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 100.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(64.dp),
                ) {
                    getNfcAnimation()?.let {
                        AppAnimation(
                            modifier = Modifier
                                .size(240.dp)
                                .zIndex(2f),
                            id = it,
                        )
                    }

                    when (state) {
                        ScanNFCState.NOT_SCANNING -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.nfc_reader_hint_1),
                                    style = RarimeTheme.typography.body3,
                                    color = RarimeTheme.colors.textSecondary,
                                    modifier = Modifier.width(250.dp),
                                    textAlign = TextAlign.Left
                                )
                                Text(
                                    text = stringResource(R.string.nfc_reader_hint_2),
                                    style = RarimeTheme.typography.body3,
                                    color = RarimeTheme.colors.textSecondary,
                                    modifier = Modifier.width(250.dp),
                                    textAlign = TextAlign.Left
                                )
                            }
                        }

                        ScanNFCState.SCANNING -> {
                            Text(
                                text = stringResource(R.string.nfc_reader_scanning),
                                style = RarimeTheme.typography.body3,
                                color = RarimeTheme.colors.textSecondary,
                                modifier = Modifier.width(250.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        ScanNFCState.SCANNED -> {
                            handleScanPassportLayoutScanned()
                        }

                        ScanNFCState.ERROR -> {
                            handleScanPassportLayoutError()
                        }
                    }
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
    )
}