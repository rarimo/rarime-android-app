package com.rarilabs.rarime.api.hiddenPrize.models


data class CreateUserRequest(
    val id: String,
    val type: String = "users",
    val attributes: CreateUserAttributes
) {
    data class CreateUserAttributes(
        val referred_by: String?
    )
}


data class CreateUserResponse(
    val data: UserData,
    val included: List<Included>? = null
)

data class UserData(
    val id: String,
    val type: String,
    val attributes: UserAttributes
)

data class UserAttributes(
    val referral_code: String,
    val referrals_count: Int,
    val referrals_limit: Int,
    val social_share: Boolean,
    val created_at: Long,
    val updated_at: Long
)