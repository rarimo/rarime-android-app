package com.distributedLab.rarime.domain.data

import com.distributedLab.rarime.util.data.ZkProof
import java.io.Serializable


data class AirdropRequest(
    val data: AirdropRequestData
): Serializable

data class AirdropRequestData(
    val type: String,
    val attributes: AirdropRequestAttributes
): Serializable

data class AirdropRequestAttributes(
    val address: String,
    val algorithm: String,
    val zk_proof: ZkProof
): Serializable

data class AirdropResponse(
    val data: AirdropResponseData,
    val included: List<String> // An array in Swift can be represented as a list in Kotlin
): Serializable

data class AirdropResponseData(
    val id: String,
    val type: String,
    val attributes: AirdropResponseAttributes
): Serializable

data class AirdropResponseAttributes(
    val address: String,
    val amount: String,
    val created_at: String,
    val status: String,
    val tx_hash: String,
    val updated_at: String
): Serializable