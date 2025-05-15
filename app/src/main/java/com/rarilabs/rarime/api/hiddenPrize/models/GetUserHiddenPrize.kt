package com.rarilabs.rarime.api.hiddenPrize.models

data class GetUserResponse(
    val data: User,
    val included: List<IncludedItem>,
    val relationships: GetUserRelationship
)

sealed class IncludedItem {
    data class Stats(val userStats: UserStats) : IncludedItem()
    data class CelebrityItem(val celebrity: Celebrity) : IncludedItem()
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

data class Celebrity(
    val type: String = "celebrity",
    val attributes: Attributes
) {
    data class Attributes(
        val title: String,
        val description: String,
        val status: String,
        val image: String,
        val hint: String
    )
}

sealed class GetUserRelationship(
    val user_stats: UserStatsRelationship,
    val celebrity: CelebrityRelationship
)
