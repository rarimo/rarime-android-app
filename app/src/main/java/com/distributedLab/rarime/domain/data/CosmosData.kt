package com.distributedLab.rarime.domain.data

data class CosmosSpendableBalancesResponse(
    val balances: List<CosmosSpendableBalance> // Array in Swift becomes List in Kotlin
)

data class CosmosSpendableBalance(
    val denom: String,
    val amount: String
)

data class CosmosTransferResponse(
    val txhash: String
)

data class Log(
    val msg_index: Int,
    val events: List<LogEvent>
)

data class LogEvent(
    val type: String,
    val attributes: List<Attribute>
)

data class Attribute(
    val key: String,
    val value: String
)

data class Event(
    val type: String,
    val attributes: List<EventAttribute>
)

data class EventAttribute(
    val key: String,
    val value: String,
    val index: Boolean
)