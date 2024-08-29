package com.rarilabs.rarime.api.ext_integrator

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
}