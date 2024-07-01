package com.rarilabs.rarime.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.squareup.moshi.JsonClass
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

@JsonClass(generateAdapter = true)
data class ErrorLog(val message: String, val stackTrace: String)

object ErrorHandler {
    private const val TAG = "ErrorHandler"
    private const val KEY_ALIAS = "LogFileKey"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val IV_SEPARATOR = "]"

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

            writeEncrypted(logEntry)
        } catch (e: Exception) {
            Log.e(TAG, "Error writing log to file", e)
        }
    }

    private fun writeEncrypted(data: String) {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv

            FileOutputStream(logFile, true).use { fos ->
                fos.write(iv)
                CipherOutputStream(fos, cipher).use { cos ->
                    cos.write(data.toByteArray())
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
                    BufferedReader(InputStreamReader(cis)).use { reader ->
                        reader.readText()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting log data", e)
            ""
        }
    }

    // TODO: not works
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
                    FileWriter(tempFile).use { writer ->
                        BufferedReader(InputStreamReader(cis)).use { reader ->
                            reader.forEachLine { line ->
                                writer.appendLine(line)
                            }
                        }
                    }
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting log data to temp file", e)
            tempFile.delete()
            null
        }
    }

    fun getLogFile(): File {
        return logFile
    }
}

