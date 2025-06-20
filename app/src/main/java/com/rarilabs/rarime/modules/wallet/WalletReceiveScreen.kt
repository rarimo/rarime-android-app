package com.rarilabs.rarime.modules.wallet

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.Gson
import com.lightspark.composeqr.QrCodeColors
import com.lightspark.composeqr.QrCodeView
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.tokens.PreviewerToken
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.modules.wallet.view_model.WalletReceiveViewModel
import com.rarilabs.rarime.ui.components.AppIcon
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.SecondaryTextButton
import com.rarilabs.rarime.ui.theme.RarimeTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun WalletReceiveScreen(
    onBack: () -> Unit = {},
    walletViewModel: WalletReceiveViewModel = hiltViewModel()
) {
    val selectedWalletAsset by walletViewModel.selectedWalletAsset.collectAsState()

    LaunchedEffect(selectedWalletAsset) {
        Log.i("selectedWalletAsset", Gson().toJson(selectedWalletAsset.userAddress))
    }

    WalletReceiveScreenContent(
        onBack = onBack,
        selectedWalletAsset = selectedWalletAsset
    )
}

@Composable
fun WalletReceiveScreenContent(
    onBack: () -> Unit,
    selectedWalletAsset: WalletAsset,
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(3.seconds)
            isCopied = false
        }
    }

    WalletRouteLayout(
        title = stringResource(R.string.wallet_receive_title, selectedWalletAsset.getTokenSymbol().uppercase()),
        description = stringResource(
            R.string.wallet_receive_description,
            selectedWalletAsset.getTokenSymbol().uppercase()
        ),
        onBack = onBack
    ) {
        CardContainer(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    QrCodeView(
                        data = selectedWalletAsset.userAddress,
                        colors = QrCodeColors(
                            background = RarimeTheme.colors.backgroundPure,
                            foreground = RarimeTheme.colors.textPrimary
                        ),
                        modifier = Modifier
                            .size(180.dp)
                            .border(7.dp, RarimeTheme.colors.textPrimary, RoundedCornerShape(12.dp))
                            .padding(20.dp)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(RarimeTheme.colors.backgroundPure)
                            .padding(4.dp)
                    ) {
                        AppIcon(
                            id = R.drawable.ic_rarime,
                            size = 36.dp,
                            tint = RarimeTheme.colors.textPrimary,
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.deposit_address_lbl),
                        style = RarimeTheme.typography.subtitle6,
                        color = RarimeTheme.colors.textPrimary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                RarimeTheme.colors.componentPrimary,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 14.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = selectedWalletAsset.userAddress,
                            style = RarimeTheme.typography.body4,
                            color = RarimeTheme.colors.textPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        SecondaryTextButton(
                            leftIcon = if (isCopied) R.drawable.ic_check else R.drawable.ic_copy_simple,
                            onClick = {
                                clipboardManager.setText(AnnotatedString(selectedWalletAsset.userAddress))
                                isCopied = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun WalletReceiveScreenPreview() {
    WalletReceiveScreenContent(
        onBack = {},
        selectedWalletAsset = WalletAsset(
            userAddress = "rarimo1000000000000000000000000000000000",
            token = PreviewerToken("RMO", "RARARARA", "RMO", 18)
        )
    )
}
