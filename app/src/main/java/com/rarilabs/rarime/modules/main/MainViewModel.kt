package com.rarilabs.rarime.modules.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.airdrop.AirDropManager
import com.rarilabs.rarime.api.auth.AuthManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.manager.SecurityManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class AppLoadingStates {
    LOADING,
    LOADED,
    LOAD_FAILED,
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    securityManager: SecurityManager,
    settingsManager: SettingsManager,
    private val walletManager: WalletManager,
    private val airDropManager: AirDropManager,
    private val authManager: AuthManager,
    private val identityManager: IdentityManager,
    private val passportManager: PassportManager
) : ViewModel() {
    val passportStatus = passportManager.passportStatus

    var appLoadingState = mutableStateOf(AppLoadingStates.LOADING)
        private set

    val pointsToken = walletManager.pointsToken

    suspend fun initApp() {
        if (identityManager.privateKey.value == null) {
            appLoadingState.value = AppLoadingStates.LOADED
            return
        }

        appLoadingState.value = AppLoadingStates.LOADING

        try {
            tryLogin()

            delay(500)

            loadUserDetails()
        } catch (e: Exception) {
            appLoadingState.value = AppLoadingStates.LOAD_FAILED
            Log.e("MainScreen", "Failed to init app", e)
        }

        appLoadingState.value = AppLoadingStates.LOADED
    }

    private suspend fun loadUserDetails() = coroutineScope {
        val walletBalances = async { try { walletManager.loadBalances() } catch (e: Exception) { /* Handle exception */ } }
        val airDropDetails = async { try { airDropManager.getAirDropByNullifier() } catch (e: Exception) { /* Handle exception */ } }

        // Await for all the async operations to complete
        walletBalances.await()
        airDropDetails.await()
    }

    private suspend fun tryLogin() {
        try {
            if (authManager.isAccessTokenExpired()) {
                authManager.refresh()
            }
        } catch (e: Exception) {
            authManager.login()
        }
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

    suspend fun finishIntro() {
        withContext(Dispatchers.IO) {
            tryLogin()

            loadUserDetails()

            isIntroFinished.value = true
            dataStoreManager.saveIsIntroFinished(true)
        }
    }

    fun joinWaitlist() {
        dataStoreManager.saveIsInWaitlist(true)
    }
}