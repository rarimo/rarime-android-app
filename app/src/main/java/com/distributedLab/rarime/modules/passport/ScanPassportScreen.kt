package com.distributedLab.rarime.modules.passport

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.distributedLab.rarime.modules.passport.models.EDocument
import org.jmrtd.lds.icao.MRZInfo

private enum class ScanPassportState {
    SCAN_MRZ, READ_NFC, SELECT_DATA, GENERATE_PROOF
}

@Composable
fun ScanPassportScreen(onClose: () -> Unit) {
    var state by remember { mutableStateOf(ScanPassportState.SCAN_MRZ) }
    var mrzData: MRZInfo? by remember { mutableStateOf(null) }

    var eDocument: EDocument? by remember { mutableStateOf(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            ScanPassportState.SCAN_MRZ -> {
                ScanMRZStep(
                    onNext = {
                        mrzData = it
                        state = ScanPassportState.READ_NFC
                    }, onClose = onClose
                )
            }

            ScanPassportState.READ_NFC -> {
                Log.e("TAG", mrzData!!.dateOfBirth)
                ReadNFCStep(
                    onNext = {
                        eDocument = it
                        state = ScanPassportState.SELECT_DATA
                    }, onClose = onClose, mrzInfo = mrzData!!
                )
            }

            ScanPassportState.SELECT_DATA -> {
                SelectDataStep(
                    onNext = { state = ScanPassportState.GENERATE_PROOF },
                    onClose = onClose,
                    eDocument = eDocument!!
                )
            }

            ScanPassportState.GENERATE_PROOF -> {
                GenerateProofStep(eDocument = eDocument!!, onClose = onClose)
            }
        }
    }
}

@Preview
@Composable
private fun ScanPassportScreenPreview() {
    ScanPassportScreen(onClose = {})
}