package com.distributedLab.rarime.domain.points

import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

enum class PointsEventStatuses(val value: String) {
    OPEN("open"),
    FULFILLED("fulfilled"),
    CLAIMED("claimed"),
}

@JsonApi(type = "event")
data class PointsEvent(
//    @Json(name = "id")
//    val id: String,
//    @Json(name = "type")
//    val type: String,

    val status: PointsEventStatuses,

    @Json(name = "created_at")
    val createdAt: Long,

    @Json(name = "updated_at")
    val updatedAt: Long,

    val meta: PointsEventMeta,

    @Json(name = "points_amount")
    val pointsAmount: Long,

    // TODO: relationship
    val balance: PointsBalance?,
) : Resource()

data class PointsEventMeta(
    val static: PointsEventMetaStatic,
    val dynamic: PointsEventMetaDynamic,
)

data class PointsEventMetaStatic(
    val name: String,
    val reward: Long,
    val title: String,
    val description: String,
    @Json(name = "short_description")
    val shortDescription: String,
    val frequency: String,
    @Json(name = "starts_at")
    val startsAt: String,
    @Json(name = "expires_at")
    val expiresAt: String,
    @Json(name = "action_url")
    val actionUrl: String,
    val logo: String,
)

data class PointsEventMetaDynamic(
    val id: String,
)

data class ClaimEventPayload(
    val id: String,
    val type: String,
)
