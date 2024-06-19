package com.distributedLab.rarime

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.distributedLab.rarime.modules.main.MainScreen
import com.distributedLab.rarime.modules.passportScan.CircuitUseCase
import com.distributedLab.rarime.modules.passportScan.models.RegisteredCircuitData
import com.distributedLab.rarime.modules.passportScan.nfc.NfcViewModel
import com.distributedLab.rarime.util.FileDownloaderInternal
import com.distributedLab.rarime.util.ZkpUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NfcAdapter

    private val nfcViewModel by viewModels<NfcViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        nfcViewModel.enableNFC = ::enableForegroundDispatch
        nfcViewModel.disableNFC = ::disableNFCForegroundDispatch

        setContent {
            MainScreen()
        }


//        lifecycleScope.launch {
//            while (true) {
//                val status = fileDownloader.checkDownloadStatus()
//                Log.i(status, status)
//                delay(1000L) // Check status every second
//                if (!status.contains("Download successful")) continue
//                val filePath = status.split("Download successful: ")[1]
//                val res = fileDownloader.unzipDownloadedFile()
//
//                val fileUzipePath = res.split("File unzipped to: ")[1]
//                val zkpres = ZkpUtil.test(fileUzipePath)
//                if (zkpres == 0) break
//                else throw IllegalStateException("File is not unzipped")
//            }
//            setContent {
//                MainScreen()
//            }
//        }


    }

    private fun enableForegroundDispatch() {
        adapter = NfcAdapter.getDefaultAdapter(this)
        val intent = Intent(applicationContext, this.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filter = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
        adapter.enableForegroundDispatch(this, pendingIntent, null, filter)
    }

    private fun disableNFCForegroundDispatch() {
        Log.d("Turn of NFC", "Turn of NFCForegroundDispatch")
        adapter.disableForegroundDispatch(this)
    }



    override fun onNewIntent(intent: Intent?) {

        super.onNewIntent(intent)
        setIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent!!.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {

            val tag =
                Objects.requireNonNull(intent.extras)!!.getParcelable<Tag>(NfcAdapter.EXTRA_TAG)!!
            nfcViewModel.setParams(tag)

            CoroutineScope(Dispatchers.Default).launch {
                nfcViewModel.startScanning()
            }
        }
    }


}