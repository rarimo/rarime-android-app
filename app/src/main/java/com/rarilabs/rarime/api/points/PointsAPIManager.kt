package com.rarilabs.rarime.api.points

import com.rarilabs.rarime.api.auth.UnauthorizedException
import com.rarilabs.rarime.api.points.models.ClaimEventBody
import com.rarilabs.rarime.api.points.models.CreateBalanceBody
import com.rarilabs.rarime.api.points.models.JoinRewardsProgramRequest
import com.rarilabs.rarime.api.points.models.MaintenanceResponse
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
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class InvitationUsedException : Exception()
class InvitationNotExistException : Exception()

class PointsAPIManager @Inject constructor(private val jsonApiPointsSvcManager: PointsAPI) {
    /* BALANCE */
    suspend fun createPointsBalance(
        body: CreateBalanceBody, authorization: String
    ): PointsBalanceBody {
        val response = jsonApiPointsSvcManager.createPointsBalance(body, authorization)

        if (response.isSuccessful) {
            return response.body()!!
        }

        val errorCode = response.code()
        val errorBody = response.errorBody()?.string()

        throw when (errorCode) {
            401 -> UnauthorizedException()
            404 -> InvitationNotExistException()
            409 -> InvitationUsedException()

            else -> Exception(errorBody)
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
    ): PointsBalanceBody? {

        val response = jsonApiPointsSvcManager.getPointsBalance(
            userNullifierHex,
            authorization,
            queryParams,
        )

        if (response.isSuccessful) {
            return response.body()
        }

        if (response.code() == 404) {
            return null
        }


        throw InternalServerError(response.message())

    }

    suspend fun verifyPassport(
        userNullifierHex: String,
        body: VerifyPassportBody,
        authorization: String,
        signature: String
    ) {
        withContext(Dispatchers.IO) {

            val response = jsonApiPointsSvcManager.verifyPassport(
                userNullifierHex,
                body,
                authorization,
                signature
            )

            if (response.isSuccessful) {
                return@withContext
            }

            if (response.code() == 409) {
                throw IllegalStateException()
            }


            ErrorHandler.logError(
                "verify Passport failed",
                response.errorBody()?.string().toString()
            )
            throw Exception(response.errorBody()?.string())

        }
    }

    suspend fun joinRewordsProgram(
        jwt: String,
        signature: String,
        payload: JoinRewardsProgramRequest,
        authorization: String,
    ): VerifyPassportResponse {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.joinRewardsProgram(
                    jwt, signature, authorization, payload
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


    suspend fun getMaintenanceStatus(): MaintenanceResponse {

        val response = jsonApiPointsSvcManager.getMaintenance()

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string())
    }
}

class InternalServerError(message: String) : Exception("Internal server Error: $message")
class ConflictException(message: String) : Exception("ConflictException: $message")