package com.rarilabs.rarime.api.voting.models

data class LatestOperationResponse(
    val data: Data,
    val included: List<Any>
)

data class Data(
    val id: String,
    val type: String,
    val attributes: Attributes
)

data class Attributes(
    val block_height: Long,

    val destination_chain: String,

    val operation_id: String,

    val proof: String,

    val tx_hash: String
)