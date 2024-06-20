package com.distributedLab.rarime.api.auth.models

import com.distributedLab.rarime.util.data.ZkProof
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestAuthorizeBody(
    val data: RequestAuthorizeData,
)

@JsonClass(generateAdapter = true)
data class RequestAuthorizeData(
    val id: String,
    val type: String = "authorize",

    val attributes: RequestAuthorizeDataAttributes,
)

@JsonClass(generateAdapter = true)
data class RequestAuthorizeDataAttributes(
    val proof: ZkProof,
)

@JsonClass(generateAdapter = true)
data class RequestAuthorizeResponseBody(
    val data: RequestAuthorizeResponseData,
)

@JsonClass(generateAdapter = true)
data class RequestAuthorizeResponseData(
    val id: String,
    val type: String,

    val attributes: RequestAuthorizeResponseDataAttributes
)

data class RequestAuthorizeResponseDataAttributes(
    val access_token: AuthToken,
    val refresh_token: AuthToken,
)

@JsonClass(generateAdapter = true)
data class AuthToken(
    val token: String,
    val token_type: String,
)

@JsonClass(generateAdapter = true)
data class AuthChallengeBody(
    val data: AuthChallengeData,
)

@JsonClass(generateAdapter = true)
data class AuthChallengeData(
    val id: String,
    val type: String = "challenge",

    val attributes: AuthChallengeDataAttributes,
)

@JsonClass(generateAdapter = true)
data class AuthChallengeDataAttributes(
    val challenge: String,
)

@JsonClass(generateAdapter = true)
data class ValidateResponseBody(
    val data: ValidateResponse
)

@JsonClass(generateAdapter = true)
data class ValidateResponse(
    val id: String,
    val type: String = "validation",

    val attributes: ValidateResponseAttributes,
)

@JsonClass(generateAdapter = true)
data class ValidateResponseAttributes(
    val claims: List<Claim>,
)

@JsonClass(generateAdapter = true)
data class Claim(
    val nullifier: String,
)
