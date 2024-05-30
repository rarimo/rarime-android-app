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



data class Transaction(
    val height: Long,
    val txhash: String,
    val data: String,
    val raw_log: String,
    val logs: List<Log>,
    val gas_wanted: Long,
    val gas_used: Long,
    val events: List<Event>
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