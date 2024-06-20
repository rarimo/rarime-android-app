package com.rarilabs.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalletReceiveViewModel @Inject constructor(
    private val walletManager: WalletManager
) : ViewModel() {
    var selectedWalletAsset = walletManager.selectedWalletAsset
        private set

    val userAddress = selectedWalletAsset.value.userAddress
}