package com.rarilabs.rarime

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        ErrorHandler.initialize(this)

        nfcManager.activity = this

        setContent {
            InAppUpdate(activity = this)
            MainScreen()
        }
    }

    override fun onResume() {
        super.onResume()
//        nfcManager.enableForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()

        if (nfcManager.state.value != ScanNFCState.NOT_SCANNING) {
            nfcManager.disableForegroundDispatch()
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        intent?.let {
            nfcManager.handleNewIntent(it)
        }
    }

}