package com.rarilabs.rarime.api.erc20

import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.erc20.models.FeeResponse
import com.rarilabs.rarime.api.erc20.models.PermitHashRequest
import com.rarilabs.rarime.api.erc20.models.PermitHashResponse
import com.rarilabs.rarime.api.erc20.models.TokenResponse
import com.rarilabs.rarime.api.erc20.models.TransferErc20Request
import com.rarilabs.rarime.api.erc20.models.TransferErc20Response
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Erc20API {
    @GET("/token/balance")
    suspend fun getBalance(@Query("address") address: String): Response<TokenResponse>

    @POST("/transfer")
    suspend fun transfer(@Body body: TransferErc20Request): Response<TransferErc20Response>

    @POST("/transfer/permit-hash")
    suspend fun permitHash(@Body body: PermitHashRequest): Response<PermitHashResponse>

    @GET("/transfer")
    suspend fun getFee(@Body body: TransferErc20Request): Response<FeeResponse>

}

