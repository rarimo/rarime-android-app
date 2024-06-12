package com.distributedLab.rarime.domain.auth

import com.distributedLab.rarime.BaseConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface JsonApiAuthSvcManager {
    @POST("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/authorize")
    fun authorize(@Body payload: RequestAuthorizePayload): Response<RequestAuthorizeResponse>

    @GET("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/authorize/{nullifier}/challenge")
    fun getChallenge(@Path("nullifier") nullifier: String): Response<AuthChallenge>

    @GET("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/refresh")
    fun refresh(@Header("Authorize") authorize: String): Response<RequestAuthorizePayload>

    @GET("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/validate")
    fun validate(@Header("Authorize") authorize: String): Response<ValidateResponse>
}