package com.distributedLab.rarime.ui.components.enter_program.components

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.api.points.PointsManager
import com.distributedLab.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val pointsManager: PointsManager,
    private val walletManager: WalletManager
): ViewModel() {
    suspend fun createBalance(referralCode: String) {
        pointsManager.createPointsBalance(referralCode)
        walletManager.loadBalances()
    }
}