package com.rarilabs.rarime.modules.passportScan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.BuildConfig
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.passportScan.camera.ScanMRZStep
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.modules.passportScan.models.ScanPassportScreenViewModel
import com.rarilabs.rarime.modules.passportScan.nfc.ReadEDocStep
import com.rarilabs.rarime.modules.passportScan.nfc.RevocationStep
import com.rarilabs.rarime.modules.passportScan.unsupportedPassports.NotAllowedPassportScreen
import com.rarilabs.rarime.modules.passportScan.unsupportedPassports.WaitlistPassportScreen
import com.rarilabs.rarime.util.Constants.NOT_ALLOWED_COUNTRIES
import com.rarilabs.rarime.util.ErrorHandler
import org.jmrtd.lds.icao.MRZInfo

enum class ScanPassportState {
    SCAN_MRZ, READ_NFC, PASSPORT_DATA, GENERATE_PROOF, FINISH_PASSPORT_FLOW, UNSUPPORTED_PASSPORT, NOT_ALLOWED_PASSPORT,
    REVOCATION_PROCESS,
    GET_IN_TOUCH,
}

@Composable
fun ScanPassportScreen(
    onClose: () -> Unit,
    onClaim: () -> Unit,
    scanPassportScreenViewModel: ScanPassportScreenViewModel = hiltViewModel(),
    initialEDocument: EDocument? = if (BuildConfig.isTestnet) scanPassportScreenViewModel.eDocument.value else null,
    innerPaddings: Map<ScreenInsets, Number> = mapOf(),
    setVisibilityOfBottomBar: (Boolean) -> Unit

) {
    val context = LocalContext.current
    val mainViewModel = LocalMainViewModel.current

    var state by remember { mutableStateOf(ScanPassportState.SCAN_MRZ) }
    var mrzData: MRZInfo? by remember { mutableStateOf(null) }

    var nfcAttempts by remember { mutableStateOf(0) }

    val balance by scanPassportScreenViewModel.pointsToken.collectAsState()
    val eDoc by scanPassportScreenViewModel.eDocument.collectAsState()

    var isAlreadyVerified by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        setVisibilityOfBottomBar(false)
    }

    LaunchedEffect(Unit) {
        if (initialEDocument != null) {
            scanPassportScreenViewModel.setPassportTEMP(initialEDocument)
        }
    }

    LaunchedEffect(Unit) {
        isAlreadyVerified = scanPassportScreenViewModel.isVerified()
    }

    fun handleNFCError(e: Exception) {
        ErrorHandler.logError("NFC error", e.toString(), e)

        nfcAttempts++

        if (nfcAttempts >= 3) {
            state = ScanPassportState.GET_IN_TOUCH
        } else {
            state = ScanPassportState.SCAN_MRZ
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = innerPaddings[ScreenInsets.BOTTOM]!!.toInt().dp,
                top = innerPaddings[ScreenInsets.TOP]!!.toInt().dp
            )
    ) {
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
                ReadEDocStep(
                    onNext = {
                        scanPassportScreenViewModel.setPassportTEMP(it)
                        scanPassportScreenViewModel.savePassport()
                        setVisibilityOfBottomBar(true)
                        //onClose.invoke()
                    },
                    onClose = {
                        onClose.invoke()
                    },
                    onError = {
                        handleNFCError(it)
                    },
                    mrzInfo = mrzData!!
                )
            }

            ScanPassportState.PASSPORT_DATA -> {
                PassportDataStep(
                    onNext = {
                        state = ScanPassportState.GENERATE_PROOF

                    },
                    onClose = {
                        onClose()
                        scanPassportScreenViewModel.resetPassportState()
                    },
                    eDocument = eDoc ?: throw IllegalStateException("No document")
                )
            }

            ScanPassportState.GENERATE_PROOF -> {}

            ScanPassportState.NOT_ALLOWED_PASSPORT -> {
                NotAllowedPassportScreen(
                    eDocument = eDoc ?: throw IllegalStateException("No Document"),
                    onClose = onClose
                ) {
                    state = ScanPassportState.GENERATE_PROOF
                }
            }

            ScanPassportState.UNSUPPORTED_PASSPORT -> {
                WaitlistPassportScreen(
                    eDocument = eDoc ?: throw IllegalStateException("No Document"),
                    onClose = {
                        scanPassportScreenViewModel.savePassport()
                        onClose.invoke()
                    }
                )
            }

            ScanPassportState.FINISH_PASSPORT_FLOW -> {
                if (balance?.balanceDetails == null) {
                    onClose.invoke()
                    scanPassportScreenViewModel.savePassport()
                } else {
                    onClaim.invoke()
                }
            }

            ScanPassportState.REVOCATION_PROCESS -> {
                RevocationStep(mrzData = mrzData!!, onClose = {
                    scanPassportScreenViewModel.rejectRevocation()
                    onClose.invoke()
                }, onNext = {
                    scanPassportScreenViewModel.finishRevocation()

                    if (!NOT_ALLOWED_COUNTRIES.contains(eDoc?.personDetails?.nationality)) {
                        state = ScanPassportState.FINISH_PASSPORT_FLOW
                    } else {
                        onClose.invoke()
                    }
                }, onError = {
                    scanPassportScreenViewModel.finishRevocation()
                    state = ScanPassportState.UNSUPPORTED_PASSPORT
                })
            }

            ScanPassportState.GET_IN_TOUCH -> {
                GetInTouchScreen(
                    eDoc = eDoc,
                    onClose = {
                        scanPassportScreenViewModel.resetPassportState()
                        onClose.invoke()
                    },
                    onSent = {
                        scanPassportScreenViewModel.resetPassportState()
                        onClose.invoke()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ScanPassportScreenPreview() {
    ScanPassportScreen(onClose = {}, onClaim = {}, setVisibilityOfBottomBar = {})
}