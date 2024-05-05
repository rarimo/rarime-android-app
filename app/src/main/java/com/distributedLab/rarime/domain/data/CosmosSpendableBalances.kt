package com.distributedLab.rarime.domain.data

data class CosmosSpendableBalancesResponse(
    val balances: List<CosmosSpendableBalance> // Array in Swift becomes List in Kotlin
)

data class CosmosSpendableBalance(
    val denom: String,
    val amount: String
)