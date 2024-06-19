package com.distributedLab.rarime.modules.home

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.api.airdrop.AirDropManager
import com.distributedLab.rarime.api.points.PointsManager
import com.distributedLab.rarime.data.enums.PassportCardLook
import com.distributedLab.rarime.data.enums.PassportIdentifier
import com.distributedLab.rarime.data.tokens.RarimoToken
import com.distributedLab.rarime.manager.PassportManager
import com.distributedLab.rarime.manager.WalletAsset
import com.distributedLab.rarime.manager.WalletManager
import com.distributedLab.rarime.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val passportManager: PassportManager,
    private val airDropManager: AirDropManager,
    private val pointsManager: PointsManager,
    private val walletManager: WalletManager,
    private val dataStoreManager: SecureSharedPrefsManager,

    ) : ViewModel() {
    val isAirDropClaimed = airDropManager.isAirDropClaimed

    private val _rmoAsset =
        MutableStateFlow(walletManager.walletAssets.value.find { it.token is RarimoToken })

    val rmoAsset: StateFlow<WalletAsset?>
        get() = _rmoAsset

//    var _pointsToken = MutableStateFlow(
//        walletManager.walletAssets.value.find { it.token is PointsToken }?.token as PointsToken?
//    )
//        private set
//
//    val pointsToken: StateFlow<PointsToken?>
//        get() = _pointsToken.asStateFlow()

    // FIXME: temp
    val pointsBalance = pointsManager.pointsBalance

    var passport = passportManager.passport
    var passportCardLook = passportManager.passportCardLook
    var passportIdentifiers = passportManager.passportIdentifiers
    var isIncognito = passportManager.isIncognitoMode

    val passportStatus = passportManager.passportStatus

    fun onPassportCardLookChange(passportCardLook: PassportCardLook) {
        passportManager.updatePassportCardLook(passportCardLook)
    }

    fun onIncognitoChange(isIncognito: Boolean) {
        passportManager.updateIsIncognitoMode(isIncognito)
    }

    fun clearAllData() {
        dataStoreManager.clearAllData()
    }

    fun onPassportIdentifiersChange(passportIdentifiers: List<PassportIdentifier>) {
        passportManager.updatePassportIdentifiers(passportIdentifiers)
    }
}