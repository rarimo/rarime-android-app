package com.distributedLab.rarime.modules.wallet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.components.CardContainer

@Composable
fun WalletReceiveScreen(onBack: () -> Unit = {}) {
    WalletRouteLayout(
        title = "Receive RMO",
        description = "You can use the QR code or the wallet address to deposit the RMO token to your account",
        onBack = onBack
    ) {
        CardContainer(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Receive Content")
        }
    }
}

@Preview
@Composable
private fun WalletReceiveScreenPreview() {
    WalletReceiveScreen()
}
