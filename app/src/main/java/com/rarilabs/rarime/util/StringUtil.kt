package com.rarilabs.rarime.util

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
}
