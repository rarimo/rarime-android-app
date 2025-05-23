package com.rarilabs.rarime.api.ext_integrator.models

import com.rarilabs.rarime.util.data.ZkProof
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class ExtIntegratorActions(val value: String) {
    SignTypedData("signTypedData"),
    Authorize("authorize"),
    QueryProofGen("proof-request"),
    LightVerification("light-verification"),
    Vote("voting"),
}

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
    @Json(name = "request_data") val requestData: String,
)

@JsonClass(generateAdapter = true)
data class QueryProofGenResponse(
    val data: QueryProofGenResponseData,
)

@JsonClass(generateAdapter = true)
data class QueryProofGenResponseData(
    val id: String,
    val type: String = "get_proof_params",
    val attributes: QueryProofGenResponseAttributes,
)

@JsonClass(generateAdapter = true)
data class QueryProofGenResponseAttributes(
    val birth_date_lower_bound: String,
    val birth_date_upper_bound: String,
    val citizenship_mask: String,
    val event_data: String,
    val event_id: String,
    val expiration_date_lower_bound: String,
    val expiration_date_upper_bound: String,
    val identity_counter: Int,
    val identity_counter_lower_bound: Int,
    val identity_counter_upper_bound: Int,
    val selector: String,
    val timestamp_lower_bound: String,
    val timestamp_upper_bound: String,

    val callback_url: String,
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

@JsonClass(generateAdapter = true)
data class LightSignatureCallbackRequest(
    val data: LightSignatureCallbackRequestData,
)

@JsonClass(generateAdapter = true)
data class LightSignatureCallbackRequestData(
    val id: String,
    val type: String = "receive_signature",
    val attributes: LightSignatureCallbackRequestAttributes,
)

@JsonClass(generateAdapter = true)
data class LightSignatureCallbackRequestAttributes(
    val pub_signals: List<String>,
    val signature: String,
)
