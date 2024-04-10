package com.distributedLab.rarime.modules.passport

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.nfc.NfcViewModel
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.HorizontalDivider
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import net.sf.scuba.data.Gender
import org.jmrtd.lds.icao.MRZInfo

@Composable
fun ReadNFCStep(
    mrzInfo: MRZInfo,
    onNext: () -> Unit,
    onClose: () -> Unit,
    nfcViewModel: NfcViewModel = viewModel(LocalContext.current as ComponentActivity)
) {

    val isScanning by nfcViewModel.isScanning.collectAsState()
    nfcViewModel.enableNFC()
    nfcViewModel.setMRZ(mrzInfo)

    ScanPassportLayout(
        step = 2,
        title = stringResource(R.string.nfc_reader_title),
        text = stringResource(R.string.nfc_reader_text),
        onClose = onClose
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.passport_nfc),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                )
                Text(
                    text = stringResource(R.string.nfc_reader_hint),
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.textSecondary,
                    modifier = Modifier.width(250.dp),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                HorizontalDivider()
                if (isScanning) {
                    PrimaryButton(
                        text = stringResource(R.string.start_btn),
                        onClick = onNext,
                        size = ButtonSize.Large,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                    )
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
    ReadNFCStep(mrzInfo, onNext = {}, onClose = {})
}