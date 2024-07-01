package com.rarilabs.rarime.modules.passportScan

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.passportScan.camera.ScanMRZStep
import com.rarilabs.rarime.modules.passportScan.models.ScanPassportScreenViewModel
import com.rarilabs.rarime.modules.passportScan.nfc.ReadEDocStep
import com.rarilabs.rarime.modules.passportScan.nfc.RevocationStep
import com.rarilabs.rarime.modules.passportScan.proof.GenerateProofStep
import com.rarilabs.rarime.modules.passportScan.unsupportedPassports.NotAllowedPassportScreen
import com.rarilabs.rarime.modules.passportScan.unsupportedPassports.WaitlistPassportScreen
import com.rarilabs.rarime.ui.components.ConfirmationDialog
import com.rarilabs.rarime.util.Constants
import com.rarilabs.rarime.util.Constants.NOT_ALLOWED_COUNTRIES
import org.jmrtd.lds.icao.MRZInfo

private enum class ScanPassportState {
    SCAN_MRZ,
    READ_NFC,
    PASSPORT_DATA,
    GENERATE_PROOF,
    FINISH_PASSPORT_FLOW,
    UNSUPPORTED_PASSPORT,
    NOT_ALLOWED_PASSPORT,

    REVOCATION_PROCESS,
}

@Composable
fun ScanPassportScreen(
    onClose: () -> Unit,
    onClaim: () -> Unit,
    scanPassportScreenViewModel: ScanPassportScreenViewModel = hiltViewModel(),
) {
    val mainViewModel = LocalMainViewModel.current

    var state by remember { mutableStateOf(ScanPassportState.SCAN_MRZ) }
    var mrzData: MRZInfo? by remember { mutableStateOf(null) }

    val eDoc = scanPassportScreenViewModel.eDocument.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            ScanPassportState.SCAN_MRZ -> {
                ScanMRZStep(
                    onNext = {
                        mrzData = it
                        state = ScanPassportState.READ_NFC
                    },
                    onClose = onClose
                )
            }

            ScanPassportState.READ_NFC -> {
                Log.e("TAG", mrzData!!.dateOfBirth)
                ReadEDocStep(
                    onNext = {
                        scanPassportScreenViewModel.savePassport(it)

                        state = ScanPassportState.PASSPORT_DATA
                    },
                    onClose = onClose,
                    mrzInfo = mrzData!!
                )
            }

            ScanPassportState.PASSPORT_DATA -> {
                PassportDataStep(
                    onNext = {
                        state =
                            if (Constants.NOT_ALLOWED_COUNTRIES.contains(eDoc.value?.personDetails?.nationality)) {
                                ScanPassportState.NOT_ALLOWED_PASSPORT
                            } else {
                                ScanPassportState.GENERATE_PROOF
                            }
                    },
                    onClose = onClose,
                    eDocument = eDoc.value!!
                )
            }

            ScanPassportState.GENERATE_PROOF -> {
                GenerateProofStep(
                    onClose = {
                        scanPassportScreenViewModel.saveRegistrationProof(it)

                        if (!NOT_ALLOWED_COUNTRIES.contains(eDoc.value?.personDetails?.nationality)) {
                            state = ScanPassportState.FINISH_PASSPORT_FLOW
                        } else {
                            onClose.invoke()
                        }
                    },
                    eDocument = eDoc.value!!,
                    onError = { state = ScanPassportState.UNSUPPORTED_PASSPORT },
                    onAlreadyRegistered = {
                        scanPassportScreenViewModel.resetPassportState()

//                        mainViewModel.setModalContent {
//                            ConfirmationDialog(
//                                title = stringResource(R.string.you_have_already_registered),
//                                subtitle = stringResource(R.string.you_have_already_registered_offer),
//                                cancelButtonText = stringResource(id = R.string.you_have_already_registered_cancel),
//                                confirmButtonText = stringResource(id = R.string.you_have_already_registered_confirm),
//                                onConfirm = {
//                                    scanPassportScreenViewModel.saveRegistrationProof(it)
//                                    state = ScanPassportState.REVOCATION_PROCESS
//                                    mainViewModel.setModalVisibility(false)
//                                },
//                                onCancel = {
//                                    mainViewModel.setModalVisibility(false)
//                                    onClose.invoke()
//                                },
//                            )
//                        }
//                        mainViewModel.setModalVisibility(true)
                        onClose.invoke()
                    }
                )
            }

            ScanPassportState.NOT_ALLOWED_PASSPORT -> {
                NotAllowedPassportScreen(
                    eDocument = eDoc.value!!,
                    onClose = onClose
                ) {
                    state = ScanPassportState.GENERATE_PROOF
                }
            }

            ScanPassportState.UNSUPPORTED_PASSPORT -> {
                WaitlistPassportScreen(eDocument = eDoc.value!!, onClose = {
                    onClose.invoke()
                })
            }

            ScanPassportState.FINISH_PASSPORT_FLOW -> {
                onClaim.invoke()
            }

            ScanPassportState.REVOCATION_PROCESS -> {
                RevocationStep(
                    mrzData = mrzData!!,
                    onClose = {
                        scanPassportScreenViewModel.rejectRevocation()
                        onClose.invoke()
                    },
                    onNext = {
                        scanPassportScreenViewModel.finishRevocation()

                        if (!NOT_ALLOWED_COUNTRIES.contains(eDoc.value?.personDetails?.nationality)) {
                            state = ScanPassportState.FINISH_PASSPORT_FLOW
                        } else {
                            onClose.invoke()
                        }
                    },
                    onError = {
                        scanPassportScreenViewModel.finishRevocation()
                        state = ScanPassportState.UNSUPPORTED_PASSPORT
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ScanPassportScreenPreview() {
    ScanPassportScreen(onClose = {}, onClaim = {})
}