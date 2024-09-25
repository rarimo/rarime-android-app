package com.rarilabs.rarime.data.enums

enum class SecurityCheckState(val value: Int) {
    UNSET(0),
    ENABLED(1),
    DISABLED(2);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}