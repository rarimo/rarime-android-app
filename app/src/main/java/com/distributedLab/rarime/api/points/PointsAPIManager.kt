package com.distributedLab.rarime.api.points

import com.distributedLab.rarime.api.points.models.ClaimEventBody
import com.distributedLab.rarime.api.points.models.CreateBalanceBody
import com.distributedLab.rarime.api.points.models.PointsBalance
import com.distributedLab.rarime.api.points.models.PointsEvent
import com.distributedLab.rarime.api.points.models.PointsPrice
import com.distributedLab.rarime.api.points.models.PointsWithdrawal
import com.distributedLab.rarime.api.points.models.VerifyPassportBody
import com.distributedLab.rarime.api.points.models.WithdrawBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class PointsAPIManager @Inject constructor(private val jsonApiPointsSvcManager: PointsAPI) {
    /* BALANCE */
    suspend fun createPointsBalance(body: CreateBalanceBody): PointsBalance {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.createPointsBalance(body)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getLeaderboard(): List<PointsBalance> {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getLeaderboard()
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getPointsBalance(userNullifierHex: String): PointsBalance {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getPointsBalance(userNullifierHex)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
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

    suspend fun getWithdrawalHistory(nullifier: String): List<PointsWithdrawal> {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getWithdrawalHistory(nullifier)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun withdrawPoints(userNullifierHex: String, body: WithdrawBody): PointsWithdrawal {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.withdrawPoints(userNullifierHex, body)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getPointPrice(): PointsPrice? {
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

    suspend fun getEventsList(): List<PointsEvent> {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getEventsList()
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun getEvent(id: String): PointsEvent {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.getEvent(id)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }

    suspend fun claimPointsByEvent(id: String, body: ClaimEventBody): PointsEvent? {
        return withContext(Dispatchers.IO) {
            try {
                jsonApiPointsSvcManager.claimPointsByEvent(id, body)
            } catch (e: HttpException) {
                throw Exception(e.toString())
            }
        }
    }
}