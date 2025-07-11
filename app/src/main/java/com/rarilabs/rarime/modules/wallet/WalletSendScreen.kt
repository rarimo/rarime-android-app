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
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.WalletUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun WalletSendScreen(
    onBack: () -> Unit,
    walletSendViewModel: WalletSendViewModel = hiltViewModel(),
) {

    val mainViewModel = LocalMainViewModel.current

    val context = LocalContext.current

    var isQrCodeScannerOpen by remember { mutableStateOf(false) }

    val humanAmountState = rememberAppTextFieldNumberState("")
    val addressState = rememberAppTextFieldState("")

    val coroutineScope = rememberCoroutineScope()
    val selectedWalletAsset by walletSendViewModel.selectedWalletAsset.collectAsState()

    val validateState by walletSendViewModel.validationState.collectAsState()

    LaunchedEffect(addressState.text, humanAmountState.text) {
        walletSendViewModel.validateSendFields(addressState.text, humanAmountState.text)
        humanAmountState.updateErrorMessage(validateState.amountError ?: "")
        addressState.updateErrorMessage(validateState.addressError ?: "")
    }

    LaunchedEffect(humanAmountState.text, validateState.isAmountValid) {
        if (validateState.isAmountValid) {
            humanAmountState.updateErrorMessage("")
            walletSendViewModel.estimateGasFee(humanAmount = humanAmountState.text)
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

                        walletSendViewModel.submitSend(
                            to = addressState.text, humanAmount = amount
                        )

                        try {
                            mainViewModel.showSnackbar(
                                options = getSnackbarDefaultShowOptions(
                                    severity = SnackbarSeverity.Success,
                                    duration = SnackbarDuration.Long,
                                    title = context.getString(R.string.wallet_success_title),
                                    message = context.getString(R.string.wallet_success_message),
                                )
                            )
                        } catch (e: Exception) {
                            ErrorHandler.logError(
                                "WalletSendScreen",
                                "smth went wrong during sending showSnackbar",
                                e
                            )
                        }


                    } catch (e: Exception) {
                        ErrorHandler.logError(
                            "WalletSendScreen",
                            "smth went wrong during sending tokens",
                            e
                        )
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
            isSendEnabled = (validateState.isAmountValid && validateState.isAddressValid)
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
    isSendEnabled: Boolean,
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
        title = stringResource(
            R.string.wallet_send_title,
            selectedWalletAsset.getTokenSymbol().uppercase()
        ),
        description = stringResource(
            R.string.wallet_send_description, selectedWalletAsset.getTokenSymbol().uppercase()
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
                            R.string.amount_placeholder,
                            selectedWalletAsset.getTokenSymbol().uppercase()
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
                                    } ${selectedWalletAsset.getTokenSymbol().uppercase()}",
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
                                            selectedWalletAsset.humanBalance().stripTrailingZeros()
                                                .toPlainString()
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
                            text = "${calculateWithFee(humanAmountState.text)} ${
                                selectedWalletAsset.getTokenSymbol().uppercase()
                            }",
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
                    enabled = isSendEnabled && !isSubmitting && !isFeeLoading
                )
            }
        }

        if (WalletUtil.isValidAddress(address = addressState.text) && WalletUtil.isValidateAmountForSend(
                humanAmountState.text
            )
        ) {
            TxConfirmBottomSheet(
                sheetState = confirmationSheetState, totalDetails = mapOf(
                    "Address" to WalletUtil.formatAddress(addressState.text, 4, 4),
                    "Send amount" to "${humanAmountState.text} ${
                        selectedWalletAsset.getTokenSymbol().uppercase()
                    }",
                    "Fee" to "${fee?.toPlainString()} ${
                        selectedWalletAsset.getTokenSymbol().uppercase()
                    }"
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
            "0xgqiuweyfgkafsdjnfksladnfajsdn", PreviewerToken("", "Preview", "testtest", 18)
        ),
        humanAmountState = rememberAppTextFieldNumberState(initialText = ""),
        addressState = rememberAppTextFieldState(initialText = ""),
        showQrCodeScanner = { },
        submit = { submit() },
        isSubmitting = isSubmitting,
        fee = BigDecimal(0),
        isFeeLoading = false,
        isSendEnabled = false
    )
}
