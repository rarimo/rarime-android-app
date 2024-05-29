package com.distributedLab.rarime.modules.passport.claimToken

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClaimTokenViewModel @Inject constructor(
    val walletManager: WalletManager
) : ViewModel() {

    suspend fun claimAirdrop() {
        walletManager.claimAirdrop()
    }
}