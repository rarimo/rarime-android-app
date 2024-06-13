package com.distributedLab.rarime.modules.passportVerify.viewModels

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.api.airdrop.AirDropManager
import com.distributedLab.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClaimSpecificTokenViewModel @Inject constructor(
    private val walletManager: WalletManager,
    private val airDropManager: AirDropManager
) : ViewModel() {

    suspend fun claimAirdrop() {
        airDropManager.claimAirdrop()
        walletManager.updateIsSpecificClaimed()
    }
}