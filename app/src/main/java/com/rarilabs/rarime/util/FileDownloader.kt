package com.rarilabs.rarime.util

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class FileDownloaderInternal(private val context: Context) {

    private val client = OkHttpClient()

    fun downloadFile(url: String, fileName: String,callback: (Boolean) -> Unit) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("FileDownloader", "Download failed", e)
                callback(false)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    Log.e("FileDownloader", "Download failed: ${response.message}")
                    callback(false)
                    return
                }

                val file = File(context.filesDir, fileName)
                try {
                    response.body?.let { body ->
                        saveToFile(body, file)
                        callback(true)
                    } ?: callback(false)
                } catch (e: Exception) {
                    Log.e("FileDownloader", "Saving file failed", e)
                    callback(false)
                }
            }
        })
    }

    private fun saveToFile(body: ResponseBody, file: File) {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)

            val data = ByteArray(4096)
            var count: Int
            while (inputStream.read(data).also { count = it } != -1) {
                outputStream.write(data, 0, count)
            }
        } catch (e: IOException) {
            Log.e("FileDownloader", "Error saving file", e)
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
            Log.e("FileDownloader", "Error unzipping file", e)
            false
        } finally {
            inputStream?.close()
        }
    }
}