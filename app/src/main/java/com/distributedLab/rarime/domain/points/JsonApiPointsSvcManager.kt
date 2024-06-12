package com.distributedLab.rarime.domain.points
import com.distributedLab.rarime.BaseConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface JsonApiPointsSvcManager {
    /* BALANCE */

    @POST("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances")
    suspend fun createPointsBalance(@Body payload: CreateBalanceBody): Response<PointsBalance>

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances")
    suspend fun getLeaderboard(): Response<List<PointsBalance>>

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances/{nullifier}")
    suspend fun getPointsBalance(@Path("nullifier") nullifier: String): Response<PointsBalance>

    @POST("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances/{nullifier}/verifypassport")
    suspend fun verifyPassport(
        @Path("nullifier") nullifier: String,
        @Body payload: VerifyPassportBody
    ): Response<Unit>

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances/{nullifier}/withdrawals")
    suspend fun getWithdrawalHistory(@Path("nullifier") nullifier: String): Response<List<PointsWithdrawal>>

    @POST("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances/{nullifier}/withdrawals")
    suspend fun withdrawPoints(@Path("nullifier") nullifier: String, @Body payload: WithdrawBody): Response<PointsWithdrawal>

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/point_price")
    suspend fun getPointPrice(): Response<PointsPrice>

    // TODO: implement
//    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/isWithdrawalAllowed")
//    suspend fun isWithdrawalAllowed(): Response<Boolean>

    /* EVENTS */

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/events")
    suspend fun getEventsList(): Response<List<PointsEvent>>

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/events/{id}")
    suspend fun getEvent(@Path("id") id: String): Response<PointsEvent>

    @PATCH("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/events/{id}")
    suspend fun claimPointsByEvent(@Path("id") id: String, @Body payload: ClaimEventBody): Response<PointsEvent>
}