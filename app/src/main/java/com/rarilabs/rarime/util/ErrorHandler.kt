package com.rarilabs.rarime.util

import android.content.Context
import android.util.Log
import com.squareup.moshi.JsonClass
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant

@JsonClass(generateAdapter = true)
data class ErrorLog(val message: String, val stackTrace: String)

object ErrorHandler {
    private const val TAG = "ErrorHandler"

    private lateinit var logFile: File

    fun initialize(context: Context) {
        setupLogFile(context)
        setupUncaughtExceptionHandler()
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

            val unixTimestamp = Instant.now().epochSecond

            FileWriter(logFile, true).use { writer ->
                val logEntry = buildString {
                    appendLine("=========== $unixTimestamp ===========")
                    appendLine("$level/$TAG: ${errorLog.message}")
                    if (errorLog.stackTrace.isNotEmpty()) {
                        appendLine(errorLog.stackTrace)
                    }
                    appendLine("=================================")
                }

                writer.append(logEntry)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error writing log to file", e)
        }
    }

    fun getLogFile(): File {
        return logFile
    }
}
