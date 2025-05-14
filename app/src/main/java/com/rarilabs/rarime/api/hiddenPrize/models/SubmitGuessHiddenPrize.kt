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

data class Included(
    val id: Int,
    val type: String,
    val attributes: UserStatsAttributes
)

data class UserStatsAttributes(
    val attempts_left: Int,
    val extra_attempts_left: Int,
    val total_attempts_count: Int,
    val reset_time: Long
)