package com.rarilabs.rarime.api.voting.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.util.Base64

data class ProposalIndexed(
    val ProposalInfo: ProposalInfo,
    val Index: Int
)

data class ProposalInfo(
    val ProposalSMT: String,
    val Status: Int,
    val Config: Config,
    val VotingResults: List<List<Long>>
)

data class Config(
    val StartTimestamp: Long,
    val Duration: Int,
    val Multichoice: Int,
    val AcceptedOptions: List<Int>,
    val Description: String,
    val VotingWhitelist: List<String>,
    val VotingWhitelistData: List<ByteArray>
)

data class QuestionResultOption(
    val answer: String,
    val votes: Int
)

data class QuestionResult(
    val question: String,
    val options: List<QuestionResultOption>
)

class ByteArrayDeserializer : JsonDeserializer<ByteArray> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ByteArray {
        return Base64.getDecoder().decode(json.asString)
    }
}

class ByteArrayListDeserializer : JsonDeserializer<List<ByteArray>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): List<ByteArray> {
        val jsonArray = json.asJsonArray
        return jsonArray.map { Base64.getDecoder().decode(it.asString) }
    }
}