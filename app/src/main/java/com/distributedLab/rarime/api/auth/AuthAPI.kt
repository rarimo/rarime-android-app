package com.distributedLab.rarime.api.auth

import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.auth.models.AuthChallengeBody
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeBody
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeResponseBody
import com.distributedLab.rarime.api.auth.models.ValidateResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthAPI {
    @POST("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/authorize")
    suspend fun authorize(@Body payload: RequestAuthorizeBody): RequestAuthorizeResponseBody

    @GET("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/authorize/{nullifier}/challenge")
    suspend fun getChallenge(@Path("nullifier") nullifier: String): AuthChallengeBody

    @GET("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/refresh")
    suspend fun refresh(@Header("Authorization") authorization: String): RequestAuthorizeResponseBody

    @GET("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/validate")
    suspend fun validate(@Header("Authorization") authorization: String): ValidateResponseBody
}