package com.rarilabs.rarime.api.erc20.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable


data class TokenResponse(
    val data: TokenData
) : Serializable

data class TokenData(
    val id: String, val type: String, val attributes: TokenAttributes
) : Serializable

data class TokenAttributes(
    val amount: String
) : Serializable


@JsonClass(generateAdapter = true)
data class TransferErc20Request(
    val data: Data
)


@JsonClass(generateAdapter = true)
data class Data(
    val type: String, val attributes: Attributes
)


@JsonClass(generateAdapter = true)
data class Attributes(
    val sender: String,
    val receiver: String,
    val amount: Long,
    val deadline: Long,
    val r: String,
    val s: String,
    val v: Int
)


data class TransferErc20Response(
    val data: Data
) : Serializable {

    data class Data(
        val type: String, val attributes: Attributes
    ) : Serializable {

        data class Attributes(
            val hash: String, val amount: Int, val fee: Int
        ) : Serializable
    }
}


@JsonClass(generateAdapter = true)
data class PermitHashRequest(
    @Json(name = "data") val data: PermitHashDataRequest
)

@JsonClass(generateAdapter = true)
data class PermitHashDataRequest(
    @Json(name = "type") val type: String,
    @Json(name = "attributes") val attributes: PermitHashAttributesRequest
)

@JsonClass(generateAdapter = true)
data class PermitHashAttributesRequest(
    @Json(name = "sender") val sender: String,
    @Json(name = "amount") val amount: Long,
    @Json(name = "deadline") val deadline: Long
)


@JsonClass(generateAdapter = true)
data class PermitHashResponse(
    @Json(name = "data") val data: PermitHashDataResponse
)

@JsonClass(generateAdapter = true)
data class PermitHashDataResponse(
    @Json(name = "type") val type: String,
    @Json(name = "attributes") val attributes: PermitHashAttributesResponse
)

@JsonClass(generateAdapter = true)
data class PermitHashAttributesResponse(
    @Json(name = "hash") val hash: String
)


@JsonClass(generateAdapter = true)
data class FeeResponse(
    val data: Data
)

data class FeeData(
    val type: String,
    val attributes: Attributes
)

data class FeeAttributes(
    val amount: Int,
    val fee: Int
)