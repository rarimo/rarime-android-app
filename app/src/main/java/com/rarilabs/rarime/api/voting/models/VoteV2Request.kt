package com.rarilabs.rarime.api.voting.models

import com.google.gson.annotations.SerializedName

data class SendTransactionRequest(
    @SerializedName("data") val data: SendTransactionData
)

data class SendTransactionData(
    @SerializedName("type") val type: String,
    @SerializedName("attributes") val attributes: SendTransactionAttributes
)

data class SendTransactionAttributes(
    @SerializedName("tx_data") val txData: String,
    @SerializedName("destination") val destination: String
)