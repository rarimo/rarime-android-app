package com.distributedLab.rarime.data.enums

enum class PassportCardLook(val value: Int) {
    WHITE(0),
    BLACK(1),
    GREEN(2);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}