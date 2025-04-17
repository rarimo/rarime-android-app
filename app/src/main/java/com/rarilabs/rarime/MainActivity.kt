package com.rarilabs.rarime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.rarilabs.rarime.manager.NfcManager
import com.rarilabs.rarime.manager.ScanNFCState
import com.rarilabs.rarime.modules.appUpdate.InAppUpdate
import com.rarilabs.rarime.modules.main.MainScreen
import com.rarilabs.rarime.util.ErrorHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var nfcManager: NfcManager

    private var navController: NavHostController? = null

    private var deepLinkData: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            )
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        installSplashScreen()
        initAppsFlyer()
        nfcManager.activity = this

        setContent {
            navController = rememberNavController()
            InAppUpdate(activity = this)
            MainScreen(navController = navController!!)
        }
    }

    override fun onPause() {
        super.onPause()
        if (nfcManager.state.value != ScanNFCState.NOT_SCANNING) {
            nfcManager.disableForegroundDispatch()
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (navController != null) {
            val data = intent.data
            handleDeepLink(data)
        }
        nfcManager.handleNewIntent(intent)
    }

    override fun onDestroy() {
        Log.d("MainActivityBW", "onDestroy called")
        super.onDestroy()
    }

    private fun initAppsFlyer() {
        AppsFlyerLib.getInstance()
            .init(BaseConfig.APPSFLYER_DEV_KEY, null, application.applicationContext)
        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(deepLinkResult: DeepLinkResult) {
                if (deepLinkResult.status != DeepLinkResult.Status.FOUND) {
                    ErrorHandler.logError(
                        "AppsFlyer",
                        "Deep link not found, status: ${deepLinkResult.status}"
                    )
                    ErrorHandler.logError("AppsFlyer", "Deep link error: ${deepLinkResult.error}")
                    return
                }

                val deepLinkObj: DeepLink = deepLinkResult.deepLink
                if (deepLinkObj.isDeferred == true) {
                    Log.i("AppsFlyer", "Deep link value: ${deepLinkObj.deepLinkValue ?: "null"}")
                    if (deepLinkObj.deepLinkValue != null) {
                        nfcManager.pointsManager.saveDeferredReferralCode(deepLinkObj.deepLinkValue!!)
                    }
                }
            }
        })

        AppsFlyerLib.getInstance().start(application.applicationContext)
    }
    private fun handleDeepLink(uri: Uri?) {
        if (uri == null) {
            Log.i("uri", "uri is null")
            return
        }
        //TODO: there is an issue that will cause onNewIntent to be called twice when the activity is already present.
        if (intent?.data != null) {
            deepLinkData = uri
            navController?.navigate(uri)
        } else {
            Log.i("Smt wrong", "intent?.data == null")
        }
    }
}