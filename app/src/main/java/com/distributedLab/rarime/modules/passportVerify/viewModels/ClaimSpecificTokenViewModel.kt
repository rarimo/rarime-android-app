package com.distributedLab.rarime.modules.passportVerify.viewModels

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.api.airdrop.AirDropManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ClaimSpecificTokenViewModel @Inject constructor(
    private val airDropManager: AirDropManager
) : ViewModel() {
    suspend fun claimAirdrop() {
        airDropManager.claimAirdrop()
    }
}