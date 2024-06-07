package com.distributedLab.rarime.domain.points
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface JsonApiPointsSvcManager {
    /* BALANCE */

    @POST("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/balances")
    suspend fun createPointsBalance(@Body payload: CreateBalancePayload): Response<PointsBalance>

    @GET("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/balances")
    suspend fun getLeaderboard(): Response<List<PointsBalance>>

    @GET("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/balances/{nullifier}")
    suspend fun getPointsBalance(@Path("nullifier") nullifier: String): Response<PointsBalance>

    @PATCH("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/balances/{nullifier}")
    suspend fun activatePointsBalance(@Path("nullifier") nullifier: String, @Body payload: CreateBalancePayload): Response<PointsBalance>

    @POST("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/balances/{nullifier}/verifypassport")
    suspend fun verifyPassport(
        @Path("nullifier") nullifier: String,
        @Body payload: VerifyPassportPayload
    ): Response<Unit>

    @GET("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/balances/{nullifier}/withdrawals")
    suspend fun getWithdrawalHistory(@Path("nullifier") nullifier: String): Response<List<PointsWithdrawal>>

    @POST("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/balances/{nullifier}/withdrawals")
    suspend fun withdrawPoints(@Path("nullifier") nullifier: String, @Body payload: WithdrawPayload): Response<PointsWithdrawal>

    @GET("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/point_price")
    suspend fun getPointPrice(): Response<PointsPrice>

    // TODO: implement
//    @GET("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/isWithdrawalAllowed")
//    suspend fun isWithdrawalAllowed(): Response<Boolean>

    /* EVENTS */

    @GET("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/events")
    suspend fun getEventsList(): Response<List<PointsEvent>>

    @GET("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/events/{id}")
    suspend fun getEvent(@Path("id") id: String): Response<PointsEvent>

    @PATCH("https://api.stage.rarime.com/integrations/rarime-points-svc/v1/public/events/{id}")
    suspend fun claimPointsByEvent(@Path("id") id: String, @Body payload: ClaimEventPayload): Response<PointsEvent>
}