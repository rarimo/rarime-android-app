package com.rarilabs.rarime.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

object SendErrorUtil {
    fun sendErrorEmail(file: File, context: Context): Intent {
        val recipient = "info@rarilabs.com"



        // Get the content URI for the file
        val fileUri: Uri = FileProvider.getUriForFile(
            context, context.applicationContext.packageName + ".provider", file
        )

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.parse("mailto:")
        }

        return Intent.createChooser(emailIntent, "Send email...")
    }

    fun saveErrorDetailsToFile(fileName: String,errorDetails: String, context: Context): File {
        // Create a file in the external storage directory

        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir, fileName)

        // Write the error details to the file
        FileWriter(file).use { writer ->
            writer.write(errorDetails)
        }

        return file
    }

    fun saveFeedbackToFile(errorDetails: String, context: Context): File {
        // Create a file in the external storage directory
        val fileName = "feedback.json"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir, fileName)

        // Write the error details to the file
        FileWriter(file).use { writer ->
            writer.write(errorDetails)
        }

        return file
    }

}