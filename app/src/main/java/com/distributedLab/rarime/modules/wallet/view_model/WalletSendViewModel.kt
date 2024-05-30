package com.distributedLab.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class WalletSendViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    val balance = walletManager.balance

    suspend fun sendTokens(to: String, amount: String) {
        withContext(Dispatchers.IO) {
            walletManager.sendTokens(to, amount)
            walletManager.refreshBalance()
        }
    }

    suspend fun fetchBalance() {
        withContext(Dispatchers.IO) {
            walletManager.refreshBalance()
        }
    }
}