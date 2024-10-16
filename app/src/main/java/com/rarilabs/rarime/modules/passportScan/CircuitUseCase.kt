package com.rarilabs.rarime.modules.passportScan

import android.content.Context
import android.util.Log
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.FileDownloaderInternal
import com.rarilabs.rarime.util.circuits.RegisteredCircuitData
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
            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_5_576_248_NA -> BaseConfig.registerIdentity_1_256_3_5_576_248_NA
            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_6_576_248_1_2432_5_296 -> BaseConfig.registerIdentity_1_256_3_6_576_248_1_2432_5_296
            RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_336_264_21_2448_6_2008 -> BaseConfig.registerIdentity_2_256_3_6_336_264_21_2448_6_2008
            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_7_336_264_21_3072_6_2008 -> BaseConfig.registerIdentity_21_256_3_7_336_264_21_3072_6_2008
            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_6_576_264_1_2448_3_256 -> BaseConfig.registerIdentity_1_256_3_6_576_264_1_2448_3_256
            RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_336_248_1_2432_3_256 -> BaseConfig.registerIdentity_2_256_3_6_336_248_1_2432_3_256
            RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_576_248_1_2432_3_256 -> BaseConfig.registerIdentity_2_256_3_6_576_248_1_2432_3_256
        }

        continuation.invokeOnCancellation {
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