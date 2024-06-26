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

    private var handleTags: ((Tag) -> Unit)? = null

    private var _state = MutableStateFlow(ScanNFCState.NOT_SCANNING)
    val state: StateFlow<ScanNFCState>
        get() = _state.asStateFlow()

    fun resetState() {
        _state.value = ScanNFCState.NOT_SCANNING
    }

    /* ENABLE SCANNING */
    fun enableForegroundDispatch() {
        adapter = NfcAdapter.getDefaultAdapter(context)
        val intent = Intent(context, context.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filter = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
        adapter.enableForegroundDispatch(context as Activity, pendingIntent, null, filter)
    }

    /* DISABLE SCANNING */
    fun disableForegroundDispatch() {
        adapter.disableForegroundDispatch(context as Activity)
    }

    /* SCAN HANDLING */
    fun handleNewIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            tag?.let {
                // Handle the tag here or pass it to a ViewModel or other handler
                CoroutineScope(Dispatchers.Default).launch {
                    handleTags?.invoke(it)
                    _state.value = ScanNFCState.SCANNED // FIXME: is this the right place to set the state?
                }
            }
        }
    }

    fun startScanning(customTagsHandler: (Tag) -> Unit): (() -> Unit)? {
        try {
            _state.value = ScanNFCState.SCANNING

            handleTags = customTagsHandler
            enableForegroundDispatch()

            return ::disableForegroundDispatch
        } catch (e: Exception) {
            Log.e("NfcManager", "Error starting NFC scanning", e)
            _state.value = ScanNFCState.ERROR
            disableForegroundDispatch()
        }

        // FIXME: This should never happen
        return null
    }
}