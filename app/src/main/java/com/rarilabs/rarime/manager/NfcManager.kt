package com.rarilabs.rarime.manager

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ScanNFCState {
    NOT_SCANNING, SCANNING, SCANNED, ERROR
}

class NfcManager @Inject constructor(
    private val context: Context
) {
    private lateinit var adapter: NfcAdapter

    lateinit var activity: Activity

    private var handleTags: ((Tag) -> Unit)? = null
    private var onError: ((Exception) -> Unit)? = null

    private var _state = MutableStateFlow(ScanNFCState.NOT_SCANNING)
    val state: StateFlow<ScanNFCState>
        get() = _state.asStateFlow()

    fun resetState() {
        _state.value = ScanNFCState.NOT_SCANNING

        disableForegroundDispatch()
    }

    /* ENABLE SCANNING */
    fun enableForegroundDispatch() {
        Log.i("NfcManager", "Enabling NFC foreground dispatch")
        adapter = NfcAdapter.getDefaultAdapter(activity)
        val intent = Intent(context, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filter = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
        adapter.enableForegroundDispatch(activity, pendingIntent, null, filter)
    }

    /* DISABLE SCANNING */
    fun disableForegroundDispatch() {
        Log.i("NfcManager", "Disabling NFC foreground dispatch")
        adapter.disableForegroundDispatch(activity)
    }

    /* SCAN HANDLING */
    fun handleNewIntent(intent: Intent) {
        Log.i("NfcManager", "Handling new NFC intent")
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            tag?.let {
                // Handle the tag here or pass it to a ViewModel or other handler
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        _state.value = ScanNFCState.SCANNING

                        Log.i("NfcManager", "Handling tag: $it")
                        handleTags?.invoke(it)

                        _state.value = ScanNFCState.SCANNED
                    } catch (e: Exception) {
                        Log.e("NfcManager", "Error handling tag", e)
                        e.printStackTrace()

                        _state.value = ScanNFCState.ERROR

                        disableForegroundDispatch()

                        onError?.invoke(e)
                    }
                }
            }
        }
    }

    fun startScanning(customTagsHandler: (Tag) -> Unit, onError: (Exception) -> Unit) {
        try {
            this.handleTags = customTagsHandler
            this.onError = onError

            enableForegroundDispatch()
        } catch (e: Exception) {
            Log.e("NfcManager", "Error starting NFC scanning", e)
            _state.value = ScanNFCState.ERROR
            disableForegroundDispatch()
        }
    }
}