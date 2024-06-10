package com.distributedLab.rarime.data.enums

enum class PassportStatus(val value: Int) {
    UNSCANNED(1), ALLOWED(2), WAITLIST(3), NOT_ALLOWED(4);

    companion object {
        fun fromInt(value: Int) = PassportStatus.entries.first { it.value == value }
    }
}