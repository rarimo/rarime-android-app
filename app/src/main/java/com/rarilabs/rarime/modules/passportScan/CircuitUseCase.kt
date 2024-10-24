package com.rarilabs.rarime.modules.passportScan

import android.content.Context
import android.util.Log
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.modules.passportScan.models.RegisteredCircuitData
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.FileDownloaderInternal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File

data class DownloadRequest(
    val zkey: String, val zkeyLen: Long, val dat: String, val datLen: Long
)

class CircuitUseCase(val context: Context) {
    private val fileDownloader = FileDownloaderInternal(context)

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun download(
        circuitData: RegisteredCircuitData, onProgressUpdate: (Int, Boolean) -> Unit
    ): DownloadRequest? = suspendCancellableCoroutine { continuation ->
        val circuitURL = when (circuitData) {
            RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA2048 -> BaseConfig.REGISTER_IDENTITY_CIRCUIT_DATA_RSA2048
            RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA4096 -> BaseConfig.REGISTER_IDENTITY_CIRCUIT_DATA_RSA4096
        }

        continuation.invokeOnCancellation {
            // Handle coroutine cancellation if needed
            ErrorHandler.logError("Download", "Download coroutine cancelled")
        }

        if (fileExists(context, CIRCUIT_NAME_ARCHIVE)) {
            val zkeyLen = fileDownloader.getFileAbsolute(getZkeyFilePath(circuitData)).length()
            val datLen = fileDownloader.getFileAbsolute(getDatFilePath(circuitData)).length()
            val downloadRequest = DownloadRequest(
                zkey = getZkeyFilePath(circuitData),
                zkeyLen,
                dat = getDatFilePath(circuitData),
                datLen
            )
            ErrorHandler.logDebug("Download", "Already downloaded")
            continuation.resume(downloadRequest) {}
            return@suspendCancellableCoroutine
        }


        fileDownloader.downloadFile(
            circuitURL, CIRCUIT_NAME_ARCHIVE
        ) { success, isFinished, progress ->
            if (success) {

                if (!isFinished) {
                    onProgressUpdate(progress, false)
                } else {
                    onProgressUpdate(100, true)

                    Log.i("Download", "File Downloaded")
                    val archive = fileDownloader.getFile(CIRCUIT_NAME_ARCHIVE)
                    val resultOfUnzip = fileDownloader.unzipFile(archive)
                    if (resultOfUnzip) {
                        onProgressUpdate(100, true)

                        fileDownloader.getFile(getZkeyFilePath(circuitData)).length()
                        val zkeyLen =
                            fileDownloader.getFileAbsolute(getZkeyFilePath(circuitData)).length()
                        val datLen =
                            fileDownloader.getFileAbsolute(getDatFilePath(circuitData)).length()

                        val downloadRequest = DownloadRequest(
                            zkey = getZkeyFilePath(circuitData),
                            zkeyLen,
                            dat = getDatFilePath(circuitData),
                            datLen
                        )

                        continuation.resume(downloadRequest) {}
                    } else {
                        ErrorHandler.logError("Download", "Unzip failed")
                        continuation.resume(null) {}
                    }
                }


            } else {
                ErrorHandler.logError("Download", "Download failed")
                continuation.resume(null) {}
            }
        }

    }

    fun getDatFilePath(circuitData: RegisteredCircuitData): String {
        return "${context.filesDir}/${circuitData.value}-download/${circuitData.value}.dat"
    }

    fun fileExists(context: Context, fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.exists()
    }

    fun getZkeyFilePath(circuitData: RegisteredCircuitData): String {
        return "${context.filesDir}/${circuitData.value}-download/circuit_final.zkey"
    }

    private companion object {

        const val CIRCUIT_NAME_ARCHIVE = "CIRCUIT_ARCHIVE.zip"
    }
}