package com.rarilabs.rarime.modules.passportVerify.viewModels

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.AuthManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReserveTokenViewModel @Inject constructor(
    val walletManager: WalletManager,
    private val passportManager: PassportManager,
    val pointsManager: PointsManager,
    val authManager: AuthManager,
    private val sharedPrefsManager: SecureSharedPrefsManager
) : ViewModel() {

    suspend fun reserve() {
        pointsManager.verifyPassport()
        walletManager.loadBalances()
    }

    fun setAlreadyReserved() {
        sharedPrefsManager.saveIsAlreadyReserved(true)
    }

    fun getFlag(): String {
        return Country.fromISOCode(passportManager.getIsoCode()!!).flag
    }
}