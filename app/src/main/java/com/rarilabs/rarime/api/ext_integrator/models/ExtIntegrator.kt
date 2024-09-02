package com.rarilabs.rarime.api.ext_integrator.models

import com.rarilabs.rarime.util.data.ZkProof
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class ExtIntegratorActions(val value: String) {
    SignTypedData("signTypedData"),
    Authorize("authorize"),
    QueryProofGen("QueryProofGen"),
}

@JsonClass(generateAdapter = true)
data class QrAction(
    val id: String,
    val type: String,
    val payload: String? = null,
    @Json(name = "callback_url") val callbackUrl: String,
    @Json(name = "data_url") val dataUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class RequestDataResponse(
    val data: RequestDataResponseData,
)

@JsonClass(generateAdapter = true)
data class RequestDataResponseData(
    val attributes: RequestDataResponseAttributes,
)

@JsonClass(generateAdapter = true)
data class RequestDataResponseAttributes(
    @Json(name = "request_data")  val requestData: String,
)

@JsonClass(generateAdapter = true)
data class QueryProofGenResponse(
    val data: QueryProofGenResponseData,
)

@JsonClass(generateAdapter = true)
data class QueryProofGenResponseData(
    val id: String,
    val type: String = "proof_parameters",
    val attributes: QueryProofGenResponseAttributes,
)

@JsonClass(generateAdapter = true)
data class QueryProofGenResponseAttributes(
    val birthDateLowerBound: String,
    val birthDateUpperBound: String,
    val callbackUrl: String,
    val citizenshipMask: String,
    val eventData: String,
    val eventId: String,
    val expirationDateLowerBound: String,
    val expirationDateUpperBound: String,
    val identityCounter: Int,
    val identityCounterLowerBound: Int,
    val identityCounterUpperBound: Int,
    val selector: String,
    val timestampLowerBound: String,
    val timestampUpperBound: String
)

@JsonClass(generateAdapter = true)
data class QueryProofGenCallbackRequest(
    val data: QueryProofGenCallbackRequestData,
)

@JsonClass(generateAdapter = true)
data class QueryProofGenCallbackRequestData(
    val id: String,
    val type: String = "receive_proof",
    val attributes: QueryProofGenCallbackRequestAttributes,
)

@JsonClass(generateAdapter = true)
data class QueryProofGenCallbackRequestAttributes(
    val proof: ZkProof,
)