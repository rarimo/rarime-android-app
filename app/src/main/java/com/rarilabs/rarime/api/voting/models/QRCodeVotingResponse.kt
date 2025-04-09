package com.rarilabs.rarime.api.voting.models

import com.google.gson.annotations.SerializedName

data class QRCodeVotingResponse(
    @SerializedName("data")
    val data: DataModel,

    @SerializedName("included")
    val included: List<Any> // or List<IncludedModel> if you know the type
)

data class DataModel(
    @SerializedName("id")
    val id: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("attributes")
    val attributes: AttributesModel
)

data class AttributesModel(
    @SerializedName("metadata")
    val metadata: MetadataModel
)

data class MetadataModel(
    @SerializedName("proposal_id")
    val proposal_id: Int
)