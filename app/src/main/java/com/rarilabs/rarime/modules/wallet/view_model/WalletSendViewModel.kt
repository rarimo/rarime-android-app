package com.rarilabs.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.tokens.Erc20Token
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.NumberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

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

    private val _sendError = MutableStateFlow<Throwable?>(null)
    val sendError: StateFlow<Throwable?> = _sendError


    private suspend fun getGasFee(humanAmount: String): BigDecimal? {
        val to = "0x0000000000000000000000000000000000000000"
        val asset = selectedWalletAsset.value
        val humanAmountNum = humanAmount.toBigDecimalOrNull() ?: return null


        return try {
            val amount = (humanAmountNum * asset.token.decimals.toBigDecimal()).toBigInteger()
            val feeWei = asset.token.estimateTransferFee(
                asset.userAddress, to, amount
            )

            NumberUtil.weiToEth(feeWei)

        } catch (e: Exception) {
            ErrorHandler.logError("WalletSendViewModel", "cant gas fee", e)
            null
        }
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
        _sendError.value = null
        try {
            sendTokens(to, humanAmount)
            fetchBalance()
        } catch (e: Exception) {
            _sendError.value = e
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
                it.token.transfer(to, bigIntAmount)
                it.loadBalance()

                walletManager.loadBalances()
            }
        }
    }

    private suspend fun fetchBalance() {
        selectedWalletAsset.value.let {
            withContext(Dispatchers.IO) {
                it.loadBalance()
            }
        }
    }
}