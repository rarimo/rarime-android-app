package com.rarilabs.rarime.util

import android.content.Context
import android.util.Log
import com.squareup.moshi.JsonClass
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        val logDir = File(context.getExternalFilesDir(null), "logs")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        logFile = File(logDir, "app.log")
    }

    private fun setupUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logError("Uncaught exception", "in thread ${thread.name}", throwable)
            System.exit(1) // Kill the app
        }
    }

    fun logDebug(tag: String, message: String) {
        Log.d(tag, message)
        writeLogToFile("DEBUG", message)
    }

    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        writeLogToFile("ERROR", message, throwable)
    }

    private fun writeLogToFile(level: String, message: String, throwable: Throwable? = null) {
        try {
            FileWriter(logFile, true).use { writer ->
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

                writer.appendLine("=========== $dateTime ===========")
                writer.appendLine("$level/$TAG: ${errorLog.message}")
                if (errorLog.stackTrace.isNotEmpty()) {
                    writer.appendLine(errorLog.stackTrace)
                }
                writer.appendLine("=================================")
            }
        } catch (e: Exception) {
            // Handle error
            Log.e(TAG, "Error writing log to file", e)
        }
    }

    fun getLogFile(): File {
        return logFile
    }
}

