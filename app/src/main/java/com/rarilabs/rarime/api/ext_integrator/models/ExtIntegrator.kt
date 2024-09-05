package com.rarilabs.rarime.api.ext_integrator.models

import android.content.Context
import com.rarilabs.rarime.R
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
) {
    fun getTitle(context: Context): String {
        return when(type) {
            ExtIntegratorActions.SignTypedData.value -> context.getString(R.string.ext_action_sign_typed_data)
            ExtIntegratorActions.Authorize.value -> context.getString(R.string.ext_action_authorize)
            ExtIntegratorActions.QueryProofGen.value -> context.getString(R.string.ext_action_query_proof_gen)
            else -> "Unknown"
        }
    }

    fun getPreviewFields(context: Context): List<Pair<String, String>> {
        return when(type) {
            ExtIntegratorActions.SignTypedData.value -> {
                listOf(
                    Pair(context.getString(R.string.ext_action_sign_typed_data_payload), payload ?: ""),
                    Pair(context.getString(R.string.ext_action_sign_typed_data_callback_url), callbackUrl),
                )
            }
            ExtIntegratorActions.Authorize.value -> {
                listOf(
                    Pair(context.getString(R.string.ext_action_authorize_payload), payload ?: ""),
                    Pair(context.getString(R.string.ext_action_authorize_callback_url), callbackUrl),
                )
            }
            ExtIntegratorActions.QueryProofGen.value -> {
                listOf(
                    Pair(context.getString(R.string.ext_action_query_proof_gen_id), id),
                    Pair(context.getString(R.string.ext_action_query_proof_gen_callback_url), callbackUrl),
                    Pair(context.getString(R.string.ext_action_query_proof_gen_data_url), dataUrl ?: ""),
                )
            }
            else -> emptyList()
        }
    }

    fun getSuccessMessage(context: Context): String {
        return when(type) {
            ExtIntegratorActions.SignTypedData.value -> context.getString(R.string.ext_action_sign_typed_data_success)
            ExtIntegratorActions.Authorize.value -> context.getString(R.string.ext_action_authorize_success)
            ExtIntegratorActions.QueryProofGen.value -> context.getString(R.string.ext_action_query_proof_gen_success)
            else -> "Unknown"
        }
    }

    fun getFailMessage(context: Context): String {
        return when(type) {
            ExtIntegratorActions.SignTypedData.value -> context.getString(R.string.ext_action_sign_typed_data_fail)
            ExtIntegratorActions.Authorize.value -> context.getString(R.string.ext_action_authorize_fail)
            ExtIntegratorActions.QueryProofGen.value -> context.getString(R.string.ext_action_query_proof_gen_fail)
            else -> "Unknown"
        }
    }
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
    @Json(name = "request_data")  val requestData: String,
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