package com.distributedLab.rarime.modules.rewards

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val walletManager: WalletManager,
): ViewModel() {
    val pointsWalletAsset = walletManager.walletAssets.value.find { it.token.symbol == "RRMO" }!!
}