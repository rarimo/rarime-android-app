package com.distributedLab.rarime.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

object SendErrorUtil {
    fun sendErrorEmail(file: File, context: Context) {
        val recipient = "yaroslav.ivanov@distributedlab.com"
        val subject = "Error in registerByDocument Function"
        val body = "Please find the attached file for error details."

        // Get the content URI for the file
        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Add this flag

        }

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."), null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveErrorDetailsToFile(errorDetails: String, context: Context): File {
        // Create a file in the external storage directory
        val fileName = "error_details.json"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir, fileName)

        // Write the error details to the file
        FileWriter(file).use { writer ->
            writer.write(errorDetails)
        }

        return file
    }

}