package com.rarilabs.rarime.modules.passportScan

import android.content.Context
import android.util.Log
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.FileDownloaderInternal
import com.rarilabs.rarime.util.FileIntegrityChecker
import com.rarilabs.rarime.util.circuits.RegisteredCircuitData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume

data class DownloadRequest(
    val zkey: String, val zkeyLen: Long, val dat: String, val datLen: Long
)

class CircuitDownloader(private val context: Context) {
    private val fileDownloader = FileDownloaderInternal(context)

    suspend fun downloadGrothFiles(
        circuitData: RegisteredCircuitData,
        onProgressUpdate: (Int, Boolean) -> Unit
    ): DownloadRequest? = suspendCancellableCoroutine { continuation ->

        val circuitURL = getCircuitURL(circuitData)

        continuation.invokeOnCancellation {
            ErrorHandler.logError("Download", "Download coroutine cancelled")
        }

        CoroutineScope(Dispatchers.IO).launch {
            val archiveName = circuitData.name + ".zip"
            if (fileExists(archiveName)) {
                val isValidChecksum = FileIntegrityChecker.verifyFileMD5(
                    File(getArchivePath(circuitData)),
                    RegisteredCircuitData.getMD5Checksum(circuitData)
                )

                Log.i("verifyChecksum", isValidChecksum.toString())
                if (isValidChecksum) {
                    ErrorHandler.logDebug("Download", "Already downloaded")
                    processDownloadedFile(circuitData, continuation)
                    return@launch
                }
            }

            try {
                fileDownloader.downloadFile(
                    circuitURL, getCircuitArchive(circuitData)
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
                            processDownloadedFile(circuitData, continuation)
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
    }

    private fun getCircuitURL(circuitData: RegisteredCircuitData): String {
        return when (circuitData) {
            //RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_5_576_248_NA -> BaseConfig.registerIdentity_1_256_3_5_576_248_NA
            //RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_336_264_21_2448_6_2008 -> BaseConfig.registerIdentity_2_256_3_6_336_264_21_2448_6_2008
            RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_7_336_264_21_3072_6_2008 -> BaseConfig.registerIdentity_21_256_3_7_336_264_21_3072_6_2008
            //RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_6_576_264_1_2448_3_256 -> BaseConfig.registerIdentity_1_256_3_6_576_264_1_2448_3_256
            //RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_336_248_1_2432_3_256 -> BaseConfig.registerIdentity_2_256_3_6_336_248_1_2432_3_256
            //RegisteredCircuitData.REGISTER_IDENTITY_2_256_3_6_576_248_1_2432_3_256 -> BaseConfig.registerIdentity_2_256_3_6_576_248_1_2432_3_256
            //RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_4_600_248_1_1496_3_256 -> BaseConfig.registerIdentity_1_256_3_4_600_248_1_1496_3_256
            RegisteredCircuitData.REGISTER_IDENTITY_1_160_3_4_576_200_NA -> BaseConfig.registerIdentity_1_160_3_4_576_200_NA
            //RegisteredCircuitData.REGISTER_IDENTITY_20_256_3_3_336_224_NA -> BaseConfig.registerIdentity_20_256_3_3_336_224_NA
            //RegisteredCircuitData.REGISTER_IDENTITY_10_256_3_3_576_248_1_1184_5_264 -> BaseConfig.registerIdentity_10_256_3_3_576_248_1_1184_5_264
            //RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_3_576_232_NA -> BaseConfig.registerIdentity_21_256_3_3_576_232_NA
            RegisteredCircuitData.REGISTER_IDENTITY_160 -> BaseConfig.registerIdentityLight160
            RegisteredCircuitData.REGISTER_IDENTITY_224 -> BaseConfig.registerIdentityLight224
            RegisteredCircuitData.REGISTER_IDENTITY_256 -> BaseConfig.registerIdentityLight256
            RegisteredCircuitData.REGISTER_IDENTITY_384 -> BaseConfig.registerIdentityLight384
            RegisteredCircuitData.REGISTER_IDENTITY_512 -> BaseConfig.registerIdentityLight512
            //RegisteredCircuitData.REGISTER_IDENTITY_21_256_3_4_576_232_NA -> BaseConfig.registerIdentity_21_256_3_4_576_232_NA
            RegisteredCircuitData.REGISTER_IDENTITY_14_256_3_4_336_64_1_1480_5_296 -> BaseConfig.registerIdentity_14_256_3_4_336_64_1_1480_5_296
            RegisteredCircuitData.REGISTER_IDENTITY_20_160_3_3_736_200_NA -> BaseConfig.registerIdentity_20_160_3_3_736_200_NA
            RegisteredCircuitData.REGISTER_IDENTITY_20_256_3_5_336_72_NA -> BaseConfig.registerIdentity_20_256_3_5_336_72_NA
            RegisteredCircuitData.REGISTER_IDENTITY_4_160_3_3_336_216_1_1296_3_256 -> BaseConfig.registerIdentity_4_160_3_3_336_216_1_1296_3_256
            RegisteredCircuitData.REGISTER_IDENTITY_1_256_3_6_336_560_1_2744_4_256 -> BaseConfig.registerIdentity_1_256_3_6_336_560_1_2744_4_256
        }
    }

    private fun processDownloadedFile(
        circuitData: RegisteredCircuitData,
        continuation: kotlin.coroutines.Continuation<DownloadRequest?>,
    ) {
        try {
            val archive = fileDownloader.getFile(getCircuitArchive(circuitData))
            if (fileDownloader.unzipFile(archive)) {
                val downloadRequest = createDownloadRequest(circuitData)
                continuation.resume(downloadRequest)
            } else {
                ErrorHandler.logError("Download", "Unzip failed")
                continuation.resumeWith(Result.failure(UnpackingError()))
            }
        } catch (e: Exception) {
            continuation.resumeWith(Result.failure(UnpackingError()))
        }
    }

    private fun createDownloadRequest(circuitData: RegisteredCircuitData): DownloadRequest {
        val zkeyFile = fileDownloader.getFileAbsolute(getZkeyFilePath(circuitData))
        val datFile = fileDownloader.getFileAbsolute(getDatFilePath(circuitData))

        return DownloadRequest(
            zkey = getZkeyFilePath(circuitData),
            zkeyLen = zkeyFile.length(),
            dat = getDatFilePath(circuitData),
            datLen = datFile.length()
        )
    }

    private suspend fun fileExists(fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, fileName)
            file.exists()
        }
    }

    fun deleteRedunantFiles(circuitData: RegisteredCircuitData) {
        val folderPath = getZKPFolderPath(circuitData)
        val archivePath = getArchivePath(circuitData)

        val folder = File(folderPath)
        val archive = File(archivePath)

        folder.deleteRecursively()
        archive.delete()
    }

    private fun getZKPFolderPath(circuitData: RegisteredCircuitData): String {
        return "${context.filesDir}/${circuitData.value}-download"
    }

    private fun getArchivePath(circuitData: RegisteredCircuitData): String {
        return "${context.filesDir}/" + getCircuitArchive(circuitData)
    }

    private fun getCircuitArchive(circuitData: RegisteredCircuitData): String {
        return "${circuitData.name}.zip"
    }

    private fun getZkeyFilePath(circuitData: RegisteredCircuitData): String {
        return "${context.filesDir}/${circuitData.value}-download/circuit_final.zkey"
    }

    private fun getDatFilePath(circuitData: RegisteredCircuitData): String {
        return "${context.filesDir}/${circuitData.value}-download/${circuitData.value}.dat"
    }
}

open class DownloadCircuitError : Exception()

class UnpackingError : DownloadCircuitError()

class ConnectionError : DownloadCircuitError()