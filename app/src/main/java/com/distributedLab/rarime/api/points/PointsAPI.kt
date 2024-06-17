package com.distributedLab.rarime.api.points
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.points.models.ClaimEventBody
import com.distributedLab.rarime.api.points.models.CreateBalanceBody
import com.distributedLab.rarime.api.points.models.PointsBalanceBody
import com.distributedLab.rarime.api.points.models.PointsEventBody
import com.distributedLab.rarime.api.points.models.PointsEventsListBody
import com.distributedLab.rarime.api.points.models.PointsPrice
import com.distributedLab.rarime.api.points.models.PointsWithdrawalBody
import com.distributedLab.rarime.api.points.models.VerifyPassportBody
import com.distributedLab.rarime.api.points.models.WithdrawBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface PointsAPI {
    /* BALANCE */

    @POST("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances")
    suspend fun createPointsBalance(@Body payload: CreateBalanceBody): PointsBalanceBody

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances")
    suspend fun getLeaderboard(): List<PointsBalanceBody>

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances/{nullifier}")
    suspend fun getPointsBalance(
        @Path("nullifier") nullifier: String,
        @Header("Authorization") authorization: String
    ): PointsBalanceBody

    @POST("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances/{nullifier}/verifypassport")
    suspend fun verifyPassport(
        @Path("nullifier") nullifier: String,
        @Body payload: VerifyPassportBody,
        @Header("Authorization") authorization: String
    ): Unit

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances/{nullifier}/withdrawals")
    suspend fun getWithdrawalHistory(@Path("nullifier") nullifier: String): List<PointsWithdrawalBody>

    @POST("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/balances/{nullifier}/withdrawals")
    suspend fun withdrawPoints(@Path("nullifier") nullifier: String, @Body payload: WithdrawBody): PointsWithdrawalBody

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/point_price")
    suspend fun getPointPrice(): PointsPrice

    // TODO: implement
//    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/isWithdrawalAllowed")
//    suspend fun isWithdrawalAllowed(): Boolean

    /* EVENTS */

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/events")
    suspend fun getEventsList(
        @Header("Authorization") authorization: String,
        @QueryMap params: Map<String, String>
    ): PointsEventsListBody

    @GET("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/events/{id}")
    suspend fun getEvent(@Path("id") id: String): PointsEventBody

    @PATCH("${BaseConfig.RELAYER_URL}/integrations/rarime-points-svc/v1/public/events/{id}")
    suspend fun claimPointsByEvent(@Path("id") id: String, @Body payload: ClaimEventBody): PointsEventBody
}