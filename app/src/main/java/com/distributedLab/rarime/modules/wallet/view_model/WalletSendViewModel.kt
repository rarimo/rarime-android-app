package com.distributedLab.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.tokens.RarimoToken
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class WalletSendViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    // FIXME: add multiple tokens support
    val _rmoAsset = MutableStateFlow(walletManager.walletAssets.value.find { it.token is RarimoToken })

    val rmoAsset: StateFlow<WalletAsset?>
        get() = _rmoAsset

    suspend fun sendTokens(to: String, amount: String) {
        withContext(Dispatchers.IO) {
            walletManager.sendTokens(to, amount) // FIXME: change to token transfer
            walletManager.loadBalances()
        }
    }

    suspend fun fetchBalance() {
        withContext(Dispatchers.IO) {
            walletManager.loadBalances()
        }
    }
}