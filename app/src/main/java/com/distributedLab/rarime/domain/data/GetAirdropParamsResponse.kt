package com.distributedLab.rarime.domain.data

import java.io.Serializable


data class GetAirdropParamsResponse(
    val data: GetAirdropParamsResponseData
) : Serializable

data class GetAirdropParamsResponseData(
    val id: String, val type: String, val attributes: GetAirdropParamsResponseAttributes
) : Serializable


data class GetAirdropParamsResponseAttributes(
    val event_id: String, val query_selector: String, val started_at: Int
) : Serializable