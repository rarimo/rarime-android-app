package com.rarilabs.rarime.services.models


enum class UserStatus(val value: String) {
    UNSCANNED("unscanned"),

    WAITLIST("waitlist"),

    VERIFIED("verified")
}

data class UniversalNotificationContent(
    val event_type: String?,
    val nationality: String?,
    val user_statuses: List<UserStatus>?,
    val new_supported_circuit: String?
)