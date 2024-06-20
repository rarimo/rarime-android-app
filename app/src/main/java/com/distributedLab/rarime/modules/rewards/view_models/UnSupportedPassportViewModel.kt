package com.distributedLab.rarime.modules.rewards.view_models

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.tokens.PointsToken
import com.distributedLab.rarime.manager.WalletAsset
import com.distributedLab.rarime.manager.WalletManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class UnSupportedPassportViewModel @Inject constructor(private val walletManager: WalletManager): ViewModel() {
    private fun getPointsWalletAsset(): WalletAsset? {
        return walletManager.walletAssets.value.find { it.token is PointsToken }
    }

    private var _pointsWalletAsset = MutableStateFlow(getPointsWalletAsset())


    val pointsWalletAsset: StateFlow<WalletAsset?>
        get() = _pointsWalletAsset.asStateFlow()
}