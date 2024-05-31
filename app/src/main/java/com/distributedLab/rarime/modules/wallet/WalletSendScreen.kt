package com.distributedLab.rarime.modules.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.qr.ScanQrScreen
import com.distributedLab.rarime.modules.wallet.view_model.WalletSendViewModel
import com.distributedLab.rarime.ui.base.ButtonSize
import com.distributedLab.rarime.ui.components.AppTextField
import com.distributedLab.rarime.ui.components.CardContainer
import com.distributedLab.rarime.ui.components.PrimaryButton
import com.distributedLab.rarime.ui.components.SecondaryTextButton
import com.distributedLab.rarime.ui.components.VerticalDivider
import com.distributedLab.rarime.ui.components.rememberAppTextFieldState
import com.distributedLab.rarime.ui.theme.RarimeTheme
import com.distributedLab.rarime.util.NumberUtil
import kotlinx.coroutines.launch

@Composable
fun WalletSendScreen(
    onBack: () -> Unit,
    walletViewModel: WalletSendViewModel = hiltViewModel(),
) {
    var isQrCodeScannerOpen by remember { mutableStateOf(false) }
    val addressState = rememberAppTextFieldState("")
    val amountState = rememberAppTextFieldState("")

    val rmoAsset = walletViewModel.rmoAsset.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val amountToReceive = amountState.text.toDoubleOrNull() ?: 0.0

    if (isQrCodeScannerOpen) {
        ScanQrScreen(
            onBack = { isQrCodeScannerOpen = false },
            onScan = {
                addressState.updateText(it)
                isQrCodeScannerOpen = false
            }
        )
    } else {
        // TODO: add loader or empty view
        rmoAsset.value?.let {
            WalletRouteLayout(
                headerModifier = Modifier.padding(horizontal = 20.dp),
                title = stringResource(R.string.wallet_send_title, it.token.symbol),
                description = stringResource(R.string.wallet_send_description, it.token.symbol),
                onBack = onBack
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CardContainer (modifier = Modifier.padding(horizontal = 20.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            AppTextField(
                                state = addressState,
                                label = stringResource(R.string.address_lbl),
                                placeholder = "rarimo1...",
                                trailingItem = {
                                    SecondaryTextButton(
                                        leftIcon = R.drawable.ic_qr_code,
                                        onClick = { isQrCodeScannerOpen = true }
                                    )
                                }
                            )
                            AppTextField(
                                state = amountState,
                                label = stringResource(R.string.amount_lbl),
                                placeholder = stringResource(R.string.amount_placeholder, it.token.symbol),
                                hint = {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = stringResource(R.string.available_hint),
                                            style = RarimeTheme.typography.body4,
                                            color = RarimeTheme.colors.textSecondary
                                        )
                                        Text(
                                            text = "${it.balance.value.toDouble()} ${it.token.symbol}",
                                            style = RarimeTheme.typography.body4,
                                            color = RarimeTheme.colors.textPrimary
                                        )
                                    }
                                },
                                trailingItem = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier
                                            .width(64.dp)
                                            .height(20.dp)
                                    ) {
                                        VerticalDivider()
                                        SecondaryTextButton(
                                            text = stringResource(R.string.max_btn),
                                            // TODO: mb to human string?
                                            onClick = { amountState.updateText(it.balance.value.toString()) }
                                        )
                                    }
                                }
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RarimeTheme.colors.backgroundPure)
                            .padding(top = 12.dp, bottom = 20.dp)
                            .padding(horizontal = 20.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = stringResource(R.string.receiver_gets),
                                style = RarimeTheme.typography.body3,
                                color = RarimeTheme.colors.textSecondary
                            )
                            Text(
                                text = "${NumberUtil.formatAmount(amountToReceive)} ${rmoAsset.value?.token?.symbol}",
                                style = RarimeTheme.typography.subtitle3,
                                color = RarimeTheme.colors.textPrimary
                            )
                        }
                        PrimaryButton(
                            text = stringResource(R.string.send_btn),
                            size = ButtonSize.Large,
                            modifier = Modifier.width(160.dp),
                            onClick = {
                                coroutineScope.launch {
                                    walletViewModel.sendTokens(addressState.text, amountState.text)
                                    walletViewModel.fetchBalance()
                                }

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
private fun WalletSendScreenPreview() {
    WalletSendScreen(
        onBack = {},
        walletViewModel = hiltViewModel()
    )
}
