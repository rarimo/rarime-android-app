package com.rarilabs.rarime.api.hiddenPrize.models

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

sealed class Included {
    data class UserStats(
        val id: Long,
        val type: String = "user_stats", // should be "user_stats"
        val attributes: UserStatsAttributes?
    ) : Included()

    data class Celebrity(
        val id: Long,
        val type: String = "celebrity", // should be "celebrity"
        val attributes: CelebrityAttributes?
    ) : Included()
}

data class UserStatsAttributes(
    val attempts_left: Int,
    val extra_attempts_left: Int,
    val total_attempts_count: Int,
    val reset_time: Long
)

data class CelebrityAttributes(
    val title: String,
    val description: String,
    val status: String,
    val image: String?,
    val hint: String?
)

class IncludedJsonAdapter(
    private val moshi: Moshi
) : JsonAdapter<Included>() {

    private val userStatsAdapter = moshi.adapter(Included.UserStats::class.java)
    private val celebrityAdapter = moshi.adapter(Included.Celebrity::class.java)

    override fun fromJson(reader: JsonReader): Included? {
        val peekedJson = reader.readJsonValue() as? Map<*, *> ?: return null
        val type = peekedJson["type"] as? String ?: return null

        return when (type) {
            "user_stats" -> userStatsAdapter.fromJsonValue(peekedJson)
            "celebrity" -> celebrityAdapter.fromJsonValue(peekedJson)
            else -> null // ignore unknown types
        }
    }

    override fun toJson(writer: JsonWriter, value: Included?) {
        throw UnsupportedOperationException("Serialization not supported")
    }
}