package com.rarilabs.rarime.util

import android.content.Context
import android.util.Log
import com.squareup.moshi.JsonClass
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant

@JsonClass(generateAdapter = true)
data class ErrorLog(val message: String, val stackTrace: String)

object ErrorHandler {
    private const val TAG = "ErrorHandler"
    private const val LOG_FILE_NAME = "app.log"
    private const val MAX_LOG_SIZE = 10 * 1024 * 1024 // 10 MB in bytes

    private lateinit var logFile: File

    fun initialize(context: Context) {
        setupLogFile(context)
        setupUncaughtExceptionHandler()
    }

    private fun setupLogFile(context: Context) {
        logFile = File(context.filesDir, LOG_FILE_NAME)
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

            val logEntry = buildString {
                appendLine("=========== $unixTimestamp ===========")
                appendLine("$level/$TAG: ${errorLog.message}")
                if (errorLog.stackTrace.isNotEmpty()) {
                    appendLine(errorLog.stackTrace)
                }
                appendLine("=================================")
            }

            if (logFile.length() + logEntry.toByteArray().size > MAX_LOG_SIZE) {
                trimLogFile()
            }

            logFile.appendText(logEntry)
        } catch (e: Exception) {
            Log.e(TAG, "Error writing log to file", e)
        }
    }

    fun clearLogFile() {
        try {
            logFile.writeText("")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing log file", e)
        }
    }

    private fun trimLogFile() {
        try {
            val lines = logFile.readLines()
            val trimmedLines = lines.drop(lines.size / 4) // Keep the last 3/4 of the log

            BufferedWriter(FileWriter(logFile)).use { writer ->
                trimmedLines.forEach { line ->
                    writer.write(line)
                    writer.newLine()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error trimming log file", e)
        }
    }

    fun getLogFile(): File {
        return logFile
    }
}
