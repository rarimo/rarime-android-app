package com.distributedLab.rarime.modules.wallet.view_model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    var selectedAssetIndex = mutableStateOf(0)
        private set
    var selectedAsset = walletManager.walletAssets.value[selectedAssetIndex.value]
    fun updateSelectedAssetIndex(index: Int) {
        selectedAssetIndex.value = index
//        selectedAsset.value = walletManager.walletAssets.value.get(selectedAssetIndex.value)
    }


    val balance = walletManager.balance

    val transactions = walletManager.transactions
}