package com.rarilabs.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.tokens.Erc20Token
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.NumberUtil
import com.rarilabs.rarime.util.WalletUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject


data class SendValidationState(
    val isAddressValid: Boolean = true,
    val isAmountValid: Boolean = true,
    val addressError: String? = null,
    val amountError: String? = null
)


@HiltViewModel
class WalletSendViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    var selectedWalletAsset = walletManager.selectedWalletAsset
        private set

    // Fee estimation state
    private val _fee = MutableStateFlow<BigDecimal?>(null)
    val fee: StateFlow<BigDecimal?> = _fee

    private val _isFeeLoading = MutableStateFlow(false)
    val isFeeLoading: StateFlow<Boolean>
        get() = _isFeeLoading


    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean>
        get() = _isSubmitting

    private val _validationState = MutableStateFlow(SendValidationState())
    val validationState: StateFlow<SendValidationState> = _validationState

    fun validateSendFields(address: String, amount: String) {
        val isAddressValid = isValidAddress(address, selectedWalletAsset.value.userAddress)
        val isAmountValid = isValidAmount(amount, selectedWalletAsset.value.humanBalance())
        _validationState.value = SendValidationState(
            isAddressValid = isAddressValid,
            isAmountValid = isAmountValid,
            addressError = if (!isAddressValid && address.isNotEmpty()) "Address is not valid" else null,
            amountError = if (!isAmountValid && amount.isNotEmpty()) "Amount is not valid" else null
        )
    }

    private suspend fun getGasFee(humanAmount: String): BigDecimal? {
        val to = "0x0000000000000000000000000000000000000000"
        val asset = selectedWalletAsset.value
        val humanAmountNum = humanAmount.toBigDecimalOrNull() ?: return null

        if (humanAmountNum == BigDecimal.ZERO) {
            return null
        }

        val amount = (humanAmountNum * BigDecimal.TEN.pow(asset.token.decimals)).toBigInteger()
        val feeWei = asset.token.estimateTransferFee(
            asset.userAddress, to, amount
        )

        return NumberUtil.weiToEth(feeWei)


    }

    suspend fun estimateGasFee(humanAmount: String) {

        ErrorHandler.logDebug("WalletSendViewModel", "getGasFee")
        _isFeeLoading.value = true
        try {
            val fee = getGasFee(humanAmount)
            _fee.value = fee
        } catch (e: Exception) {
            _fee.value = null
        } finally {
            _isFeeLoading.value = false
        }
    }

    suspend fun submitSend(to: String, humanAmount: String) {
        _isSubmitting.value = true
        try {
            sendTokens(to, humanAmount)
            fetchBalance()
        } catch (e: Exception) {
            ErrorHandler.logError("Send Exception", "Error", e)
        } finally {
            _isSubmitting.value = false
        }
    }


    private suspend fun sendTokens(to: String, humanAmount: String) {
        withContext(Dispatchers.IO) {
            selectedWalletAsset.value.let { it ->
                val bigIntAmount: BigInteger = if (it.token !is Erc20Token) {
                    NumberUtil.toBigIntAmount(humanAmount.toDouble(), it.token.decimals)
                } else {
                    BigInteger(humanAmount)
                }
                val transaction = it.token.transfer(to, bigIntAmount)
                    //walletManager.insertTransaction(transaction)
                walletManager.loadBalances()

                it.loadBalance()

            }
        }
    }

    private fun isValidAddress(address: String, userAddress: String): Boolean {
        return WalletUtil.isValidAddressForSend(address, userAddress)
    }

    private fun isValidAmount(rawAmount: String, balance: BigDecimal): Boolean {
        return WalletUtil.isValidateAmountForSend(rawAmount, balance)
    }

    private suspend fun fetchBalance() {
        selectedWalletAsset.value.let {
            withContext(Dispatchers.IO) {
                it.loadBalance()
            }
        }
    }
}