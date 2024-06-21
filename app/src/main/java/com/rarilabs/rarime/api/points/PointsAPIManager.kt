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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class PointsAPIManager @Inject constructor(private val jsonApiPointsSvcManager: PointsAPI) {
    /* BALANCE */
    suspend fun createPointsBalance(
        body: CreateBalanceBody, authorization: String
    ): PointsBalanceBody {
        try {
            return jsonApiPointsSvcManager.createPointsBalance(body, authorization)
        } catch (e: HttpException) {
            throw Exception(e.toString())
        }
    }

    suspend fun getLeaderboard(): PointsLeaderBoardBody {
        try {
            val response = jsonApiPointsSvcManager.getLeaderboard()

            return response
        } catch (e: HttpException) {
            throw Exception(e.toString())
        }
    }

    suspend fun getPointsBalance(
        userNullifierHex: String, authorization: String, queryParams: Map<String, String>
    ): PointsBalanceBody {
        try {
            val response = jsonApiPointsSvcManager.getPointsBalance(
                userNullifierHex,
                authorization,
                queryParams,
            )

            return response
        } catch (e: HttpException) {
            throw Exception(e.toString())
        }
    }

    suspend fun verifyPassport(
        userNullifierHex: String,
        body: VerifyPassportBody,
        authorization: String,
    ) {
        withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.verifyPassport(userNullifierHex, body, authorization)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun joinRewordsProgram(
        nullifier: String,
        signature: String,
        payload: JoinRewardsProgramRequest,
        authorization: String
    ): VerifyPassportResponse {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.joinRewardsProgram(
                    nullifier, signature, authorization, payload
                )
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }

    }

    suspend fun getWithdrawalHistory(nullifier: String): List<PointsWithdrawalBody> {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getWithdrawalHistory(nullifier)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun withdrawPoints(userNullifierHex: String, body: WithdrawBody): PointsWithdrawalBody {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.withdrawPoints(userNullifierHex, body)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getPointPrice(): PointsPrice {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getPointPrice()
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    // TODO: implement
    // suspend fun isWithdrawalAllowed(): Boolean {}

    /* EVENTS */

    suspend fun getEventTypes(
        params: Map<String, String>
    ): PointsEventsTypesBody {
        try {
            return jsonApiPointsSvcManager.getEventTypes(params)
        } catch (e: HttpException) {
            throw Exception(e.toString())
        }
    }

    suspend fun getEventsList(
        authorization: String, params: Map<String, String>
    ): PointsEventsListBody {
        try {
            return jsonApiPointsSvcManager.getEventsList(
                authorization,
                params,
            )
        } catch (e: HttpException) {
            throw Exception(e.toString())
        }
    }

    suspend fun getEvent(id: String, authorization: String): PointsEventBody {
        try {
            return jsonApiPointsSvcManager.getEvent(id, authorization)
        } catch (e: HttpException) {
            throw Exception(e.toString())
        }
    }

    suspend fun claimPointsByEvent(id: String, body: ClaimEventBody): PointsEventBody {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.claimPointsByEvent(id, body)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }
}