package com.distributedLab.rarime.modules.common

import com.distributedLab.rarime.domain.points.ClaimEventPayload
import com.distributedLab.rarime.domain.points.CreateBalancePayload
import com.distributedLab.rarime.domain.points.JsonApiPointsSvcManager
import com.distributedLab.rarime.domain.points.PointsBalance
import com.distributedLab.rarime.domain.points.PointsEvent
import com.distributedLab.rarime.domain.points.PointsPrice
import com.distributedLab.rarime.domain.points.PointsWithdrawal
import com.distributedLab.rarime.domain.points.VerifyPassportPayload
import com.distributedLab.rarime.domain.points.WithdrawPayload
import javax.inject.Inject

class PointsManager @Inject constructor(private val jsonApiPointsSvcManager: JsonApiPointsSvcManager) {
    /* BALANCE */

    suspend fun createPointsBalance(payload: CreateBalancePayload): PointsBalance? {
        val response = jsonApiPointsSvcManager.createPointsBalance(payload)

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null

        // TODO:
        //  return response.errorBody()!!
    }

    suspend fun getLeaderboard(): List<PointsBalance>? {
        val response = jsonApiPointsSvcManager.getLeaderboard()

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }

    suspend fun getPointsBalance(nullifier: String): PointsBalance? {
        val response = jsonApiPointsSvcManager.getPointsBalance(nullifier)

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }

    suspend fun activatePointsBalance(nullifier: String, payload: CreateBalancePayload): PointsBalance? {
        val response = jsonApiPointsSvcManager.activatePointsBalance(nullifier, payload)

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }

    suspend fun verifyPassport(
        nullifier: String,
        payload: VerifyPassportPayload
    ): Unit {
        val response = jsonApiPointsSvcManager.verifyPassport(nullifier, payload)

        if (!response.isSuccessful) {
            // TODO: get error code
            throw Exception("Failed to verify passport")
        }
    }

    suspend fun getWithdrawalHistory(nullifier: String): List<PointsWithdrawal>? {
        val response = jsonApiPointsSvcManager.getWithdrawalHistory(nullifier)

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }

    suspend fun withdrawPoints(nullifier: String, payload: WithdrawPayload): PointsWithdrawal? {
        val response = jsonApiPointsSvcManager.withdrawPoints(nullifier, payload)

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }

    suspend fun getPointPrice(): PointsPrice? {
        val response = jsonApiPointsSvcManager.getPointPrice()

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }

    // TODO: implement
    // suspend fun isWithdrawalAllowed(): Boolean {}

    /* EVENTS */

    suspend fun getEventsList(): List<PointsEvent>? {
        val response = jsonApiPointsSvcManager.getEventsList()

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }

    suspend fun getEvent(id: String): PointsEvent? {
        val response = jsonApiPointsSvcManager.getEvent(id)

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }

    suspend fun claimPointsByEvent(id: String, payload: ClaimEventPayload): PointsEvent? {
        val response = jsonApiPointsSvcManager.claimPointsByEvent(id, payload)

        if (response.isSuccessful) {
            return response.body()!!
        }

        return null
    }
}