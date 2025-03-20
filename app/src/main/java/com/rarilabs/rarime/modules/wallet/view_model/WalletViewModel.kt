package com.rarilabs.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    private var _walletAssets = MutableStateFlow(
        walletManager.walletAssets.value
        //.filter { it.token is RarimoToken }
    )

    val walletAssets: StateFlow<List<WalletAsset>>
        get() = _walletAssets.asStateFlow()

    var selectedWalletAsset = walletManager.selectedWalletAsset
        private set

    fun updateSelectedWalletAsset(walletAsset: WalletAsset) {
        walletManager.setSelectedWalletAsset(walletAsset)
    }

    suspend fun updateBalances() {
        walletManager.loadBalances()
    }
}