package com.distributedLab.rarime.domain.points

import com.distributedLab.rarime.util.data.Proof
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PointsBalance(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,

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
)

@JsonClass(generateAdapter = true)
data class PointsWithdrawal(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,

    val amount: Long,
    val address: String,

    @Json(name = "created_at")
    val createdAt: Long,

    val balance: PointsBalance?,
)

@JsonClass(generateAdapter = true)
data class PointsPrice(
//    @Json(name = "id")
//    val type: String,

    val urmo: Long,
)

@JsonClass(generateAdapter = true)
data class CreateBalanceBody(
    @Json(name = "data") val data: CreateBalancePayload
)

@JsonClass(generateAdapter = true)
data class CreateBalancePayload(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,

    @Json(name = "attributes") val attributes: CreateBalanceAttributes
)

@JsonClass(generateAdapter = true)
data class CreateBalanceAttributes(
    @Json(name = "referred_by") val referredBy: String
)

@JsonClass(generateAdapter = true)
data class VerifyPassportBody(
    @Json(name = "data") val data: VerifyPassportPayload
)

@JsonClass(generateAdapter = true)
data class VerifyPassportPayload(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,

    val attributes: VerifyPassportAttributes
)

@JsonClass(generateAdapter = true)
data class VerifyPassportAttributes(
    val proof: Proof
)

@JsonClass(generateAdapter = true)
data class WithdrawBody(
    @Json(name = "data") val data: WithdrawPayload
)

@JsonClass(generateAdapter = true)
data class WithdrawPayload(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,

    val attributes: WithdrawPayloadAttributes,
)

@JsonClass(generateAdapter = true)
data class WithdrawPayloadAttributes(
    val amount: Long,
    val address: String,
    val proof: Proof, // FIXME: is it correct?
)

