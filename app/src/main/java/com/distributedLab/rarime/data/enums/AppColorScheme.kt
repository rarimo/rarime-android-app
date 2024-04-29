package com.distributedLab.rarime.data.enums

enum class AppColorScheme(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}