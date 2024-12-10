package com.rarilabs.rarime.api.registration

import com.rarilabs.rarime.api.registration.models.RegisterBody
import com.rarilabs.rarime.api.registration.models.RegisterResponseBody
import com.rarilabs.rarime.api.registration.models.VerifySodRequest
import com.rarilabs.rarime.api.registration.models.VerifySodResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationAPI {
    @POST("/integrations/registration-relayer/v1/register")
    suspend fun register(@Body body: RegisterBody): Response<RegisterResponseBody>

    @POST("/intergrations/incognito-light-registrator/v1/register")
    suspend fun incognitoLightRegistrator(@Body body: VerifySodRequest): Response<VerifySodResponse>
}