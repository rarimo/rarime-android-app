package com.rarilabs.rarime.api.points.models

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class JoinRewardsProgramRequest(
    val data: JoinRewardsProgramRequestData
)

@JsonClass(generateAdapter = true)
data class JoinRewardsProgramRequestData(
    val id: String,
    val type: String,
    val attributes: JoinRewardsProgramRequestAttributes
)

@JsonClass(generateAdapter = true)
data class JoinRewardsProgramRequestAttributes(
    val country: String
)


@JsonClass(generateAdapter = true)
data class VerifyPassportResponse(
    val data: VerifyPassportResponseData
)

@JsonClass(generateAdapter = true)
data class VerifyPassportResponseData(
    val id: String,
    val type: String,
    val attributes: VerifyPassportResponseAttributes
)

@JsonClass(generateAdapter = true)
data class VerifyPassportResponseAttributes(
    val claimed: Boolean
)