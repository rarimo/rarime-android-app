package com.rarilabs.rarime.modules.wallet

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.tokens.PreviewerToken
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.qr.ScanQrScreen
import com.rarilabs.rarime.modules.wallet.view_model.WalletSendViewModel
import com.rarilabs.rarime.ui.base.ButtonSize
import com.rarilabs.rarime.ui.components.AppTextField
import com.rarilabs.rarime.ui.components.AppTextFieldNumberState
import com.rarilabs.rarime.ui.components.AppTextFieldState
import com.rarilabs.rarime.ui.components.CardContainer
import com.rarilabs.rarime.ui.components.PrimaryButton
import com.rarilabs.rarime.ui.components.SecondaryTextButton
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.TxConfirmBottomSheet
import com.rarilabs.rarime.ui.components.VerticalDivider
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.ui.components.rememberAppTextFieldNumberState
import com.rarilabs.rarime.ui.components.rememberAppTextFieldState
import com.rarilabs.rarime.ui.theme.RarimeTheme
import com.rarilabs.rarime.util.NumberUtil
import com.rarilabs.rarime.util.WalletUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun WalletSendScreen(
    onBack: () -> Unit,
    walletSendViewModel: WalletSendViewModel = hiltViewModel(),
) {

    fun isValidAddress(address: String, userAddress: String): Boolean {
        val isNotOwn = address.lowercase() != userAddress.lowercase()
        val isValidFormat =
            address.startsWith("0x") && address.length == 42 && address.matches(Regex("^0x[0-9a-fA-F]{40}$"))
        return isNotOwn && isValidFormat
    }

    fun isValidAmount(rawAmount: String, balance: BigDecimal): Boolean {
        val amount = rawAmount.toBigDecimalOrNull() ?: return false
        if (amount <= BigDecimal.ZERO) return false
        val res = amount <= balance
        return res
    }

    var isError by remember {
        mutableStateOf(false)
    }

    val mainViewModel = LocalMainViewModel.current

    val context = LocalContext.current

    var isQrCodeScannerOpen by remember { mutableStateOf(false) }

    val humanAmountState = rememberAppTextFieldNumberState("")
    val addressState = rememberAppTextFieldState("")

    val coroutineScope = rememberCoroutineScope()
    val selectedWalletAsset by walletSendViewModel.selectedWalletAsset.collectAsState()

    LaunchedEffect(humanAmountState.text) {

        if (humanAmountState.text.isEmpty()) {
            return@LaunchedEffect
        }

        if (!isValidAmount(
                humanAmountState.text,
                selectedWalletAsset.humanBalance()
            )
        ) {
            isError = true
            humanAmountState.updateErrorMessage("amount is not Valid")
            return@LaunchedEffect
        } else {
            isError = false
            humanAmountState.updateErrorMessage("")
        }
        if (humanAmountState.text.toDoubleOrNull() == null) return@LaunchedEffect

        walletSendViewModel.estimateGasFee(humanAmountState.text)
    }

    LaunchedEffect(addressState.text) {
        if (addressState.text.isEmpty()) {
            return@LaunchedEffect
        }

        if (!isValidAddress(addressState.text, selectedWalletAsset.userAddress)) {
            addressState.updateErrorMessage("Address is not valid")
            isError = true
            return@LaunchedEffect
        } else {
            isError = false
            humanAmountState.updateErrorMessage("")
        }
    }

    val fee by walletSendViewModel.fee.collectAsState()
    val isSubmitting by walletSendViewModel.isSubmitting.collectAsState()
    val isFeeLoading by walletSendViewModel.isFeeLoading.collectAsState()

    if (isQrCodeScannerOpen) {
        ScanQrScreen(onBack = { isQrCodeScannerOpen = false }, onScan = {
            addressState.updateText(it)
            isQrCodeScannerOpen = false
        })
    } else {
        WalletSendScreenContent(
            onBack = onBack,
            selectedWalletAsset = selectedWalletAsset,
            humanAmountState = humanAmountState,
            addressState = addressState,
            showQrCodeScanner = { isQrCodeScannerOpen = true },
            submit = { amount ->
                coroutineScope.launch {
                    try {

                        Log.i("Sending", "Send to ${addressState.text} ${humanAmountState.text}")
                        walletSendViewModel.submitSend(
                            to = addressState.text, humanAmount = amount
                        )
                        Log.i("Sending", "Success")

                        mainViewModel.showSnackbar(
                            options = getSnackbarDefaultShowOptions(
                                severity = SnackbarSeverity.Success,
                                duration = SnackbarDuration.Long,
                                title = context.getString(R.string.wallet_success_title),
                                message = context.getString(R.string.wallet_success_message),
                            )
                        )
                    } catch (e: Exception) {
                        mainViewModel.showSnackbar(
                            options = getSnackbarDefaultShowOptions(
                                severity = SnackbarSeverity.Error,
                                duration = SnackbarDuration.Long,
                                title = context.getString(R.string.wallet_error_title),
                                message = context.getString(R.string.wallet_error_message),
                            )
                        )
                    }

                }
            },
            fee = fee,
            isSubmitting = isSubmitting,
            isFeeLoading = isFeeLoading,
            isError = isError
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
    submit: (amount: String) -> Unit,
    isSubmitting: Boolean = false,
    fee: BigDecimal?,
    isFeeLoading: Boolean,
    isError: Boolean,
) {
    val confirmationSheetState = rememberAppSheetState()


    fun calculateWithFee(rawAmount: String): String {
        val amount = rawAmount.toBigDecimalOrNull() ?: return "0.0"
        val feeValue = fee ?: BigDecimal.ZERO
        val balance = selectedWalletAsset.humanBalance()

        val total = amount + feeValue
        return if (total > balance) {
            val maxSendable = (balance - feeValue).coerceAtLeast(BigDecimal.ZERO)
            maxSendable.stripTrailingZeros().toPlainString()
        } else {
            amount.stripTrailingZeros().toPlainString()
        }
    }

    WalletRouteLayout(
        title = stringResource(R.string.wallet_send_title, selectedWalletAsset.token.symbol),
        description = stringResource(
            R.string.wallet_send_description, selectedWalletAsset.token.symbol
        ),
        onBack = onBack
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()
        ) {
            CardContainer {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    AppTextField(
                        state = addressState,
                        label = stringResource(R.string.address_lbl),
                        placeholder = selectedWalletAsset.userAddress.substring(0, 7) + "...",
                        trailingItem = {
                            SecondaryTextButton(
                                leftIcon = R.drawable.ic_qr_code, onClick = { showQrCodeScanner() })
                        },
                        enabled = !isSubmitting,
                    )
                    AppTextField(
                        state = humanAmountState,
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
                                    text = "${
                                        selectedWalletAsset.humanBalance().stripTrailingZeros()
                                            .toPlainString()
                                    } ${selectedWalletAsset.token.symbol}",
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
                                SecondaryTextButton(
                                    text = stringResource(R.string.max_btn), onClick = {
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
                    if (isFeeLoading) {
                        Text(
                            text = "Loading...",
                            style = RarimeTheme.typography.subtitle5,
                            color = RarimeTheme.colors.textPrimary
                        )
                    } else {
                        Text(
                            text = "${calculateWithFee(humanAmountState.text)} ${selectedWalletAsset.token.symbol}",
                            style = RarimeTheme.typography.subtitle5,
                            color = RarimeTheme.colors.textPrimary
                        )
                    }

                }
                PrimaryButton(
                    text = stringResource(R.string.send_btn),
                    size = ButtonSize.Large,
                    modifier = Modifier.width(160.dp),
                    onClick = {
                        confirmationSheetState.show()
                    },
                    enabled = !isError && !isSubmitting && !isFeeLoading
                )
            }
        }

        if (addressState.text.isNotEmpty() && humanAmountState.text.isNotEmpty()) {
            TxConfirmBottomSheet(
                sheetState = confirmationSheetState, totalDetails = mapOf(
                    "Address" to WalletUtil.formatAddress(addressState.text, 4, 4),
                    "Send amount" to "${NumberUtil.formatAmount(humanAmountState.text.toDouble())} ${selectedWalletAsset.token.symbol}",
                    "Fee" to "${fee?.toPlainString()} ${selectedWalletAsset.token.symbol}"
                ), onConfirm = {

                    submit(calculateWithFee(humanAmountState.text))
                    confirmationSheetState.hide()

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
        fee = BigDecimal(0),
        isFeeLoading = false,
        isError = false
    )
}
