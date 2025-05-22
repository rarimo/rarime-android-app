package com.rarilabs.rarime.api.hiddenPrize

import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserRequest
import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserResponse
import com.rarilabs.rarime.api.hiddenPrize.models.HiddenPrizeClaimRequest
import com.rarilabs.rarime.api.hiddenPrize.models.HiddenPrizeClaimResponse
import com.rarilabs.rarime.api.hiddenPrize.models.SubmitGuessRequest
import com.rarilabs.rarime.api.hiddenPrize.models.SubmitGuessResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface HiddenPrizeApi {

    @POST("/integrations/registration-relayer/v1/register")
    suspend fun claimTokens(
        @Body request: HiddenPrizeClaimRequest, @Header("Authorization") authorization: String,
    ): Response<HiddenPrizeClaimResponse>


    @POST("/integrations/guess-celebrity-svc/v1/public/users")
    suspend fun createNewUser(
        @Body body: CreateUserRequest, @Header("Authorization") authorization: String
    ): Response<CreateUserResponse>

    @GET("/integrations/guess-celebrity-svc/v1/public/users/{nullifier}")
    suspend fun getUserInfo(
        @Path("nullifier") nullifier: String, @Header("Authorization") authorization: String,
    ): Response<CreateUserResponse>

    @POST("/integrations/guess-celebrity-svc/v1/public/users/{nullifier}/extra")
    suspend fun addExtraAttemptSocialShare(
        @Path("nullifier") nullifier: String, @Header("Authorization") authorization: String,
    ): Response<Unit>

    @POST("/integrations/guess-celebrity-svc/v1/public/users/{nullifier}/guess")
    suspend fun submitCelebrityGuess(
        @Path("nullifier") nullifier: String,
        @Body body: SubmitGuessRequest,
        @Header("Authorization") authorization: String,

        ): Response<SubmitGuessResponse>


}