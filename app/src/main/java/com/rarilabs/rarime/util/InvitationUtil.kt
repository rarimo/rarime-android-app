package com.rarilabs.rarime.util

import android.content.Context
import android.content.Intent

object InvitationUtil {
    fun shareInvitation(context: Context, text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, null))
    }
}