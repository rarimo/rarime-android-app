package com.distributedLab.rarime.modules.wallet.view_model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    // TODO: remove, use WalletAsset in select field
    var selectedAssetIndex = mutableStateOf(0)
        private set

    private val _selectedAddress = MutableStateFlow(walletManager.walletAssets.value[selectedAssetIndex.value])

    val selectedAsset: StateFlow<WalletAsset>
        get() = _selectedAddress.asStateFlow()

    fun updateSelectedAssetIndex(index: Int) {
        selectedAssetIndex.value = index
        _selectedAddress.value = walletManager.walletAssets.value[selectedAssetIndex.value]
    }
}