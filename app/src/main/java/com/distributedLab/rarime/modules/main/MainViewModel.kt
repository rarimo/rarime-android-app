package com.distributedLab.rarime.modules.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.api.airdrop.AirDropManager
import com.distributedLab.rarime.api.auth.AuthManager
import com.distributedLab.rarime.api.points.PointsManager
import com.distributedLab.rarime.data.tokens.PointsToken
import com.distributedLab.rarime.manager.IdentityManager
import com.distributedLab.rarime.store.SecureSharedPrefsManager
import com.distributedLab.rarime.manager.SecurityManager
import com.distributedLab.rarime.manager.SettingsManager
import com.distributedLab.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    securityManager: SecurityManager,
    settingsManager: SettingsManager,
    private val walletManager: WalletManager,
    private val airDropManager: AirDropManager,
    private val authManager: AuthManager,
    private val identityManager: IdentityManager,
    private val pointsManager: PointsManager
) : ViewModel() {
    // FIXME: recomposability
//    val pointsWalletAsset = walletManager.walletAssets.value.firstOrNull { it.token is PointsToken }
//
//    val pointsToken: PointsToken? = pointsWalletAsset?.token as PointsToken
//
//    val isPointsBalanceCreated = pointsToken?.getIsBalanceCreated() ?: false


    // FIXME: temp
    val pointsBalance = pointsManager.pointsBalance

    suspend fun initApp() {
        try {
            if (authManager.isAccessTokenExpired()) {
                authManager.refresh()
            }
        } catch (e: Exception) {
            // TODO: is it correct?
            identityManager.privateKey.value?.let {
                authManager.login()
            }
        }

        pointsManager.getPointsBalance()
        walletManager.loadBalances()
        airDropManager.getAirDropByNullifier()
    }

    var _isModalShown = MutableStateFlow(false)
        private set

    val isModalShown: StateFlow<Boolean>
        get() = _isModalShown.asStateFlow()

    fun setModalVisibility(isVisible: Boolean) {
        _isModalShown.value = isVisible
    }

    var _modalContent = MutableStateFlow<@Composable () -> Unit?>({})
        private set

    val modalContent: StateFlow<@Composable () -> Unit?>
        get() = _modalContent.asStateFlow()

    fun setModalContent(content: @Composable () -> Unit?) {
        _modalContent.value = content
    }

    var isIntroFinished = mutableStateOf(dataStoreManager.readIsIntroFinished())
        private set

    var isScreenLocked = securityManager.isScreenLocked
    var biometricsState = securityManager.biometricsState
    var passcodeState = securityManager.passcodeState

    var colorScheme = settingsManager.colorScheme

    var isBottomBarShown = mutableStateOf(false)
        private set

    fun setBottomBarVisibility(isVisible: Boolean) {
        isBottomBarShown.value = isVisible
    }

    fun finishIntro() {
        isIntroFinished.value = true
        dataStoreManager.saveIsIntroFinished(true)
    }
}