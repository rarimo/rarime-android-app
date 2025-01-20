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
            RegisteredCircuitData.REGISTER_IDENTITY_11_256_3_3_576_248_1_1184_5_264 -> BaseConfig.registerIdentity_11_256_3_3_576_248_1_1184_5_264
            RegisteredCircuitData.REGISTER_IDENTITY_12_256_3_3_336_232_NA -> BaseConfig.registerIdentity_12_256_3_3_336_232_NA
            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_4_336_232_1_1480_5_296 -> BaseConfig.registerIdentity_1_256_3_4_336_232_1_1480_5_296
            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_4_600_248_1_1496_3_256 -> BaseConfig.registerIdentity_1_256_3_4_600_248_1_1496_3_256
            RegisteredCircuitData.REGISTER_IDENTITY_1_160_3_4_576_200_NA -> BaseConfig.registerIdentity_1_160_3_4_576_200_NA
            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_3_336_232_NA -> BaseConfig.registerIdentity_21_256_3_3_336_232_NA
            RegisteredCircuitData.REGISTER_IDENTITY_24_256_3_4_336_232_NA -> BaseConfig.registerIdentity_24_256_3_4_336_232_NA
            RegisteredCircuitData.REGISTER_IDENTITY_20_256_3_3_336_224_NA -> BaseConfig.registerIdentity_20_256_3_3_336_224_NA
            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_3_576_248_NA -> BaseConfig.registerIdentity_1_256_3_3_576_248_NA
            RegisteredCircuitData.REGISTER_IDENTITY_1_160_3_3_576_200_NA -> BaseConfig.registerIdentity_1_160_3_3_576_200_NA
            RegisteredCircuitData.REGISTER_IDENTITY_10_256_3_3_576_248_1_1184_5_264 -> BaseConfig.registerIdentity_10_256_3_3_576_248_1_1184_5_264
            RegisteredCircuitData.REGISTER_IDENTITY_11_256_3_5_576_248_1_1808_4_256 -> BaseConfig.registerIdentity_11_256_3_5_576_248_1_1808_4_256
            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_3_576_232_NA -> BaseConfig.registerIdentity_21_256_3_3_576_232_NA
            RegisteredCircuitData.REGISTER_IDENTITY_3_160_3_3_336_200_NA -> BaseConfig.registerIdentity_3_160_3_3_336_200_NA
            RegisteredCircuitData.REGISTER_IDENTITY_3_160_3_4_576_216_1_1512_3_256 -> BaseConfig.registerIdentity_3_160_3_4_576_216_1_1512_3_256
            RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_336_264_1_2448_3_256 -> BaseConfig.registerIdentity_2_256_3_6_336_264_1_2448_3_256
            RegisteredCircuitData.REGISTER_IDENTITY_160 -> BaseConfig.registerIdentityLight160
            RegisteredCircuitData.REGISTER_IDENTITY_224 -> BaseConfig.registerIdentityLight224
            RegisteredCircuitData.REGISTER_IDENTITY_256 -> BaseConfig.registerIdentityLight256
            RegisteredCircuitData.REGISTER_IDENTITY_384 -> BaseConfig.registerIdentityLight384
            RegisteredCircuitData.REGISTER_IDENTITY_512 -> BaseConfig.registerIdentityLight512
            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_4_576_232_NA -> BaseConfig.registerIdentity_21_256_3_4_576_232_NA
            RegisteredCircuitData.REGISTER_IDENTITY_11_256_3_3_576_240_1_864_5_264 -> BaseConfig.registerIdentity_11_256_3_3_576_240_1_864_5_264
            RegisteredCircuitData.REGISTER_IDENTITY_11_256_3_5_576_248_1_1808_5_296 -> BaseConfig.registerIdentity_11_256_3_5_576_248_1_1808_5_296
        }

        continuation.invokeOnCancellation {
            ErrorHandler.logError("Download", "Download coroutine cancelled")
        }

        if (fileExists(context, circuitData.name + ".zip")) {
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
            circuitURL, getCircuitArchive(circuitData)
        ) { success, isFinished, progress ->
            if (success) {
                if (!isFinished) {
                    onProgressUpdate(progress, false)
                } else {
                    onProgressUpdate(100, true)

                    Log.i("Download", "File Downloaded")
                    val archive = fileDownloader.getFile(getCircuitArchive(circuitData))
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

    fun getCircuitArchive(circuitData: RegisteredCircuitData): String {
        return circuitData.name + ".zip"
    }

    fun getZkeyFilePath(circuitData: RegisteredCircuitData): String {
        return "${context.filesDir}/${circuitData.value}-download/circuit_final.zkey"
    }
}