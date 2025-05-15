package com.rarilabs.rarime.api.hiddenPrize.models

data class SubmitGuessRequest(
    val data: Data
) {
    data class Data(
        val type: String = "feature_vector",
        val attributes: Attributes
    ) {
        data class Attributes(
            val features: List<Float>
        )
    }
}

data class SubmitGuessResponse(
    val data: GuessResult,
    val included: List<Included>
)

data class GuessResult(
    val type: String,
    val attributes: GuessAttributes
)

data class GuessAttributes(
    val success: Boolean,
    val original_feature_vector: List<Float>
)

sealed class Included {
    data class Stats(val userStats: UserStats) : Included()
    data class CelebrityItem(val celebrity: Celebrity) : IncludedItem()
}
