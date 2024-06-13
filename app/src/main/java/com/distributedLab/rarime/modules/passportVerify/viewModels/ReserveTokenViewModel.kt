package com.distributedLab.rarime.modules.passportVerify.viewModels

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.PassportManager
import com.distributedLab.rarime.modules.common.WalletManager
import com.distributedLab.rarime.util.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReserveTokenViewModel @Inject constructor(
    val walletManager: WalletManager, private val passportManager: PassportManager
) : ViewModel() {

    //TODO: Reserve flow
    suspend fun reserve() {
        walletManager.claimAirdrop()
        walletManager.updateIsReserved()
    }

    fun getFlag(): String {
        return Country.fromISOCode(passportManager.getIsoCode()!!)!!.flag
    }
}