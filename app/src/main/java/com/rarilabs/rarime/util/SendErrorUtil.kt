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

        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.setData(Uri.parse("mailto:"))

        val emailIntent = Intent(Intent.ACTION_SEND)

        val fileUri: Uri = FileProvider.getUriForFile(
            context, context.applicationContext.packageName + ".provider", file
        )


        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App logs")
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        emailIntent.putExtra(Intent.EXTRA_TEXT,  "Please check my attached logs.")
        emailIntent.selector = selectorIntent

        return Intent.createChooser(emailIntent, "Send email...")
    }
}
