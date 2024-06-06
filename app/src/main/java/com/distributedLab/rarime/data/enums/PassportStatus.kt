package com.distributedLab.rarime.data.enums

enum class PassportStatus(val value: Int) {
    UN_SCANNED(1), ALLOWED(2), WAIT_LIST(3), NOT_ALLOWED(4);

    companion object {
        fun fromInt(value: Int) = PassportStatus.entries.first { it.value == value }
    }
}