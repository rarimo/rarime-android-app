package com.rarilabs.rarime.modules.passportScan

import android.content.Context
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.FileDownloaderInternal
import com.rarilabs.rarime.util.circuits.RegisterNoirCircuitData
import com.rarilabs.rarime.util.circuits.RegisteredCircuitData
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File

class CircuitNoirDownloader(private val context: Context) {

    private val fileDownloader = FileDownloaderInternal(context)

    suspend fun downloadNoirByteCode(
        circuitData: RegisterNoirCircuitData,
        onProgressUpdate: (Int, Boolean) -> Unit
    ): String = suspendCancellableCoroutine { continuation ->


        val url = getNoirCircuitUrl(circuitData)

        try {
            fileDownloader.downloadFile(
                url, "ultraPlonkTrustedSetup.dat"
            ) { success, isFinished, progress, e ->

                if (e != null) {
                    continuation.resumeWith(Result.failure(e))
                    return@downloadFile
                }

                if (success) {
                    if (!isFinished) {
                        onProgressUpdate(progress, false)
                    } else {
                        onProgressUpdate(100, true)
                    }
                } else {
                    continuation.resumeWith(Result.failure(ConnectionError()))
                }
            }
        } catch (e: Exception) {
            ErrorHandler.logError("Download", "Unexpected error", e)
            continuation.resumeWith(Result.failure(ConnectionError().apply { initCause(e) }))
        }
    }

    suspend fun downloadTrustedSetup(
        onProgressUpdate: (Int, Boolean) -> Unit
    ): String = suspendCancellableCoroutine { continuation ->

        val trustedSetupUrl = BaseConfig.NOIR_TRUSTED_SETUP_URL

        try {
            fileDownloader.downloadFile(
                trustedSetupUrl, "ultraPlonkTrustedSetup.dat"
            ) { success, isFinished, progress, e ->

                if (e != null) {
                    continuation.resumeWith(Result.failure(e))
                    return@downloadFile
                }

                if (success) {
                    if (!isFinished) {
                        onProgressUpdate(progress, false)
                    } else {
                        onProgressUpdate(100, true)
                    }
                } else {
                    continuation.resumeWith(Result.failure(ConnectionError()))
                }
            }
        } catch (e: Exception) {
            ErrorHandler.logError("Download", "Unexpected error", e)
            continuation.resumeWith(Result.failure(ConnectionError().apply { initCause(e) }))
        }

    }

    fun deleteRedunantFiles(circuitData: RegisteredCircuitData) {
        val trustedSetup = getTrustedSetupPath()

        val archive = File(trustedSetup)

        archive.delete()
    }

    private fun getNoirCircuitUrl(circuitData: RegisterNoirCircuitData): String {
        return when (circuitData) {
            RegisterNoirCircuitData.REGISTER_IDENTITY_2_256_3_6_336_264_21_2448_6_2008 -> BaseConfig.registerIdentity_2_256_3_6_336_264_21_2448_6_2008
            RegisterNoirCircuitData.REGISTER_IDENTITY_2_256_3_6_336_248_1_2432_3_256 -> BaseConfig.registerIdentity_2_256_3_6_336_248_1_2432_3_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_1_256_3_4_600_248_1_1496_3_256 -> BaseConfig.registerIdentity_1_256_3_4_600_248_1_1496_3_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_20_256_3_3_336_224_NA -> BaseConfig.registerIdentity_20_256_3_3_336_224_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_10_256_3_3_576_248_1_1184_5_264 -> BaseConfig.registerIdentity_10_256_3_3_576_248_1_1184_5_264
        }
    }

    fun getTrustedSetupPath(): String {
        return "${context.filesDir}/ultraPlonkTrustedSetup.dat"
    }


}
