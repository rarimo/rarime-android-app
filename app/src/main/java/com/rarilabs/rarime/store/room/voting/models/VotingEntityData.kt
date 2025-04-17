package com.rarilabs.rarime.store.room.voting.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.rarilabs.rarime.api.voting.models.Poll
import org.web3j.utils.Numeric
import java.lang.reflect.Type

@Entity(tableName = "voting")
data class VotingEntityData(
    @PrimaryKey
    val proposalId: Int,
    val votingBlob: String
) {

    companion object {
        fun fromVoteToVotingEntityData(vote: Poll): VotingEntityData {
            val voteJson = getGson().toJson(vote)
            return VotingEntityData(
                proposalId = vote.id.toInt(),
                votingBlob = voteJson
            )
        }

        private fun getGson(): Gson {
            return GsonBuilder()
                .registerTypeAdapter(ByteArray::class.java, object : JsonSerializer<ByteArray>,
                    JsonDeserializer<ByteArray> {
                    override fun serialize(
                        src: ByteArray,
                        typeOfSrc: Type,
                        context: JsonSerializationContext
                    ): JsonElement {
                        return JsonPrimitive(Numeric.toHexString(src))
                    }

                    override fun deserialize(
                        json: JsonElement,
                        typeOfT: Type,
                        context: JsonDeserializationContext
                    ): ByteArray {

                        return Numeric.hexStringToByteArray(json.asString)
                    }
                }).create()

        }

        fun fromEntityDataToVote(votingEntityData: VotingEntityData): Poll {
            val votingBlob = getGson().fromJson(votingEntityData.votingBlob, Poll::class.java)

            return votingBlob
        }
    }

}