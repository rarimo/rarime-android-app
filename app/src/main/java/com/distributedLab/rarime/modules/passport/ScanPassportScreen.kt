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
import com.distributedLab.rarime.util.Constants
import com.distributedLab.rarime.util.data.ZkProof
import org.jmrtd.lds.icao.MRZInfo

private enum class ScanPassportState {
    SCAN_MRZ, READ_NFC, PASSPORT_DATA, GENERATE_PROOF, CLAIM_TOKENS, UNSUPPORTED_PASSPORT, NOT_ALLOWED_PASSPORT
}

@Composable
fun ScanPassportScreen(
    onClose: () -> Unit
) {
    var state by remember { mutableStateOf(ScanPassportState.SCAN_MRZ) }
    var mrzData: MRZInfo? by remember { mutableStateOf(null) }
    var eDocument: EDocument? by remember { mutableStateOf(null) }
    var registrationProof: ZkProof? by remember { mutableStateOf(null) }

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
                        state = ScanPassportState.PASSPORT_DATA
                    }, onClose = onClose, mrzInfo = mrzData!!
                )
            }

            ScanPassportState.PASSPORT_DATA -> {
                PassportDataStep(
                    onNext = {
                        state =
                            if (Constants.NOT_ALLOWED_COUNTRIES.contains(eDocument?.personDetails?.issuerAuthority)) {
                                ScanPassportState.NOT_ALLOWED_PASSPORT
                            } else {
                                ScanPassportState.GENERATE_PROOF
                            }
                    }, onClose = onClose, eDocument = eDocument!!
                )
            }

            ScanPassportState.GENERATE_PROOF -> {
                GenerateProofStep(onClose = {
                    registrationProof = it
                    if (eDocument?.personDetails?.issuerAuthority == "UKR") {
                        state = ScanPassportState.CLAIM_TOKENS
                    } else {
                        onClose.invoke()
                    }

                }, eDocument = eDocument!!, onError = {
                    state = ScanPassportState.UNSUPPORTED_PASSPORT
                })
            }

            ScanPassportState.NOT_ALLOWED_PASSPORT -> {
                NotAllowedPassportScreen(eDocument = eDocument!!, onClose = onClose) {
                    state = ScanPassportState.GENERATE_PROOF
                }
            }

            ScanPassportState.UNSUPPORTED_PASSPORT -> {
                WaitlistPassportScreen(eDocument = eDocument!!) {
                    onClose.invoke()
                }
            }


            ScanPassportState.CLAIM_TOKENS -> {
                ClaimTokensStep(
                    onFinish = onClose
                )
            }
        }
    }
}

@Preview
@Composable
private fun ScanPassportScreenPreview() {
    ScanPassportScreen(onClose = {})
}