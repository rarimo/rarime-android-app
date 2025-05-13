package com.rarilabs.rarime.api.likeness.models

data class LikenessRequest(
    val data: RegisterRequestData
)

data class RegisterRequestData(
    val tx_data: String,
    val destination: String? = null,
    val no_send: Boolean = false,
    val meta: Map<String, String>? = null
)


data class LikenessResponse(
    val data: EvmTxResponseData
)

data class EvmTxResponseData(
    val id: String,
    val type: String,
    val attributes: EvmTxResponseAttributes
)

data class EvmTxResponseAttributes(
    val tx_hash: String
)