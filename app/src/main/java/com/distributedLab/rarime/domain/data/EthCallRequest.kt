package com.distributedLab.rarime.domain.data

data class EthCallRequest(
    val jsonrpc: String,
    val method: String,
    val params: List<Any>,
    val id: String
) {
    data class Params(
        val to: String,
        val data: String
    )
}

data class EthCallResponse(
    val jsonrpc: String,
    val id: Int,
    val result: String
)
