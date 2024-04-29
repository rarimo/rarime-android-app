package com.distributedLab.rarime.data.enums

enum class AppLanguage(val value: String) {
    ENGLISH("en"),
    UKRAINIAN("uk"),
    GEORGIAN("ka");

    companion object {
        fun fromString(value: String) = entries.first { it.value == value }
    }
}