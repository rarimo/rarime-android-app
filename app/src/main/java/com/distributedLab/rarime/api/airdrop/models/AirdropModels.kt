package com.distributedLab.rarime.api.airdrop.models

import com.distributedLab.rarime.util.data.ZkProof
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateAirDropBody(
    val data: CreateAirDrop,
)

@JsonClass(generateAdapter = true)
data class CreateAirDrop(
    val type: String,
    val attributes: CreateAirDropAttributes,
)

@JsonClass(generateAdapter = true)
data class CreateAirDropAttributes(
    val address: String,

    @JsonProperty("zk_proof")
    val zkProof: ZkProof,
)

@JsonClass(generateAdapter = true)
data class AirDropResponseBody(
    val data: Airdrop,
)

@JsonClass(generateAdapter = true)
data class Airdrop(
    val id: String,
    val type: String,
    val attributes: AirdropAttributes,
)

@JsonClass(generateAdapter = true)
data class AirdropAttributes(
    val address: String,
    val nullifier: String,
    val status: String,

    @JsonProperty("created_at")
    val createdAt: String,

    @JsonProperty("updated_at")
    val updatedAt: String,

    val amount: String,

    @JsonProperty("tx_hash")
    val txHash: String,
)

@JsonClass(generateAdapter = true)
data class AirdropEventParamsBody(
    val data: AirdropEventParams,
)

@JsonClass(generateAdapter = true)
data class AirdropEventParams(
    val id: String,
    val type: String,
    val attributes: AirdropEventParamsAttributes,
)

@JsonClass(generateAdapter = true)
data class AirdropEventParamsAttributes(
    @JsonProperty("event_id")
    val eventId: String,
    @JsonProperty("started_at")
    val startedAt: Long,
    @JsonProperty("query_selector")
    val querySelector: Long,
)
