package com.distributedLab.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.WalletManager
import com.distributedLab.rarime.util.RarimoUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class SendState {
    AMOUNT_ZERO, AMOUNT_INSUFFICIENT, INVALID_ADDRESS, OK, SENDING, FINISHED, SENDING_ERROR, INVALID_AMOUNT
}

@HiltViewModel
class WalletSendViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    val balance = walletManager.balance

    private val _sendErrorState = MutableStateFlow(SendState.OK)

    val sendErrorState: StateFlow<SendState>
        get() = _sendErrorState.asStateFlow()

    suspend fun sendTokens(to: String, amount: String) {
        val validationResult = validateInputs(to, amount)
        try {
            if (validationResult) {
                withContext(Dispatchers.IO) {
                    _sendErrorState.value = SendState.SENDING
                    walletManager.sendTokens(to, amount)
                    walletManager.refreshBalance()
                    _sendErrorState.value = SendState.FINISHED
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _sendErrorState.value = SendState.SENDING_ERROR
        }


    }


    private fun validateInputs(to: String, _amount: String): Boolean {

        val amount = _amount.toDoubleOrNull()

        if (!RarimoUtils.isValidAddress(to)) {
            _sendErrorState.value = SendState.INVALID_ADDRESS
            return false
        }
        if (amount == null) {
            _sendErrorState.value = SendState.INVALID_AMOUNT
            return false
        }

        if (walletManager.balance.value < amount) {
            _sendErrorState.value = SendState.AMOUNT_INSUFFICIENT
            return false
        }

        if (amount == 0.0) {
            _sendErrorState.value = SendState.AMOUNT_ZERO
            return false
        }
        return true
    }

}