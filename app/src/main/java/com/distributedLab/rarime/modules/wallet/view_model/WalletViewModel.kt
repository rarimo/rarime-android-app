package com.distributedLab.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.manager.WalletAsset
import com.distributedLab.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    var walletAssets = walletManager.walletAssets
        private set

    var selectedWalletAsset = walletManager.selectedWalletAsset
        private set

    fun updateSelectedWalletAsset(walletAsset: WalletAsset) {
        walletManager.setSelectedWalletAsset(walletAsset)
    }
}