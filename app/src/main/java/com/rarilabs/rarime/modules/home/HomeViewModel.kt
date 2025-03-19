package com.rarilabs.rarime.modules.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rarilabs.rarime.api.airdrop.AirDropManager
import com.rarilabs.rarime.api.registration.RegistrationManager
import com.rarilabs.rarime.data.enums.PassportCardLook
import com.rarilabs.rarime.data.enums.PassportIdentifier
import com.rarilabs.rarime.data.tokens.PointsToken
import com.rarilabs.rarime.manager.NotificationManager
import com.rarilabs.rarime.manager.PassportManager
import com.rarilabs.rarime.manager.WalletAsset
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import com.rarilabs.rarime.store.SecureSharedPrefsManager
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
    private val app: Application,
    private val passportManager: PassportManager,
    private val airDropManager: AirDropManager,
    private val walletManager: WalletManager,
    private val notificationManager: NotificationManager,
    private val registrationManager: RegistrationManager,
    private val sharedPrefsManager: SecureSharedPrefsManager
) : AndroidViewModel(app) {

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

    fun setTempEDocument(eDocument: EDocument) {
        registrationManager.setEDocument(eDocument)
    }

    fun onPassportIdentifiersChange(passportIdentifiers: List<PassportIdentifier>) {
        passportManager.updatePassportIdentifiers(passportIdentifiers)
    }

    fun getIsAlreadyReserved(): Boolean {
        return sharedPrefsManager.getIsAlreadyReserved()
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
            } catch (e: Exception) {
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

//    suspend fun generateTestProof() {
//        val assetContext: Context = (app as Context).createPackageContext("com.rarilabs.rarime", 0)
//        val assetManager = assetContext.assets
//
//
//        val dir = "${app.filesDir}/circuitName"
//
//        val zkeyPath = "$dir/"
//        val datPath = "$dir/"
//
//        val zkeyFile = File(zkeyPath)
//        val datFile = File(datPath)
//
//
//        val zkfilePath = zkeyFile.absolutePath
//        val zkeyFileLen = zkeyFile.length()
//        val datFilePath = datFile.absolutePath
//        val datFileLen = datFile.length()
//        val inputs = """
//
//        """.trimIndent()
//
//        val customDispatcher = Executors.newFixedThreadPool(1) { runnable ->
//            Thread(null, runnable, "LargeStackThread", 100 * 1024 * 1024) // 100 MB stack size
//        }.asCoroutineDispatcher()
//
//        val zkp = ZKPUseCase(app as Context, assetManager)
//        val res = withContext(customDispatcher) {
//            zkp.generateRegisterZKP(
//                zkeyFilePath = zkfilePath,
//                zkeyFileLen = zkeyFileLen,
//                datFilePath = datFilePath,
//                datFileLen = datFileLen,
//                inputs = inputs.toByteArray(),
//                proofFunction =
//            )
//        }
//        Log.e("Res", Gson().toJson(res))
//    }
}