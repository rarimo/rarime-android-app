package com.rarilabs.rarime.api.ext_integrator

import com.rarilabs.rarime.api.ext_integrator.models.LightSignatureCallbackRequest
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenCallbackRequest
import com.rarilabs.rarime.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.rarime.api.ext_integrator.models.RequestDataResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ExtIntegratorAPI {
    @POST
    suspend fun callback(@Url url: String, @Body payload: String)

    @GET
    suspend fun getRequestData(@Url url: String): RequestDataResponse

    @POST
    suspend fun queryProofCallback(@Url url: String, @Body payload: QueryProofGenCallbackRequest)

    @POST
    suspend fun lightSignatureCallback(@Url url: String, @Body payload: LightSignatureCallbackRequest)

    @GET
    suspend fun queryProofData(@Url url: String): QueryProofGenResponse

    @GET
    suspend fun queryIpfsData(@Url url: String): Any
}