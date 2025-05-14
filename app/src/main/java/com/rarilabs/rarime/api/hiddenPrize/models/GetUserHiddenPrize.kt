package com.rarilabs.rarime.api.hiddenPrize.models

data class GetUserResponse(
    val data: User,
    val included: List<IncludedItem>
)

sealed class IncludedItem {
    data class Stats(val userStats: UserStats) : IncludedItem()
    data class Status(val serviceStatus: ServiceStatus) : IncludedItem()
    data class HintItem(val hint: Hint) : IncludedItem()
}

data class ServiceStatus(
    val type: String = "service_status",
    val attributes: Attributes
) {
    data class Attributes(
        val expiration_time: Long,
        val max_total_attempt_count: Int,
        val max_day_attempt_count: Int,
        val max_feature_length: Int
    )
}

data class Hint(
    val type: String = "hint",
    val attributes: Attributes
) {
    data class Attributes(
        val hint_message: String
    )
}