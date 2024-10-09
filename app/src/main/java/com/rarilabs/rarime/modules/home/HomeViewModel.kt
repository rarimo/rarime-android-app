package com.rarilabs.rarime.modules.home

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.airdrop.AirDropManager
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.tokens.PointsToken
import com.rarilabs.rarime.manager.NotificationManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val passportManager: PassportManager,
    private val airDropManager: AirDropManager,
    private val walletManager: WalletManager,
    private val notificationManager: NotificationManager
) : ViewModel() {
    val isAirDropClaimed = airDropManager.isAirDropClaimed

    val notReadNotifications = notificationManager.notificationList

    private val _selectedWalletAsset =
        MutableStateFlow(walletManager.walletAssets.value.find { it.token is PointsToken })

    val selectedWalletAsset: StateFlow<WalletAsset?>
        get() = _selectedWalletAsset.asStateFlow()

    val pointsToken = walletManager.pointsToken

    val isShowPassport = passportManager.isShowPassport
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

    fun onPassportIdentifiersChange(passportIdentifiers: List<PassportIdentifier>) {
        passportManager.updatePassportIdentifiers(passportIdentifiers)
    }

    suspend fun loadNotifications() {
        try {
            notificationManager.loadNotifications()
        } catch (e: Exception) {
            ErrorHandler.logError("HomeViewModel", "error load notifications", e)
        }
    }

    suspend fun loadUserDetails() = coroutineScope {
        val passportStatus = async {
            try {
                passportManager.loadPassportStatus()
            }catch (e: Exception) {
                ErrorHandler.logError("loadPassportStatus", "Error", e)
            }
        }
        val walletBalances = async {
            try {
                walletManager.loadBalances()
            } catch (e: Exception) { /* Handle exception */
                ErrorHandler.logError("loadBalances ", "Error", e)
            }
        }

        // Await for all the async operations to complete
        passportStatus.await()
        walletBalances.await()

    }
}