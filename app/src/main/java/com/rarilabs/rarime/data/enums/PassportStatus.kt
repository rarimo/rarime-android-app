package com.rarilabs.rarime.data.enums

enum class PassportStatus(val value: Int) {
    UNSCANNED(1),
    ALLOWED(2),
    WAITLIST(3),
    NOT_ALLOWED(4),
    WAITLIST_NOT_ALLOWED(5),
    UNREGISTERED(6),
    ALREADY_REGISTERED_BY_OTHER_PK(7);

    companion object {
        fun fromInt(value: Int) = PassportStatus.entries.first { it.value == value }
    }
}