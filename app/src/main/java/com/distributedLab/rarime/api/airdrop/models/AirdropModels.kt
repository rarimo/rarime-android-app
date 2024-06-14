package com.distributedLab.rarime.api.airdrop.models

import com.distributedLab.rarime.util.data.ZkProof
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.JsonClass

enum class AirDropStatuses(val value: String) {
    COMPLETED("completed"),
    PENDING("pending"),
}

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

//    @JsonProperty("zk_proof")
    val zk_proof: ZkProof,
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

    // FIXME: change to enum
    val status: String,

//    @JsonProperty("created_at")
    val created_at: String,

//    @JsonProperty("updated_at")
    val updated_at: String,

    val amount: String,

//    @JsonProperty("tx_hash")
    val tx_hash: String?,
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
//    @JsonProperty("event_id")
    val event_id: String,
//    @JsonProperty("started_at")
    val started_at: Long,
//    @JsonProperty("query_selector")
    val query_selector: String,
)
