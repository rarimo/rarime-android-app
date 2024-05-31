package com.distributedLab.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.tokens.RarimoToken
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WalletReceiveViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    val rarimoAddress = walletManager.rarimoAddress

    // FIXME: add multiple tokens support
    val _rmoAsset = MutableStateFlow(walletManager.walletAssets.value.find { it.token is RarimoToken })

    val rmoAsset: StateFlow<WalletAsset?>
        get() = _rmoAsset
}