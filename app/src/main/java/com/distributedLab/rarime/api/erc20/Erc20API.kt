package com.distributedLab.rarime.api.erc20

import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.erc20.models.FeeResponse
import com.distributedLab.rarime.api.erc20.models.PermitHashRequest
import com.distributedLab.rarime.api.erc20.models.PermitHashResponse
import com.distributedLab.rarime.api.erc20.models.TokenResponse
import com.distributedLab.rarime.api.erc20.models.TransferErc20Request
import com.distributedLab.rarime.api.erc20.models.TransferErc20Response
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Erc20API {
    @GET("${BaseConfig.EVM_SERVICE_URL}/token/balance")
    suspend fun getBalance(@Query("address") address: String): Response<TokenResponse>

    @POST("${BaseConfig.EVM_SERVICE_URL}/transfer")
    suspend fun transfer(@Body body: TransferErc20Request): Response<TransferErc20Response>

    @POST("${BaseConfig.EVM_SERVICE_URL}/transfer/permit-hash")
    suspend fun permitHash(@Body body: PermitHashRequest): Response<PermitHashResponse>

    @GET("${BaseConfig.EVM_SERVICE_URL}/transfer")
    suspend fun getFee(@Body body: TransferErc20Request): Response<FeeResponse>

}

