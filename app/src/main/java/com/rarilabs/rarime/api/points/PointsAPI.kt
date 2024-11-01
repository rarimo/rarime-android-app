package com.rarilabs.rarime.api.points

import com.rarilabs.rarime.api.points.models.ClaimEventBody
import com.rarilabs.rarime.api.points.models.CreateBalanceBody
import com.rarilabs.rarime.api.points.models.JoinRewardsProgramRequest
import com.rarilabs.rarime.api.points.models.PointsBalanceBody
import com.rarilabs.rarime.api.points.models.PointsEventBody
import com.rarilabs.rarime.api.points.models.PointsEventsListBody
import com.rarilabs.rarime.api.points.models.PointsEventsTypesBody
import com.rarilabs.rarime.api.points.models.PointsLeaderBoardBody
import com.rarilabs.rarime.api.points.models.PointsPrice
import com.rarilabs.rarime.api.points.models.PointsWithdrawalBody
import com.rarilabs.rarime.api.points.models.VerifyPassportBody
import com.rarilabs.rarime.api.points.models.VerifyPassportResponse
import com.rarilabs.rarime.api.points.models.WithdrawBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface PointsAPI {/* BALANCE */

    @POST("/integrations/rarime-points-svc/v1/public/balances")
    suspend fun createPointsBalance(
        @Body payload: CreateBalanceBody, @Header("Authorization") authorization: String
    ): Response<PointsBalanceBody>

    @GET("/integrations/rarime-points-svc/v1/public/balances")
    suspend fun getLeaderboard(): PointsLeaderBoardBody

    @GET("/integrations/rarime-points-svc/v1/public/balances/{nullifier}")
    suspend fun getPointsBalance(
        @Path("nullifier") nullifier: String,
        @Header("Authorization") authorization: String,
        @QueryMap queryParams: Map<String, String>
    ): PointsBalanceBody?

    @POST("/integrations/rarime-points-svc/v1/public/balances/{nullifier}/verifypassport")
    suspend fun verifyPassport(
        @Path("nullifier") nullifier: String,
        @Body payload: VerifyPassportBody,
        @Header("Authorization") authorization: String,
        @Header("Signature") contentType: String
    ): Response<Unit>

    @GET("/integrations/rarime-points-svc/v1/public/balances/{nullifier}/withdrawals")
    suspend fun getWithdrawalHistory(@Path("nullifier") nullifier: String): List<PointsWithdrawalBody>

    @POST("/integrations/rarime-points-svc/v1/public/balances/{nullifier}/withdrawals")
    suspend fun withdrawPoints(
        @Path("nullifier") nullifier: String, @Body payload: WithdrawBody
    ): PointsWithdrawalBody

    @GET("/integrations/rarime-points-svc/v1/public/point_price")
    suspend fun getPointPrice(): PointsPrice


    @GET("/integrations/rarime-points-svc/v1/public/event_types")
    suspend fun getEventTypes(@QueryMap params: Map<String, String>): PointsEventsTypesBody

    @GET("/integrations/rarime-points-svc/v1/public/events")
    suspend fun getEventsList(
        @Header("Authorization") authorization: String, @QueryMap params: Map<String, String>
    ): PointsEventsListBody

    @POST("/integrations/rarime-points-svc/v1/public/balances/{nullifier}/join_program")
    suspend fun joinRewardsProgram(
        @Path("nullifier") nullifier: String,
        @Header("Signature") signature: String,
        @Header("Authorization") authorization: String,
        @Body payload: JoinRewardsProgramRequest
    ): VerifyPassportResponse

    @GET("/integrations/rarime-points-svc/v1/public/events/{id}")
    suspend fun getEvent(
        @Path("id") id: String,
        @Header("Authorization") authorization: String,

    ): PointsEventBody

    @PATCH("/integrations/rarime-points-svc/v1/public/events/{id}")
    suspend fun claimPointsByEvent(
        @Path("id") id: String, @Body payload: ClaimEventBody
    ): PointsEventBody
}