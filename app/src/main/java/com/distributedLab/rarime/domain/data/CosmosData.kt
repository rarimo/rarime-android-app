package com.distributedLab.rarime.domain.data

data class CosmosSpendableBalancesResponse(
    val balances: List<CosmosSpendableBalance> // Array in Swift becomes List in Kotlin
)

data class CosmosSpendableBalance(
    val denom: String,
    val amount: String
)

data class CosmosTransferResponse(
    val data: CosmosTransferData
)

data class CosmosTransferData(
    val id: String,
    val type: String,
    val attributes: CosmosTransferAttributes
)

data class CosmosTransferAttributes(
    val address: String,
    val amount: String,
    val created_at: String,
    val status: String,
    val tx_hash: String,
    val updated_at: String
)