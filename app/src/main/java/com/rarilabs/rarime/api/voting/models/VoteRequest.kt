package com.rarilabs.rarime.api.voting.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class VoteRequest(
    val data: VoteRequestData
)

@JsonClass(generateAdapter = true)

data class VoteRequestData(
    val type: String, val attributes: VoteRequestAttributes
)

@JsonClass(generateAdapter = true)

data class VoteRequestAttributes(
    @Json(name = "tx_data") val txData: String,
    val destination: String,
    @Json(name = "proposal_id") val proposalId: Long
)

@JsonClass(generateAdapter = true)
data class VoteResponse(
    val data: VoteResponseData
)

@JsonClass(generateAdapter = true)

data class VoteResponseData(
    val id: String, val type: String
)