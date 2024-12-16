package com.rarilabs.rarime.util

import android.net.Uri
import java.util.Locale

/**
 * @author AliMertOzdemir
 * @class StringUtil
 * @created 26.11.2020
 */
object StringUtil {
    fun byteArrayToHex(a: ByteArray): String {
        val sb = StringBuilder(a.size * 2)
        for (b in a) sb.append(String.format("%02x", b))
        return sb.toString().uppercase(Locale.getDefault())
    }

    fun fixPersonalNumberMrzData(mrzData: String, personalNumber: String?): String {
        if (personalNumber.isNullOrEmpty()) {
            return mrzData
        }
        var firstPart =
            mrzData.split(personalNumber.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        var restPart =
            mrzData.split(personalNumber.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        if (firstPart.lastIndexOf("<") < 10) {
            firstPart += "<"
        }
        if (restPart.indexOf("<<<<") == 0) {
            restPart = restPart.substring(1)
        }
        return firstPart + personalNumber + restPart
    }

    fun parseHost(rawURL: String): String? {
        return try {
            val uri = Uri.parse(rawURL)
            when (uri.scheme) {
                "http", "https" -> uri.host
                else -> uri.authority ?: uri.host
            }
        } catch (e: Exception) {
            extractTargetRaw(rawURL)
        }
    }

    private fun extractTargetRaw(url: String): String? {
        try {
            // Find the start of the target (after the scheme delimiter "://")
            val schemeDelimiter = "://"
            val startIndex = url.indexOf(schemeDelimiter)
            if (startIndex == -1) return null // No scheme delimiter found

            // Extract the substring starting after "://"
            val remainder = url.substring(startIndex + schemeDelimiter.length)

            // Find the first "/" after the scheme to isolate the target
            val endIndex = remainder.indexOf("/")
            return if (endIndex == -1) remainder else remainder.substring(0, endIndex)
        } catch (e: Exception) {
            // Log errors for invalid input
            println("Error extracting target: ${e.message}")
            return null
        }
    }
}
