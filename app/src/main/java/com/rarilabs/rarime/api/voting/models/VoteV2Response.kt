package com.rarilabs.rarime.api.voting.models


import com.google.gson.annotations.SerializedName

data class VoteV2Response(
    @SerializedName("data") val data: TxData
)

data class TxData(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String
)