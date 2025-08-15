package com.rarilabs.rarime.modules.passportScan

import android.content.Context
import android.util.Log
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.util.FileDownloaderInternal
import com.rarilabs.rarime.util.circuits.RegisterNoirCircuitData
import com.rarilabs.rarime.util.circuits.RegisteredCircuitData
import java.io.File

class CircuitNoirDownloader(private val context: Context) {

    private val fileDownloader = FileDownloaderInternal(context)

    suspend fun downloadNoirByteCode(
        circuitData: RegisterNoirCircuitData,
        onProgressUpdate: (Int, Boolean) -> Unit
    ): String {

        val url = getNoirCircuitUrl(circuitData)

        val filePath = url.split("/").last()

        Log.i("circuit plonk url", url)

        return try {
            fileDownloader.downloadFileBlocking(
                url, filePath
            ) { progress ->
                onProgressUpdate(progress, false)
            }

        } catch (e: Exception) {
            throw e
        }.absolutePath

    }

    suspend fun downloadTrustedSetup(
        onProgressUpdate: (Int, Boolean) -> Unit
    ): String {
        val trustedSetupUrl = BaseConfig.NOIR_TRUSTED_SETUP_URL
        val fileHash = "a23b2409db1bbac272520bdca41ec1d1"

        val file = fileDownloader.downloadFileBlocking(
            trustedSetupUrl,
            "ultraPlonkTrustedSetup.dat",
            fileHash
        ) {
            onProgressUpdate(it, false)
        }

        return file.absolutePath
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
            RegisterNoirCircuitData.REGISTER_IDENTITY_21_256_3_3_336_232_NA -> BaseConfig.registerIdentity_21_256_3_3_336_232_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_21_256_3_4_576_232_NA -> BaseConfig.registerIdentity_21_256_3_4_576_232_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_11_256_3_3_576_248_NA -> BaseConfig.registerIdentity_11_256_3_3_576_248_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_2_256_3_6_576_248_1_2432_3_256 -> BaseConfig.registerIdentity_2_256_3_6_576_248_1_2432_3_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_3_512_3_3_336_264_NA -> BaseConfig.registerIdentity_3_512_3_3_336_264_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_14_256_3_3_576_240_NA -> BaseConfig.registerIdentity_14_256_3_3_576_240_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_14_256_3_4_576_248_1_1496_3_256 -> BaseConfig.registerIdentity_14_256_3_4_576_248_1_1496_3_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_20_160_3_2_576_184_NA -> BaseConfig.registerIdentity_20_160_3_2_576_184_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_1_256_3_5_336_248_1_2120_4_256 -> BaseConfig.registerIdentity_1_256_3_5_336_248_1_2120_4_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_2_256_3_4_336_232_1_1480_4_256 -> BaseConfig.registerIdentity_2_256_3_4_336_232_1_1480_4_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_2_256_3_4_336_248_NA -> BaseConfig.registerIdentity_2_256_3_4_336_248_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_20_256_3_5_336_248_NA -> BaseConfig.registerIdentity_20_256_3_5_336_248_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_24_256_3_4_336_248_NA -> BaseConfig.registerIdentity_24_256_3_4_336_248_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_6_160_3_3_336_216_1_1080_3_256 -> BaseConfig.registerIdentity_6_160_3_3_336_216_1_1080_3_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_11_256_3_5_576_248_NA -> BaseConfig.registerIdentity_11_256_3_5_576_248_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_14_256_3_4_336_232_1_1480_5_296 -> BaseConfig.registerIdentity_14_256_3_4_336_232_1_1480_5_296
            RegisterNoirCircuitData.REGISTER_IDENTITY_1_256_3_4_576_232_1_1480_3_256 -> BaseConfig.registerIdentity_1_256_3_4_576_232_1_1480_3_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_1_256_3_5_576_248_NA -> BaseConfig.registerIdentity_1_256_3_5_576_248_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_20_160_3_3_576_200_NA -> BaseConfig.registerIdentity_20_160_3_3_576_200_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_23_160_3_3_576_200_NA -> BaseConfig.registerIdentity_23_160_3_3_576_200_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_3_256_3_4_600_248_1_1496_3_256 -> BaseConfig.registerIdentity_3_256_3_4_600_248_1_1496_3_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_1_256_3_6_576_264_1_2448_3_256 -> BaseConfig.registerIdentity_1_256_3_6_576_264_1_2448_3_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_11_256_3_5_584_264_1_2136_4_256 -> BaseConfig.registerIdentity_11_256_3_5_584_264_1_2136_4_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_11_256_3_5_576_264_NA -> BaseConfig.registerIdentity_11_256_3_5_576_264_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_2_256_3_4_336_248_22_1496_7_2408 -> BaseConfig.registerIdentity_2_256_3_4_336_248_22_1496_7_2408
            RegisterNoirCircuitData.REGISTER_IDENTITY_1_256_3_4_336_232_NA -> BaseConfig.registerIdentity_1_256_3_4_336_232_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_25_384_3_3_336_232_NA -> BaseConfig.registerIdentity_25_384_3_3_336_232_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_25_384_3_4_336_264_1_2904_2_256 -> BaseConfig.registerIdentity_25_384_3_4_336_264_1_2904_2_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_26_512_3_3_336_248_NA -> BaseConfig.registerIdentity_26_512_3_3_336_248_NA
            RegisterNoirCircuitData.REGISTER_IDENTITY_26_512_3_3_336_264_1_1968_2_256 -> BaseConfig.registerIdentity_26_512_3_3_336_264_1_1968_2_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_27_512_3_4_336_248_NA -> BaseConfig.registerIdentity_27_512_3_4_336_248_NA

            RegisterNoirCircuitData.REGISTER_IDENTITY_1_256_3_5_336_248_1_2120_3_256 -> BaseConfig.registerIdentity_1_256_3_5_336_248_1_2120_3_256
            RegisterNoirCircuitData.REGISTER_IDENTITY_7_160_3_3_336_216_1_1080_3_256 -> BaseConfig.registerIdentity_7_160_3_3_336_216_1_1080_3_256

            RegisterNoirCircuitData.REGISTER_IDENTITY_8_160_3_3_336_216_1_1080_3_256 -> BaseConfig.registerIdentity_8_160_3_3_336_216_1_1080_3_256

            RegisterNoirCircuitData.REGISTER_IDENTITY_3_256_3_3_576_248_NA -> BaseConfig.registerIdentity_3_256_3_3_576_248_NA
        }
    }

    fun getTrustedSetupPath(): String {
        return "${context.filesDir}/ultraPlonkTrustedSetup.dat"
    }


}