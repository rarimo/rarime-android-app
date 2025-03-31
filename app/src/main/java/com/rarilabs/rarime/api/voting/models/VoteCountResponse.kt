package com.rarilabs.rarime.api.voting.models

import com.google.gson.annotations.SerializedName

data class VoteCountResponse(
    @SerializedName("data") val data: VoteCountData
)

data class VoteCountData(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("attributes") val attributes: VoteCountAttributes
)

data class VoteCountAttributes(
    @SerializedName("vote_count") val voteCount: Int
)