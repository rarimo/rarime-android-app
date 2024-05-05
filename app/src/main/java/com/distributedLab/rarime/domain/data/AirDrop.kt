package com.distributedLab.rarime.domain.data

import com.distributedLab.rarime.util.data.ZkProof


data class AirdropRequest(
    val data: AirdropRequestData
)

data class AirdropRequestData(
    val type: String,
    val attributes: AirdropRequestAttributes
)

data class AirdropRequestAttributes(
    val address: String,
    val algorithm: String,
    val zk_proof: ZkProof
)

data class AirdropResponse(
    val data: AirdropResponseData,
    val included: List<String> // An array in Swift can be represented as a list in Kotlin
)

data class AirdropResponseData(
    val id: String,
    val type: String,
    val attributes: AirdropResponseAttributes
)

data class AirdropResponseAttributes(
    val address: String,
    val amount: String,
    val created_at: String,
    val status: String,
    val tx_hash: String,
    val updated_at: String
)