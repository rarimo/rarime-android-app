package com.distributedLab.rarime.api.cosmos.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CosmosSpendableBalancesResponse(
    val balances: List<CosmosSpendableBalance> // Array in Swift becomes List in Kotlin
)

@JsonClass(generateAdapter = true)
data class CosmosSpendableBalance(
    val denom: String,
    val amount: String
)

@JsonClass(generateAdapter = true)
data class CosmosTransferResponse(
    val data: CosmosTransferData
)

@JsonClass(generateAdapter = true)
data class CosmosTransferData(
    val id: String,
    val type: String,
    val attributes: CosmosTransferAttributes
)

@JsonClass(generateAdapter = true)
data class CosmosTransferAttributes(
    val address: String,
    val amount: String,
    val created_at: String,
    val status: String,
    val tx_hash: String,
    val updated_at: String
)

@JsonClass(generateAdapter = true)
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

@JsonClass(generateAdapter = true)
data class Log(
    val msg_index: Int,
    val events: List<LogEvent>
)

@JsonClass(generateAdapter = true)
data class LogEvent(
    val type: String,
    val attributes: List<Attribute>
)

@JsonClass(generateAdapter = true)
data class Attribute(
    val key: String,
    val value: String
)

@JsonClass(generateAdapter = true)
data class Event(
    val type: String,
    val attributes: List<EventAttribute>
)

@JsonClass(generateAdapter = true)
data class EventAttribute(
    val key: String,
    val value: String,
    val index: Boolean
)