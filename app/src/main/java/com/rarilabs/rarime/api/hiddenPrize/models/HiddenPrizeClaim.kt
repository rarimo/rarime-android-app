package com.rarilabs.rarime.api.hiddenPrize.models


data class HiddenPrizeClaimRequest(
    val data: HiddenPrizeClaimData
)

data class HiddenPrizeClaimData(
    val tx_data: String,
    val destination: String? = null,
    val no_send: Boolean = false,
    val meta: Map<String, String>? = null
)


data class HiddenPrizeClaimResponse(
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