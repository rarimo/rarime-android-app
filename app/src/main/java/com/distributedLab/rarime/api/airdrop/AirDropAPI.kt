package com.distributedLab.rarime.api.airdrop

import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.airdrop.models.AirdropEventParamsBody
import com.distributedLab.rarime.api.airdrop.models.AirDropResponseBody
import com.distributedLab.rarime.api.airdrop.models.CreateAirDropBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AirDropAPI {
    @POST("${BaseConfig.RELAYER_URL}/integrations/airdrop-svc/airdrops")
    suspend fun createAirDrop(@Body body: CreateAirDropBody): AirDropResponseBody

    @GET("${BaseConfig.RELAYER_URL}/integrations/airdrop-svc/airdrops/{nullifier}")
    suspend fun getAirDropByNullifier(@Path("nullifier") nullifier: String): AirDropResponseBody

    @GET("${BaseConfig.RELAYER_URL}/integrations/airdrop-svc/airdrops/params")
    suspend fun getAirDropParams(): AirdropEventParamsBody
}