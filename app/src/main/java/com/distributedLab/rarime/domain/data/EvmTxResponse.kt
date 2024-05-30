package com.distributedLab.rarime.domain.data

data class EvmTxResponse(
    val data: EvmTxResponseData,
    val included: List<String>
)

data class EvmTxResponseData(
    val id: String,
    val type: String,
    val attributes: EvmTxResponseAttributes
)

data class EvmTxResponseAttributes(
    val tx_hash: String
)


data class RegisterRequest(
    val data: RegisterRequestData
)

data class RegisterRequestData(
    val tx_data: String
)