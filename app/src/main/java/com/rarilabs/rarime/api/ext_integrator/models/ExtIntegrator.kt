package com.rarilabs.rarime.api.ext_integrator.models

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
    @Json(name = "callback_url") val callbackUrl: String,
    @Json(name = "data_url") val dataUrl: String,
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
data class ProofParametersRequest(
    val id: String = "nikita@gmail.com",
    val type: String = "proof_parameters",
    val birthDateLowerBound: String = "0x303030303030",
    val birthDateUpperBound: String = "0x303630383239",
    val callbackUrl: String = "https://api.orgs.app.stage.rarime.com/integrations/verificator-svc/public/callback/0x28906909b4f99dca8d10cf00ff182f8942e75fb57caa1083a942350d6d7c930b",
    val citizenshipMask: String = "0x554B52204745543A202F696E746567726174696F6E732F7665726966696361746F722D7376632F707269766174652F766572696669636174696F6E2D7374617475732F6E696B69746140676D61696C2E636F6D20504F53543A202F696E746567726174696F6E732F7665726966696361746F722D7376632F7075626C69632F63616C6C6261636B2F307832383930363930396234663939646361386431306366303066663138326638393432653735666235376361613130383361393432333530643664376339333062",
    val eventData: String = "0x28906909b4f99dca8d10cf00ff182f8942e75fb57caa1083a942350d6d7c930b",
    val eventId: String = "111186066134341633902189494613533900917417361106374681011849132651019822199",
    val expirationDateLowerBound: String = "52983525027888",
    val expirationDateUpperBound: String = "52983525027888",
    val identityCounter: Int = 0,
    val identityCounterLowerBound: Int = 0,
    val identityCounterUpperBound: Int = 1,
    val selector: String = "35361",
    val timestampLowerBound: String = "0",
    val timestampUpperBound: String = "19000000000",
)