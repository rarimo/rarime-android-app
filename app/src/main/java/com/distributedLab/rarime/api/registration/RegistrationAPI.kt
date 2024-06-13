package com.distributedLab.rarime.api.registration

import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.registration.models.RegisterResponseBody
import com.distributedLab.rarime.api.registration.models.RegisterBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationAPI {
    @POST("${BaseConfig.RELAYER_URL}/integrations/registration-relayer/v1/register")
    suspend fun register(@Body body: RegisterBody): Response<RegisterResponseBody>
}