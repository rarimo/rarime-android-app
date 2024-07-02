package com.rarilabs.rarime.modules.passportScan.nfc

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import org.jmrtd.lds.icao.MRZInfo


@Composable
fun ReadEDocStep(
    mrzInfo: MRZInfo,
    onNext: (eDocument: EDocument) -> Unit,
    onClose: () -> Unit,
    onError: () -> Unit,
    readEDocStepViewModel: ReadEDocStepViewModel = hiltViewModel(),
) {
    val state by readEDocStepViewModel.state.collectAsState()
    val errorMessageId by readEDocStepViewModel.errorMessageId.collectAsState()

    LaunchedEffect(Unit) {
        readEDocStepViewModel.startScanning(mrzInfo)
    }

    ScanPassportLayout(
        step = 2,
        title = stringResource(R.string.nfc_reader_title),
        text = stringResource(R.string.nfc_reader_text),
        onClose = {
            readEDocStepViewModel.resetState()
            onClose()
        }
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppAnimation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(2f)
                        .padding(horizontal = 10.dp),
                    id = R.raw.anim_passport_nfc
                )

                when (state) {
                    ScanNFCState.NOT_SCANNING -> {
                        Text(
                            text = stringResource(R.string.nfc_reader_hint),
                            style = RarimeTheme.typography.body3,
                            color = RarimeTheme.colors.textSecondary,
                            modifier = Modifier.width(250.dp),
                            textAlign = TextAlign.Center
                        )
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
                        readEDocStepViewModel.resetState()
                        onNext(readEDocStepViewModel.eDocument)
                    }

                    ScanNFCState.ERROR -> {
                        readEDocStepViewModel.resetState()

                        val context = LocalContext.current
                        Toast.makeText(
                            context,
                            stringResource(id = errorMessageId),
                            Toast.LENGTH_SHORT
                        ).show()

                        onError()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ReadNFCStepPreview() {
    val mrzInfo = MRZInfo(
        "P", "NNN", "", "", "", "NNN", "", Gender.UNSPECIFIED, "", ""
    )
    ReadEDocStep(mrzInfo, onNext = {}, onClose = {}, onError = {})
}