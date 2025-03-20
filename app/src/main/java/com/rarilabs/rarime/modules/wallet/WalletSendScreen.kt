package com.rarilabs.rarime.modules.wallet

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.tokens.PreviewerToken
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.modules.qr.ScanQrScreen
import com.rarilabs.rarime.modules.wallet.view_model.WalletSendViewModel
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppTextField
import com.rarilabs.rarime.ui.components.AppTextFieldNumberState
import com.rarilabs.rarime.ui.components.AppTextFieldState
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SecondaryTextButton
import com.rarilabs.rarime.ui.components.TxConfirmBottomSheet
import com.rarilabs.rarime.ui.components.VerticalDivider
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.components.rememberAppTextFieldNumberState
import com.rarilabs.rarime.ui.components.rememberAppTextFieldState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.NumberUtil
import com.rarilabs.rarime.util.WalletUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WalletSendScreen(
    onBack: () -> Unit,
    walletSendViewModel: WalletSendViewModel = hiltViewModel(),
) {
    var isQrCodeScannerOpen by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val humanAmountState = rememberAppTextFieldNumberState("")
    val addressState = rememberAppTextFieldState("")

    val selectedWalletAsset = walletSendViewModel.selectedWalletAsset.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    fun submit() {
        coroutineScope.launch {
            isSubmitting = true
            try {
                val a = walletSendViewModel.sendTokens(addressState.text, humanAmountState.text)
                a
                walletSendViewModel.fetchBalance()
            } catch (e: Exception) {
                ErrorHandler.logError(
                    "Error sending",
                    "Cant send token ${walletSendViewModel.selectedWalletAsset.value.token}",
                    e
                )
            }

            isSubmitting = false
        }
    }

    if (isQrCodeScannerOpen) {
        ScanQrScreen(
            onBack = { isQrCodeScannerOpen = false },
            onScan = {
                addressState.updateText(it)
                isQrCodeScannerOpen = false
            }
        )
    } else {
        WalletSendScreenContent(
            onBack = onBack,
            selectedWalletAsset = selectedWalletAsset.value,
            humanAmountState = humanAmountState,
            addressState = addressState,
            showQrCodeScanner = { isQrCodeScannerOpen = true },
            submit = { submit() },
        )
    }
}

@Composable
private fun WalletSendScreenContent(
    onBack: () -> Unit,
    selectedWalletAsset: WalletAsset,

    humanAmountState: AppTextFieldNumberState,
    addressState: AppTextFieldState,

    showQrCodeScanner: () -> Unit,

    submit: () -> Unit,

    isSubmitting: Boolean = false,
) {
    val confirmationSheetState = rememberAppSheetState()

    WalletRouteLayout(
        title = stringResource(R.string.wallet_send_title, selectedWalletAsset.token.symbol),
        description = stringResource(
            R.string.wallet_send_description, selectedWalletAsset.token.symbol
        ),
        onBack = onBack
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    AppTextField(
                        state = addressState,
                        label = stringResource(R.string.address_lbl),
                        placeholder = selectedWalletAsset.userAddress.substring(0, 7) + "...",
                        trailingItem = {
                            SecondaryTextButton(
                                leftIcon = R.drawable.ic_qr_code,
                                onClick = { showQrCodeScanner() }
                            )
                        },
                        enabled = !isSubmitting,
                    )
                    AppTextField(state = humanAmountState,
                        label = stringResource(R.string.amount_lbl),
                        enabled = !isSubmitting,
                        placeholder = stringResource(
                            R.string.amount_placeholder, selectedWalletAsset.token.symbol
                        ),
                        hint = {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.available_hint),
                                    style = RarimeTheme.typography.body5,
                                    color = RarimeTheme.colors.textSecondary
                                )
                                Text(
                                    text = "${selectedWalletAsset.humanBalance()} ${selectedWalletAsset.token.symbol}",
                                    style = RarimeTheme.typography.body5,
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
                                SecondaryTextButton(text = stringResource(R.string.max_btn),
                                    // TODO: mb to human string?
                                    onClick = {
                                        humanAmountState.updateText(
                                            selectedWalletAsset.humanBalance().toString()
                                        )
                                    })
                            }
                        })
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RarimeTheme.colors.backgroundPure)
                    .padding(top = 12.dp, bottom = 20.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.receiver_gets),
                        style = RarimeTheme.typography.body4,
                        color = RarimeTheme.colors.textSecondary
                    )
                    Text(
                        text = "${NumberUtil.formatAmount(humanAmountState.text.toDoubleOrNull() ?: 0.0)} ${selectedWalletAsset.token.symbol}",
                        style = RarimeTheme.typography.subtitle5,
                        color = RarimeTheme.colors.textPrimary
                    )
                }
                PrimaryButton(
                    text = stringResource(R.string.send_btn),
                    size = ButtonSize.Large,
                    modifier = Modifier.width(160.dp),
                    onClick = {
                        confirmationSheetState.show()
                    },
                    enabled = addressState.text.isNotEmpty() && humanAmountState.text.isNotEmpty() && !isSubmitting
                )
            }
        }

        if (addressState.text.isNotEmpty() && humanAmountState.text.isNotEmpty()) {
            TxConfirmBottomSheet(sheetState = confirmationSheetState, totalDetails = mapOf(
                "Address" to WalletUtil.formatAddress(addressState.text, 4, 4),
                "Send amount" to "${NumberUtil.formatAmount(humanAmountState.text.toDouble())} ${selectedWalletAsset.token.symbol}",
                "Fee" to "0 ${selectedWalletAsset.token.symbol}"
            ), onConfirm = {

                submit()

            })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WalletSendScreenContentPreview() {
    val scope = rememberCoroutineScope()

    var isSubmitting by remember { mutableStateOf(false) }

    fun submit() {
        scope.launch {
            isSubmitting = true

            delay(2000)

            isSubmitting = false
        }
    }

    WalletSendScreenContent(
        onBack = {},
        selectedWalletAsset = WalletAsset(
            "0xgqiuweyfgkafsdjnfksladnfajsdn", PreviewerToken("", "Preview", "PRW", 18)
        ),
        humanAmountState = rememberAppTextFieldNumberState(initialText = ""),
        addressState = rememberAppTextFieldState(initialText = ""),
        showQrCodeScanner = { },
        submit = { submit() },
        isSubmitting = isSubmitting,
    )
}
