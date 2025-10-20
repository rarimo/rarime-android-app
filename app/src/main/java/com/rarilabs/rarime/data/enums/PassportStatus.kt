package com.rarilabs.rarime.data.enums

enum class PassportStatus(val value: Int) {
    UNSCANNED(1),
    ALLOWED(2),
    WAITLIST(3),
    UNSUPPORTED_FOR_REWARDS(4),
    WAITLIST_UNSUPPORTED_FOR_REWARDS(5),
    UNREGISTERED(6),
    ALREADY_REGISTERED_BY_OTHER_PK(7);

    companion object {
        fun fromInt(value: Int) = PassportStatus.entries.first { it.value == value }
    }
}