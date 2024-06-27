package com.rarilabs.rarime.api.registration

import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.registration.models.RegisterBody
import com.rarilabs.rarime.api.registration.models.RegisterResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationAPI {
    @POST("${BaseConfig.RELAYER_URL}/integrations/registration-relayer/v1/register")
    suspend fun register(@Body body: RegisterBody): RegisterResponseBody
}