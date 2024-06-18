package com.distributedLab.rarime.modules.passportScan

import android.content.Context
import android.util.Log
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.modules.passportScan.models.RegisteredCircuitData
import com.distributedLab.rarime.util.FileDownloaderInternal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

data class DownloadRequest(
    val zkey: String, val dat: String
)

class CircuitUseCase(context: Context) {
    private val fileDownloader = FileDownloaderInternal(context)

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun download(circuitData: RegisteredCircuitData): DownloadRequest? =
        suspendCancellableCoroutine { continuation ->
            val circuitURL = when (circuitData) {
                RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA2048 -> BaseConfig.REGISTER_IDENTITY_CIRCUIT_DATA_RSA2048
                RegisteredCircuitData.REGISTER_IDENTITY_UNIVERSAL_RSA4096 -> BaseConfig.REGISTER_IDENTITY_CIRCUIT_DATA_RSA4096
            }

            continuation.invokeOnCancellation {
                // Handle coroutine cancellation if needed
                Log.e("Download", "Download coroutine cancelled")
            }

            fileDownloader.downloadFile(circuitURL, CIRCUIT_NAME_ARCHIVE) { success ->
                if (success) {
                    val archive = fileDownloader.getFile(CIRCUIT_NAME_ARCHIVE)
                    val resultOfUnzip = fileDownloader.unzipFile(archive)
                    if (resultOfUnzip) {
                        val downloadRequest = DownloadRequest(
                            zkey = getZkeyFilePath(circuitData), dat = getDatFilePath(circuitData)
                        )
                        continuation.resume(downloadRequest) {}
                    } else {
                        Log.e("Download", "Unzip failed")
                        continuation.resume(null) {}
                    }
                } else {
                    Log.e("Download", "Download failed")
                    continuation.resume(null) {}
                }
            }
        }

    private fun getZkeyFilePath(circuitData: RegisteredCircuitData): String {
        return "${circuitData.value}-download/${circuitData.value}.dat"
    }

    private fun getDatFilePath(circuitData: RegisteredCircuitData): String {
        return "${circuitData.value}-download/circuit_final.zkey"
    }

    private companion object {

        const val CIRCUIT_NAME_ARCHIVE = "CIRCUIT_ARCHIVE.zip"
    }
}