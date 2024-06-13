package com.distributedLab.rarime.api.registration.models

data class RegisterResponseBody(
    val data: RegisterResponse,
    val included: List<String>
)

data class RegisterResponse(
    val id: String,
    val type: String,
    val attributes: RegisterResponseAttributes
)

data class RegisterResponseAttributes(
    val tx_hash: String
)


data class RegisterBody(
    val data: RegisterData
)

data class RegisterData(
    val tx_data: String
)