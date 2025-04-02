package com.rarilabs.rarime.api.voting.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParticipationProofInputs(
    @Json(name = "participationEventId") val participationEventId: String,
    @Json(name = "challengedEventId") val challengedEventId: String,
    @Json(name = "nullifiersTreeSiblings") val nullifiersTreeSiblings: List<String>,
    @Json(name = "nullifiersTreeRoot") val nullifiersTreeRoot: String,
    @Json(name = "skIdentity") val skIdentity: String
)