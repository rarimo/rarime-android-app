package com.rarilabs.rarime.api.hiddenPrize.models


data class SubmitGuessRequest(val data: SubmitGuessRequestData)

data class SubmitGuessRequestData(
    val type: String = "feature_vector", val attributes: Attributes
) {
    data class Attributes(
        val features: List<Float>
    )
}

data class SubmitGuessResponse(
    val data: GuessResult, val included: List<Included>? = null
)

data class GuessResult(
    val type: String, val attributes: GuessAttributes
)

data class GuessAttributes(
    val success: Boolean, val original_feature_vector: List<Float>?
)
