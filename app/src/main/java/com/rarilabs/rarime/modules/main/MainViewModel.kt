package com.rarilabs.rarime.modules.main

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.api.airdrop.AirDropManager
import com.rarilabs.rarime.api.auth.AuthManager
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.SecurityManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.ui.components.SnackbarShowOptions
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class AppLoadingStates {
    LOADING,
    LOADED,
    LOAD_FAILED,
    MAINTENANCE,
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

    fun getIsPkInit(): Boolean {
        return identityManager.privateKeyBytes != null
    }

    var _snackbarContent = MutableStateFlow<SnackbarShowOptions?>(null)
        private set
    val snackbarContent: StateFlow<SnackbarShowOptions?>
        get() = _snackbarContent.asStateFlow()

    private var _snackbarHostState = MutableStateFlow(SnackbarHostState())
    val snackbarHostState: StateFlow<SnackbarHostState>
        get() = _snackbarHostState.asStateFlow()

    private var _extIntDataURI = MutableStateFlow<Pair<Uri?, Long>?>(null)
    val extIntDataURI: StateFlow<Pair<Uri?, Long>?>
        get() = _extIntDataURI.asStateFlow()

    fun setExtIntDataURI(uri: Uri?) {
        val tempUrl = uri?.buildUpon()?.build().let { it to System.currentTimeMillis() }
        _extIntDataURI.value = tempUrl
    }


    suspend fun initApp() = coroutineScope {
        // 1. Early checks
        if (pointsManager.getMaintenanceStatus()) {
            appLoadingState.value = AppLoadingStates.MAINTENANCE
            return@coroutineScope
        }
        if (identityManager.privateKey.value == null) {
            appLoadingState.value = AppLoadingStates.LOADED
            return@coroutineScope
        }

        // 2. Set loading state
        appLoadingState.value = AppLoadingStates.LOADING

        val clearLogsJob = launch {
            if (!isLogsDeleted.value) {
                try {
                    ErrorHandler.clearLogFile()
                    identityManager.updateIsLogsDeleted(true)
                } catch (e: Exception) {
                    ErrorHandler.logError("MainViewModel", "Failed to clear logs", e)
                }
            }
        }

        val initJob = launch {
            try {
                tryLogin()
                loadUserDetails()
                appLoadingState.value = AppLoadingStates.LOADED
            } catch (e: Exception) {
                appLoadingState.value = AppLoadingStates.LOAD_FAILED
                ErrorHandler.logError("MainScreen", "Failed to init app", e)
            }
        }

        initJob.join()
        clearLogsJob.join()
    }

    private suspend fun loadUserDetails() = coroutineScope {
        val walletDeferred = async { walletManager.loadBalances() }
        val passportDeferred = async { passportManager.loadPassportStatus() }

        awaitAll(walletDeferred, passportDeferred)
    }

    suspend fun tryLogin() = runCatching {
        authManager.login()
    }.onFailure { e ->
        // Single point to handle the exception
        ErrorHandler.logError("MainViewModel", "Failed to login", e)
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

    suspend fun showSnackbar(options: SnackbarShowOptions) {
        _snackbarContent.value = options
        val result = _snackbarHostState.value.showSnackbar(
            message = "",
            duration = options.duration,
        )

        when (result) {
            SnackbarResult.Dismissed -> {
                clearSnackbarOptions()
            }

            SnackbarResult.ActionPerformed -> {
                clearSnackbarOptions()
            }
        }
    }

    fun clearSnackbarOptions() {
        _snackbarContent.value = null
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
}