package com.rarilabs.rarime.modules.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.airdrop.AirDropManager
import com.rarilabs.rarime.api.auth.AuthManager
import com.rarilabs.rarime.api.ext_integrator.ExtIntegratorManager
import com.rarilabs.rarime.api.ext_integrator.models.QrAction
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.SecurityManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okio.ByteString.Companion.decodeHex
import javax.inject.Inject

enum class AppLoadingStates {
    LOADING,
    LOADED,
    LOAD_FAILED,
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val securityManager: SecurityManager,
    private val settingsManager: SettingsManager,
    private val walletManager: WalletManager,
    private val airDropManager: AirDropManager,
    private val authManager: AuthManager,
    private val identityManager: IdentityManager,
    private val passportManager: PassportManager,
    private val pointsManager: PointsManager,
    private val extIntegratorManager: ExtIntegratorManager
) : ViewModel() {
    val isLogsDeleted = identityManager.isLogsDeleted

    val passportStatus = passportManager.passportStatus

    var appLoadingState = mutableStateOf(AppLoadingStates.LOADING)
        private set

    val pointsToken = walletManager.pointsToken

    var _isModalShown = MutableStateFlow(false)
        private set
    val isModalShown: StateFlow<Boolean>
        get() = _isModalShown.asStateFlow()

    var _modalContent = MutableStateFlow<@Composable () -> Unit?>({})
        private set
    val modalContent: StateFlow<@Composable () -> Unit?>
        get() = _modalContent.asStateFlow()

    var colorScheme = settingsManager.colorScheme
    var isBottomBarShown = mutableStateOf(false)
        private set

    suspend fun initApp() {
        if (identityManager.privateKey.value == null) {
            appLoadingState.value = AppLoadingStates.LOADED
            return
        }

        appLoadingState.value = AppLoadingStates.LOADING

        try {
            if (!isLogsDeleted.value) {
                ErrorHandler.clearLogFile()
                identityManager.updateIsLogsDeleted(true)
            }
        } catch (e: Exception) {
            ErrorHandler.logError("MainViewModel", "Failed to clear logs", e)
        }

        try {
            tryLogin()

            delay(500)

            loadUserDetails()
        } catch (e: Exception) {
            appLoadingState.value = AppLoadingStates.LOAD_FAILED
            ErrorHandler.logError("MainScreen", "Failed to init app", e)
        }

        appLoadingState.value = AppLoadingStates.LOADED
    }

    suspend fun loadUserDetails() = coroutineScope {
        val walletBalances = async {
            try {
                walletManager.loadBalances()
            } catch (e: Exception) {
                e.printStackTrace()
                /* Handle exception */
            }
        }
        val airDropDetails = async {
            try {
                airDropManager.getAirDropByNullifier()
            } catch (e: Exception) {
                e.printStackTrace()
                /* Handle exception */
            }
        }
        val passportStatus = async {
            try {
                passportManager.loadPassportStatus()
            } catch (e: Exception) {
                /* Handle exception */
                e.printStackTrace()
            }
        }

        // Await for all the async operations to complete
        walletBalances.await()
        airDropDetails.await()
        passportStatus.await()
    }

    suspend fun tryLogin() {
        try {
            authManager.login()
        } catch (e: Exception) {
            ErrorHandler.logError("MainViewModel", "Failed to login", e)
        }
    }

    fun setModalContent(content: @Composable () -> Unit?) {
        _modalContent.value = content
    }

    fun setModalVisibility(isVisible: Boolean) {
        _isModalShown.value = isVisible
    }

    fun setBottomBarVisibility(isVisible: Boolean) {
        isBottomBarShown.value = isVisible
    }

    suspend fun finishIntro() {
        withContext(Dispatchers.IO) {
            tryLogin()

            loadUserDetails()
        }
    }

    suspend fun acceptInvitation(code: String) {
        pointsManager.createPointsBalance(code)
    }

    fun updatePasscodeState(state: SecurityCheckState) {
        securityManager.updatePasscodeState(state)
    }

    fun updateBiometricsState(state: SecurityCheckState) {
        securityManager.updateBiometricsState(state)
    }

    suspend fun sendExtIntegratorCallback(qrAction: QrAction) {
        extIntegratorManager.handleAction(qrAction)
    }
}