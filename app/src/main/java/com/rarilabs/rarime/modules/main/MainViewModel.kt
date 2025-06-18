package com.rarilabs.rarime.modules.main

import android.app.Application
import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import com.rarilabs.rarime.data.enums.AppIcon
import com.rarilabs.rarime.data.enums.SecurityCheckState
import com.rarilabs.rarime.manager.AirDropManager
import com.rarilabs.rarime.manager.AuthManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.manager.SecurityManager
import com.rarilabs.rarime.manager.SettingsManager
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.ui.components.SnackbarShowOptions
import com.rarilabs.rarime.util.AppIconUtil
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class AppLoadingStates {
    LOADING, LOADED, LOAD_FAILED, MAINTENANCE,
}

enum class ScreenInsets {
    TOP, RIGHT, BOTTOM, LEFT,
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: Application,
    private val securityManager: SecurityManager,
    private val settingsManager: SettingsManager,
    private val walletManager: WalletManager,
    private val airDropManager: AirDropManager,
    private val authManager: AuthManager,
    private val identityManager: IdentityManager,
    private val passportManager: PassportManager,
    private val pointsManager: PointsManager,

    ) : AndroidViewModel(app) {

    val isScreenLocked = securityManager.isScreenLocked


    val isLogsDeleted = identityManager.isLogsDeleted

    val passportStatus = passportManager.passportStatus


    private var _appLoadingState = MutableStateFlow(AppLoadingStates.LOADING)

    val appLoadingStates: StateFlow<AppLoadingStates>
        get() = _appLoadingState

    private val _appIcon = MutableStateFlow(AppIconUtil.getIcon(app))

    val appIcon: StateFlow<AppIcon>
        get() = _appIcon.asStateFlow()

    val pointsToken = walletManager.pointsToken

    private var _isModalShown = MutableStateFlow(false)

    val isModalShown: StateFlow<Boolean>
        get() = _isModalShown.asStateFlow()

    private var _modalContent = MutableStateFlow<@Composable () -> Unit?>({})

    val modalContent: StateFlow<@Composable () -> Unit?>
        get() = _modalContent.asStateFlow()

    var _screenInsets = MutableStateFlow<Map<ScreenInsets, Number>>(
        mapOf(
            ScreenInsets.TOP to 0,
            ScreenInsets.RIGHT to 0,
            ScreenInsets.BOTTOM to 0,
            ScreenInsets.LEFT to 0,
        )
    )
        private set
    val screenInsets: StateFlow<Map<ScreenInsets, Number>>
        get() = _screenInsets.asStateFlow()

    fun setScreenInsets(
        top: Number? = _screenInsets.value[ScreenInsets.TOP],
        right: Number? = _screenInsets.value[ScreenInsets.RIGHT],
        bottom: Number? = _screenInsets.value[ScreenInsets.BOTTOM],
        left: Number? = _screenInsets.value[ScreenInsets.LEFT],
    ) {
        _screenInsets.value = mapOf(
            ScreenInsets.TOP to (top ?: _screenInsets.value[ScreenInsets.TOP]!!),
            ScreenInsets.RIGHT to (right ?: _screenInsets.value[ScreenInsets.RIGHT]!!),
            ScreenInsets.BOTTOM to (bottom ?: _screenInsets.value[ScreenInsets.BOTTOM]!!),
            ScreenInsets.LEFT to (left ?: _screenInsets.value[ScreenInsets.LEFT]!!),
        )
    }

    fun setAppIcon(appIcon: AppIcon) {
        _appIcon.value = appIcon
    }


    var colorScheme = settingsManager.colorScheme

    private var _isBottomBarShown = MutableStateFlow<Boolean>(true)

    val isBottomBarShown: StateFlow<Boolean>
        get() = _isBottomBarShown.asStateFlow()

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
        uri?.let {
            _extIntDataURI.value = it to System.currentTimeMillis()
        }
    }


    suspend fun initApp() {
        withContext(Dispatchers.IO) {
            _appLoadingState.value = AppLoadingStates.LOADING

            if (pointsManager.getMaintenanceStatus()) {
                _appLoadingState.value = AppLoadingStates.MAINTENANCE
                return@withContext
            }
            if (identityManager.privateKey.value == null) {
                _appLoadingState.value = AppLoadingStates.LOADED
                return@withContext
            }


            if (!isLogsDeleted.value) {
                try {
                    ErrorHandler.clearLogFile()
                    identityManager.updateIsLogsDeleted(true)
                } catch (e: Exception) {
                    ErrorHandler.logError("MainViewModel", "Failed to clear logs", e)
                }
            }


            try {
                val loginJob = async { tryLogin() }
                val userDetailsJob = async { loadUserDetails() }

                // awaitAll waits for all deferred tasks to complete.
                // The total wait time is the duration of the LONGEST task.
                awaitAll(loginJob, userDetailsJob)
                _appLoadingState.value = AppLoadingStates.LOADED
            } catch (e: Exception) {
                _appLoadingState.value = AppLoadingStates.LOAD_FAILED
                ErrorHandler.logError("MainScreen", "Failed to init app", e)
            }
        }
    }

    private suspend fun loadUserDetails() = coroutineScope {
        //val walletDeferred = async { walletManager.loadBalances() }
        val passportDeferred = async { passportManager.loadPassportStatus() }

        awaitAll(passportDeferred)
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
        _isBottomBarShown.value = isVisible
    }

    suspend fun showSnackbar(options: SnackbarShowOptions) {
        _snackbarContent.value = options

        kotlinx.coroutines.delay(
            when (options.duration) {
                SnackbarDuration.Short -> 2000
                SnackbarDuration.Long -> 4000
                SnackbarDuration.Indefinite -> Long.MAX_VALUE
            }
        )

        clearSnackbarOptions()
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