package com.rarilabs.rarime.api.registration.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponseBody(
    val data: RegisterResponse,
    val included: List<String>
)

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    val id: String,
    val type: String,
    val attributes: RegisterResponseAttributes
)

@JsonClass(generateAdapter = true)
data class RegisterResponseAttributes(
    val tx_hash: String
)


@JsonClass(generateAdapter = true)
data class RegisterBody(
    val data: RegisterData
)

@JsonClass(generateAdapter = true)
data class RegisterData(
    val tx_data: String,
    val no_send: Boolean,
    val destination: String
)