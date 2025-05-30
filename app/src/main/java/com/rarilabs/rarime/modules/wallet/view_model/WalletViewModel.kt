package com.rarilabs.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {

    val walletAssets = walletManager.walletAssets

    val selectedWalletAsset = walletManager.selectedWalletAsset

    fun updateSelectedWalletAsset(walletAsset: WalletAsset) {
        walletManager.setSelectedWalletAsset(walletAsset)
    }

    suspend fun updateBalances() {
        walletManager.loadBalances()
    }
}