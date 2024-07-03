package com.rarilabs.rarime.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.modules.passportScan.models.EDocument
import java.io.File
import java.io.FileWriter

object SendEmailUtil {
    fun sendEmail(
        file: File,
        context: Context,
        header: String = "App logs",
        description: String = "Please check my attached logs."
    ): Intent {
        val recipient = BaseConfig.FEEDBACK_EMAIL

        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.setData(Uri.parse("mailto:"))

        val emailIntent = Intent(Intent.ACTION_SEND)

        val fileUri: Uri = FileProvider.getUriForFile(
            context, context.applicationContext.packageName + ".provider", file
        )


        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, header)
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        emailIntent.putExtra(Intent.EXTRA_TEXT, description)
        emailIntent.selector = selectorIntent

        return Intent.createChooser(emailIntent, "Send email...")
    }

    fun generateEdocumentFile(eDocument: EDocument, context: Context): File? {
        try {

            val file = File(context.filesDir, "eDocument.json")

            val eDocJson = Gson().toJson(eDocument)

            FileWriter(file).use { writer ->
                writer.write(eDocJson)
                writer.flush()
                writer.close()
            }

            return file

        } catch (e: Exception) {
            return null
        }
    }

    fun deleteEdocumentFile(context: Context) {
        try {
            val file = File(context.filesDir, "eDocument.json" )
            if (file.exists()){
                file.delete()
            }
        }catch (e:Exception) {
            ErrorHandler.logError("Send Edocument", "Cant delete eDocument file", e)
        }

    }
}
