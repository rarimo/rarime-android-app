package com.rarilabs.rarime.modules.rewards.view_models

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.tokens.PointsToken
import com.rarilabs.rarime.data.tokens.RarimoToken
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RewardsClaimViewModel @Inject constructor(
    private val walletManager: WalletManager,
    private val pointsManager: PointsManager,
) : ViewModel() {
    val walletAssets = walletManager.walletAssets

    fun getPointsWalletAsset(): WalletAsset? {
        return walletManager.walletAssets.value.find { it.getToken() is PointsToken }
    }

    fun getRarimoWalletAsset(): WalletAsset? {
        return walletManager.walletAssets.value.find { it.getToken() is RarimoToken }
    }

    var _pointsWalletAsset = MutableStateFlow(getPointsWalletAsset())
        private set

    val pointsWalletAsset: StateFlow<WalletAsset?>
        get() = _pointsWalletAsset.asStateFlow()

    var _rarimoWalletAsset = MutableStateFlow(getRarimoWalletAsset())
        private set

    val rarimoWalletAsset: StateFlow<WalletAsset?>
        get() = _rarimoWalletAsset.asStateFlow()

    fun updateScreenWalletAssets() {
        _pointsWalletAsset.value = getPointsWalletAsset()
        _rarimoWalletAsset.value = getRarimoWalletAsset()
    }

    suspend fun withdrawPoints(amount: String) {
        pointsManager.withdrawPoints(amount)
    }

    suspend fun reloadWalletAssets() {
        walletManager.loadBalances()
    }
}