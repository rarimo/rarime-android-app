package com.rarilabs.rarime.ui.components.enter_program.components

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val pointsManager: PointsManager,
    private val walletManager: WalletManager
): ViewModel() {
    suspend fun createNLoadBalance(referralCode: String) {
        pointsManager.createPointsBalance(referralCode)
        walletManager.loadBalances()
    }

    suspend fun loadBalance() {
        pointsManager.getPointsBalance()
    }
}