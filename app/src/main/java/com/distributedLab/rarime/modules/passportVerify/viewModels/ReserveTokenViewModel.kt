package com.distributedLab.rarime.modules.passportVerify.viewModels

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.api.auth.AuthManager
import com.distributedLab.rarime.api.points.PointsManager
import com.distributedLab.rarime.manager.PassportManager
import com.distributedLab.rarime.manager.WalletManager
import com.distributedLab.rarime.util.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReserveTokenViewModel @Inject constructor(
    val walletManager: WalletManager, private val passportManager: PassportManager,
    val pointsManager: PointsManager,
    val authManager: AuthManager,
) : ViewModel() {

    suspend fun reserve() {
        pointsManager.verifyPassport()
        pointsManager.getPointsBalance()
        walletManager.loadBalances()
    }

    fun getFlag(): String {
        return Country.fromISOCode(passportManager.getIsoCode()!!)!!.flag
    }
}