package com.rarilabs.rarime.api.hiddenPrize.models

data class CreateUserRequest(
    val data: Data
) {
    data class Data(
        val type: String = "users",
        val attributes: Attributes
    ) {
        data class Attributes(
            val referred_by: String
        )
    }
}

data class CreateUserResponse(
    val data: User,
    val included: List<UserStats>? = null
)

data class User(
    val id: String,
    val type: String = "users",
    val attributes: Attributes
) {
    data class Attributes(
        val referral_code: String,
        val referrals_count: Int,
        val referrals_limit: Int,
        val social_share: Boolean,
        val created_at: Long,
        val updated_at: Long
    )
}

data class UserStats(
    val type: String = "user_stats",
    val attributes: Attributes
) {
    data class Attributes(
        val extra_attempts_left: Int,
        val total_attempts_count: Int,
        val reset_time: Long
    )
}