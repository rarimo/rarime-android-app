package com.distributedLab.rarime.api.auth

import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.auth.models.AuthChallenge
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeBody
import com.distributedLab.rarime.api.auth.models.RequestAuthorizePayload
import com.distributedLab.rarime.api.auth.models.RequestAuthorizeResponse
import com.distributedLab.rarime.api.auth.models.ValidateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthAPI {
    @POST("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/authorize")
    fun authorize(@Body payload: RequestAuthorizeBody): RequestAuthorizeResponse

    @GET("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/authorize/{nullifier}/challenge")
    fun getChallenge(@Path("nullifier") nullifier: String): AuthChallenge

    @GET("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/refresh")
    fun refresh(@Header("Authorization") authorization: String): RequestAuthorizePayload

    @GET("${BaseConfig.RELAYER_URL}/integrations/decentralized-auth-svc/v1/validate")
    fun validate(@Header("Authorization") authorization: String): ValidateResponse
}