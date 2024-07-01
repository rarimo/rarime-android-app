package com.rarilabs.rarime.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.squareup.moshi.JsonClass
import java.io.*
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@JsonClass(generateAdapter = true)
data class ErrorLog(val message: String, val stackTrace: String)

object ErrorHandler {
    private const val TAG = "ErrorHandler"
    private const val KEY_ALIAS = "LogFileKey"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"

    private lateinit var logFile: File

    fun initialize(context: Context) {
        setupLogFile(context)
        setupUncaughtExceptionHandler()
        generateKeyIfNecessary()
    }

    private fun setupLogFile(context: Context) {
        logFile = File(context.filesDir, "app.log")
    }

    private fun setupUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logError("Uncaught exception", "in thread ${thread.name}", throwable)
        }
    }

    fun logDebug(tag: String, message: String) {
        Log.d(tag, message)
        writeLogToFile("DEBUG", "$tag:\t $message")
    }

    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        writeLogToFile("ERROR", "$tag:\t $message", throwable)
    }

    private fun writeLogToFile(level: String, message: String, throwable: Throwable? = null) {
        try {
            val errorLog = if (throwable != null) {
                val stackTrace = StringWriter().apply {
                    throwable.printStackTrace(PrintWriter(this))
                }.toString()
                ErrorLog(message, stackTrace)
            } else {
                ErrorLog(message, "")
            }

            val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())

            val logEntry = buildString {
                appendLine("=========== $dateTime ===========")
                appendLine("$level/$TAG: ${errorLog.message}")
                if (errorLog.stackTrace.isNotEmpty()) {
                    appendLine(errorLog.stackTrace)
                }
                appendLine("=================================")
            }

            val decryptedLogs = readDecryptedLog()
            val combinedLogs = decryptedLogs + logEntry

            writeEncrypted(combinedLogs.toByteArray())
        } catch (e: Exception) {
            Log.e(TAG, "Error writing log to file", e)
        }
    }

    private fun writeEncrypted(data: ByteArray) {
        try {
            val key = getSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv

            FileOutputStream(logFile, false).use { fos ->
                fos.write(iv.size)
                fos.write(iv)
                CipherOutputStream(fos, cipher).use { cos ->
                    cos.write(data)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error encrypting log data", e)
        }
    }

    private fun generateKeyIfNecessary() {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setRandomizedEncryptionRequired(true)
                    .build()
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating encryption key", e)
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    fun readDecryptedLog(): String {
        return try {
            val key = getSecretKey()

            FileInputStream(logFile).use { fis ->
                val ivSize = fis.read()
                val iv = ByteArray(ivSize)
                fis.read(iv)
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

                CipherInputStream(fis, cipher).use { cis ->
                    cis.readBytes().toString(Charsets.UTF_8)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting log data", e)
            ""
        }
    }

    fun getDecryptedLogFile(context: Context): File? {
        val tempFile = createTempFile("decrypted_log", ".tmp", context.cacheDir)

        return try {
            val key = getSecretKey()

            FileInputStream(logFile).use { fis ->
                val ivSize = fis.read()
                val iv = ByteArray(ivSize)
                fis.read(iv)
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

                CipherInputStream(fis, cipher).use { cis ->
                    tempFile.writeBytes(cis.readBytes())
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting log data to temp file", e)
            tempFile.delete()
            null
        }
    }
}
