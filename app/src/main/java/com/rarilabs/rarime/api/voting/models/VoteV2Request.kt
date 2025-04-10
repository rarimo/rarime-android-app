package com.rarilabs.rarime.api.voting.models

data class SendTransactionRequest(
    val data: SendTransactionData
)

data class SendTransactionData(
    val type: String,
    val attributes: SendTransactionAttributes
)

data class SendTransactionAttributes(
    val tx_data: String,
    val destination: String
)