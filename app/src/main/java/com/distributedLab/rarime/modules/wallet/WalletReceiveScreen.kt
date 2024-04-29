package com.distributedLab.rarime.modules.wallet

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.common.WalletViewModel
import com.distributedLab.rarime.ui.components.AppIcon
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.SecondaryTextButton
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.lightspark.composeqr.QrCodeColors
import com.lightspark.composeqr.QrCodeView
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun WalletReceiveScreen(
    walletViewModel: WalletViewModel = viewModel(LocalContext.current as ComponentActivity),
    onBack: () -> Unit = {}
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
        title = stringResource(R.string.wallet_receive_title),
        description = stringResource(R.string.wallet_receive_description),
        onBack = onBack
    ) {
        CardContainer(modifier = Modifier.padding(horizontal = 12.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    QrCodeView(
                        data = walletViewModel.address,
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
                        style = RarimeTheme.typography.subtitle4,
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
                            text = walletViewModel.address,
                            style = RarimeTheme.typography.body3,
                            color = RarimeTheme.colors.textPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        SecondaryTextButton(
                            leftIcon = if (isCopied) R.drawable.ic_check else R.drawable.ic_copy_simple,
                            onClick = {
                                clipboardManager.setText(AnnotatedString(walletViewModel.address))
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
    WalletReceiveScreen()
}
