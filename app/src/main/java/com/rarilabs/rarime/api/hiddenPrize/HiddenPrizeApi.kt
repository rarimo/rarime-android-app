package com.rarilabs.rarime.api.hiddenPrize

import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserRequest
import com.rarilabs.rarime.api.hiddenPrize.models.CreateUserResponse
import com.rarilabs.rarime.api.hiddenPrize.models.GetUserResponse
import com.rarilabs.rarime.api.hiddenPrize.models.HiddenPrizeClaimRequest
import com.rarilabs.rarime.api.hiddenPrize.models.HiddenPrizeClaimResponse
import com.rarilabs.rarime.api.hiddenPrize.models.SubmitGuessRequest
import com.rarilabs.rarime.api.hiddenPrize.models.SubmitGuessResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HiddenPrizeApi {

    @POST("/integrations/registration-relayer/v1/likeness-registry")
    suspend fun claimTokens(@Body request: HiddenPrizeClaimRequest): Response<HiddenPrizeClaimResponse>


    @POST("/integrations/guess-celebrity-svc/v1/public/users")
    suspend fun createNewUser(@Body body: CreateUserRequest): Response<CreateUserResponse>

    @GET("/integrations/guess-celebrity-svc/v1/public/users/{nullifier}")
    suspend fun getUserInfo(@Path("nullifier") nullifier: String): Response<GetUserResponse>

    @POST("/integrations/guess-celebrity-svc/v1/public/users/{nullifier}/extra")
    suspend fun addExtraAttemptSocialShare(@Path("nullifier") nullifier: String): Response<Unit>

    @POST("/integrations/guess-celebrity-svc/v1/public/users/{nullifier}/guess")
    suspend fun submitCelebrityGuess(
        @Path("nullifier") nullifier: String,
        @Body body: SubmitGuessRequest
    ): Response<SubmitGuessResponse>


}