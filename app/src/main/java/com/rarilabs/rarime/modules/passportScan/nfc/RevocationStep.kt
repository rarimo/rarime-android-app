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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.ScanNFCState
import com.rarilabs.rarime.modules.passportScan.ScanPassportLayout
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.RevocationStepViewModel
import com.rarilabs.rarime.ui.components.AppAnimation
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.data.ZkProof
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jmrtd.lds.icao.MRZInfo

@Composable
fun RevocationStep(
    mrzData: MRZInfo,
    eDocument: EDocument,
    registrationProof: ZkProof,
    onNext: (EDocument) -> Unit,
    onClose: () -> Unit,
    onError: () -> Unit,
    revocationStepViewModel: RevocationStepViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        revocationStepViewModel.startScanning(
            mrzData = mrzData,
            eDocument = eDocument,
            registrationProof = registrationProof,
        )
    }

    val state by revocationStepViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        revocationStepViewModel.startScanning(
            mrzData = mrzData,
            eDocument = eDocument,
            registrationProof = registrationProof,
        )
    }

    ScanPassportLayout(step = 2,
        title = stringResource(R.string.nfc_reader_title),
        text = stringResource(R.string.nfc_reader_text),
        onClose = {
            revocationStepViewModel.resetState()
            onClose()
        }) {
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
                        revocationStepViewModel.resetState()
                        onNext(revocationStepViewModel.eDocument)
                    }

                    ScanNFCState.ERROR -> {
                        revocationStepViewModel.resetState()

                        val context = LocalContext.current

                        Toast.makeText(context, R.string.nfc_reader_error, Toast.LENGTH_SHORT)
                            .show()

                        onError()
                    }
                }

            }
        }
    }
}