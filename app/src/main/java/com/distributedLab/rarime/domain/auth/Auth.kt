package com.distributedLab.rarime.domain.auth

import moe.banana.jsonapi2.JsonApi
import moe.banana.jsonapi2.Resource

data class RequestAuthorizePayload(
    val id: String,
    val type: String,
    val attributes: RequestAuthorizePayloadAttributes,
)

data class RequestAuthorizePayloadAttributes(
    val proof: Map<String, Any>,
)

@JsonApi(type = "request-response")
data class RequestAuthorizeResponse(
    val accessToken: AuthToken,
    val refreshToken: AuthToken,
) : Resource()

data class AuthToken(
    val token: String,
    val tokenType: String,
)

@JsonApi(type = "challenge")
data class AuthChallenge(
    val challenge: String,
) : Resource()

@JsonApi(type = "validate")
data class ValidateResponse(
    val claims: List<Claim>,
) : Resource()

data class Claim(
    val address: String,
    val nullifier: String,
)
