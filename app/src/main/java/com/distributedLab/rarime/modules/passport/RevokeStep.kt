package com.distributedLab.rarime.modules.passport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.passport.nfc.NfcViewModel
import com.distributedLab.rarime.modules.passport.nfc.ScanNFCPassportState
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.theme.RarimeTheme

@Composable
fun RevokeStep(
    modifier: Modifier = Modifier, onClose: () -> Unit, nfcViewModel: NfcViewModel = hiltViewModel(),
    challenge: ByteArray
) {

    val scanState by nfcViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        nfcViewModel.enableNFC.invoke()
    }

    val lifecycle = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { lifecycleOwner, event ->
            when (event) {
                Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> {
                    nfcViewModel.disableNFC.invoke()
                }

                else -> {}
            }
        }
        lifecycle.lifecycle.addObserver(observer)
        onDispose {
            lifecycle.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppIcon(
            id = R.drawable.ic_history, size = 100.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please scan your NFC card",
            fontSize = 18.sp,
            style = RarimeTheme.typography.h6,
            color = RarimeTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This is required to revoke your passport",
            fontSize = 14.sp,
            style = RarimeTheme.typography.body3,
            color = RarimeTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(80.dp))

        when (scanState) {

            ScanNFCPassportState.NOT_SCANNING -> {
                Text(text = "Scann your passport")
            }

            ScanNFCPassportState.SCANNING -> {
                Text(text = "Scanning")
            }

            ScanNFCPassportState.SCANNED -> {
                onClose.invoke()
                nfcViewModel.disableNFC.invoke()
            }

            ScanNFCPassportState.ERROR -> {
                Text(
                    text = "Error",
                    style = RarimeTheme.typography.body3,
                    color = RarimeTheme.colors.errorMain
                )
            }
        }


    }
}


@Preview(showBackground = true)
@Composable
private fun RevokeStepPreview() {
    RevokeStep(onClose = {}, challenge = ByteArray(1))
}