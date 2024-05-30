package com.distributedLab.rarime.util

object RarimoUtils {
    fun isValidAddress(address: String): Boolean {
        val pattern = "^rarimo[0-9a-z]{39}$"
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)
        return regex.matches(address)
    }
}