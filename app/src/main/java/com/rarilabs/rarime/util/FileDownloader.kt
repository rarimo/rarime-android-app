package com.rarilabs.rarime.util

import android.content.Context
import com.rarilabs.rarime.modules.passportScan.ConnectionError
import com.rarilabs.rarime.modules.passportScan.DownloadCircuitError
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.SocketException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class FileDownloaderInternal(private val context: Context) {

    private val client = OkHttpClient()

    fun downloadFile(
        url: String,
        fileName: String,
        callback: (isSuccess: Boolean, isFinished: Boolean, progress: Int, e: Exception?) -> Unit
    ) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                ErrorHandler.logError("FileDownloader", "Download failed", e)
                callback(false, false, 0, ConnectionError().apply { initCause(e) })
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    ErrorHandler.logError("FileDownloader", "Download failed: ${response.message}")
                    callback(false, false, 0, ConnectionError())
                    return
                }

                val file = File(context.filesDir, fileName)
                try {
                    response.body?.let { body ->
                        saveToFile(body, file, callback)
                    } ?: run {
                        callback(false, false, 0, ConnectionError())
                    }
                } catch (e: Exception) {
                    ErrorHandler.logError("FileDownloader", "Saving file failed", e)
                    callback(false, false, 0, DownloadCircuitError().apply { initCause(e) })
                }
            }
        })
    }

    private fun saveToFile(
        body: ResponseBody, file: File, callback: (Boolean, Boolean, Int, Exception?) -> Unit
    ) {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)

            val data = ByteArray(4096)
            val fileSize = body.contentLength()
            var downloaded: Long = 0
            var count: Int

            while (inputStream.read(data).also { count = it } != -1) {
                outputStream.write(data, 0, count)
                downloaded += count
                val progress = (downloaded * 100 / fileSize).toInt()
                callback(true, false, progress, null)
            }
            callback(true, true, 100, null)
        } catch (e: SocketException) {
            ErrorHandler.logError("FileDownloader", "Network error while saving file", e)
            callback(false, false, 0, ConnectionError().apply { initCause(e) })
        } catch (e: IOException) {
            ErrorHandler.logError("FileDownloader", "IO error while saving file", e)
            callback(false, false, 0, DownloadCircuitError().apply { initCause(e) })
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    fun getFile(fileName: String): File {
        return File(context.filesDir, fileName)
    }

    fun getFileAbsolute(fullPath: String): File {
        return File("", fullPath)
    }

    fun unzipFile(zipFile: File): Boolean {
        var inputStream: ZipInputStream? = null
        return try {
            inputStream = ZipInputStream(FileInputStream(zipFile))

            var zipEntry: ZipEntry? = inputStream.nextEntry
            while (zipEntry != null) {
                val newFile = File(context.filesDir, zipEntry.name)
                if (zipEntry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
                    FileOutputStream(newFile).use { fos ->
                        inputStream.copyTo(fos)
                    }
                }
                zipEntry = inputStream.nextEntry
            }
            inputStream.closeEntry()
            true
        } catch (e: IOException) {
            ErrorHandler.logError("FileDownloader", "Error unzipping file", e)
            false
        } finally {
            inputStream?.close()
        }
    }
}