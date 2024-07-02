package com.rarilabs.rarime.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.rarilabs.rarime.BaseConfig
import java.io.File

object SendErrorUtil {
    fun sendErrorEmail(file: File, context: Context): Intent {
        val recipient = BaseConfig.FEEDBACK_EMAIL

        // Get the content URI for the file
        val fileUri: Uri = FileProvider.getUriForFile(
            context, context.applicationContext.packageName + ".provider", file
        )

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, "App logs")
            putExtra(Intent.EXTRA_TEXT, "Please check my attached logs.")
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return Intent.createChooser(emailIntent, "Send email...")
    }
}
