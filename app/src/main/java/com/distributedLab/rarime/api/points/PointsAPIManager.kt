package com.distributedLab.rarime.api.points

import android.util.Log
import com.distributedLab.rarime.api.points.models.ClaimEventBody
import com.distributedLab.rarime.api.points.models.CreateBalanceBody
import com.distributedLab.rarime.api.points.models.PointsBalanceBody
import com.distributedLab.rarime.api.points.models.PointsEventBody
import com.distributedLab.rarime.api.points.models.PointsPrice
import com.distributedLab.rarime.api.points.models.PointsWithdrawalBody
import com.distributedLab.rarime.api.points.models.VerifyPassportBody
import com.distributedLab.rarime.api.points.models.WithdrawBody
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class PointsAPIManager @Inject constructor(private val jsonApiPointsSvcManager: PointsAPI) {
    /* BALANCE */
    suspend fun createPointsBalance(body: CreateBalanceBody): PointsBalanceBody {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.createPointsBalance(body)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getLeaderboard(): List<PointsBalanceBody> {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getLeaderboard()
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getPointsBalance(userNullifierHex: String, authorization: String): PointsBalanceBody? {
        try {
            val response = jsonApiPointsSvcManager.getPointsBalance(userNullifierHex, authorization)

            return response
        } catch (e: HttpException) {
            throw Exception(e.toString())
        }

        return null
    }

    suspend fun verifyPassport(userNullifierHex: String, body: VerifyPassportBody) {
        withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.verifyPassport(userNullifierHex, body)
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

    suspend fun getEventsList(): List<PointsEventBody> {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getEventsList()
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getEvent(id: String): PointsEventBody {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getEvent(id)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
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