package com.distributedLab.rarime.modules.wallet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.distributedLab.rarime.ui.components.CardContainer

@Composable
fun WalletSendScreen(onBack: () -> Unit) {
    WalletRouteLayout(
        title = "Send RMO",
        description = "Withdraw the RMO token",
        onBack = onBack
    ) {
        CardContainer(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Send Content")
        }
    }
}

@Preview
@Composable
private fun WalletSendScreenPreview() {
    WalletSendScreen {}
}
