package com.distributedLab.rarime.domain.points
import com.squareup.moshi.Json
import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

@JsonApi(type = "balance")
data class PointsBalance (
//    @Json(name = "id")
//    val id: String,
//    @Json(name = "type")
//    val type: String,

    val amount: Long,
    @Json(name = "is_disabled")
    val isDisabled: Boolean,
    @Json(name = "created_at")
    val createdAt: Long,
    @Json(name = "updated_at")
    val updatedAt: Long,
    val rank: Long,
    @Json(name = "active_referral_codes")
    val activeReferralCodes: List<String>,
    @Json(name = "consumed_referral_codes")
    val consumedReferralCodes: List<String>,
    val level: Long,
) : Resource()

@JsonApi(type = "withdrawal")
data class PointsWithdrawal (
//    @Json(name = "id")
//    val id: String,
//    @Json(name = "type")
//    val type: String,

    val amount: Long,
    val address: String,

    @Json(name = "created_at")
    val createdAt: Long,

    val balance: PointsBalance?,
) : Resource()

@JsonApi(type = "point_price")
data class PointsPrice (
//    @Json(name = "id")
//    val type: String,

    val urmo: Long,
) : Resource()

data class CreateBalancePayload(
    val id: String,
    val type: String,

    @Json(name = "referred_by")
    val referredBy: String,
)

data class VerifyPassportPayload(
    val id: String,
    val type: String,

    val proof: Map<String, Any>,
)
