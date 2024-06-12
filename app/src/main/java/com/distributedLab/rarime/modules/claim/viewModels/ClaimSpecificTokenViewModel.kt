package com.distributedLab.rarime.modules.claim.viewModels

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClaimSpecificTokenViewModel @Inject constructor(
    private val walletManager: WalletManager
) : ViewModel() {

    suspend fun claimAirdrop() {
        walletManager.claimAirdrop()
        walletManager.updateIsSpecificClaimed()
    }
}