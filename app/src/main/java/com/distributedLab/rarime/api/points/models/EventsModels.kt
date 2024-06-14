package com.distributedLab.rarime.api.points.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class PointsEventStatuses(val value: String) {
    OPEN("open"),
    FULFILLED("fulfilled"),
    CLAIMED("claimed"),
}

data class PointsEventBody(
    val data: PointsEventData
)

@JsonClass(generateAdapter = true)
data class PointsEventData(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,

    val attributes: PointsEventAttributes,
)

@JsonClass(generateAdapter = true)
data class PointsEventAttributes(
    val status: PointsEventStatuses,

    @Json(name = "created_at") val createdAt: Long,
    @Json(name = "updated_at") val updatedAt: Long,

    val meta: PointsEventMeta,

    @Json(name = "points_amount") val pointsAmount: Long,

    // TODO: relationship
    val balance: PointsBalanceData?,
)

@JsonClass(generateAdapter = true)
data class PointsEventMeta(
    val static: PointsEventMetaStatic,
    val dynamic: PointsEventMetaDynamic,
)

@JsonClass(generateAdapter = true)
data class PointsEventMetaStatic(
    val name: String,
    val reward: Long,
    val title: String,
    val description: String,
    @Json(name = "short_description") val shortDescription: String,
    val frequency: String,
    @Json(name = "starts_at") val startsAt: String,
    @Json(name = "expires_at") val expiresAt: String,
    @Json(name = "action_url") val actionUrl: String,
    val logo: String,
)

@JsonClass(generateAdapter = true)
data class PointsEventMetaDynamic(
    val id: String,
)

@JsonClass(generateAdapter = true)
data class ClaimEventBody(
    @Json(name = "data") val data: ClaimEventData
)

@JsonClass(generateAdapter = true)
data class ClaimEventData(
    val id: String,
    val type: String,
)
