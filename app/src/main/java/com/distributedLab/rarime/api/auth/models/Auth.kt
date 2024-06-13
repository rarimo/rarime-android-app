package com.distributedLab.rarime.api.auth.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestAuthorizeBody(
    val data: RequestAuthorizePayload,
)

@JsonClass(generateAdapter = true)
data class RequestAuthorizePayload(
    val id: String,
    val type: String,

    val attributes: RequestAuthorizePayloadAttributes,
)

@JsonClass(generateAdapter = true)
data class RequestAuthorizePayloadAttributes(
    val proof: Map<String, Any>,
)

@JsonClass(generateAdapter = true)
data class RequestAuthorizeResponse(
    val id: String,
    val type: String,

    val accessToken: AuthToken,
    val refreshToken: AuthToken,
)

@JsonClass(generateAdapter = true)
data class AuthToken(
    val token: String,
    val tokenType: String,
)

@JsonClass(generateAdapter = true)
data class AuthChallenge(
    val id: String,
    val type: String,

    val challenge: String,
)

@JsonClass(generateAdapter = true)
data class ValidateResponse(
    val id: String,
    val type: String,

    val claims: List<Claim>,
)
@JsonClass(generateAdapter = true)

data class Claim(
    val address: String,
    val nullifier: String,
)
