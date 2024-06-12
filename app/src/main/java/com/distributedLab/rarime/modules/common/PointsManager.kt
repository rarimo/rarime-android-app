package com.distributedLab.rarime.modules.common

import com.distributedLab.rarime.domain.points.ClaimEventBody
import com.distributedLab.rarime.domain.points.ClaimEventPayload
import com.distributedLab.rarime.domain.points.CreateBalanceAttributes
import com.distributedLab.rarime.domain.points.CreateBalanceBody
import com.distributedLab.rarime.domain.points.CreateBalancePayload
import com.distributedLab.rarime.domain.points.JsonApiPointsSvcManager
import com.distributedLab.rarime.domain.points.PointsBalance
import com.distributedLab.rarime.domain.points.PointsEvent
import com.distributedLab.rarime.domain.points.PointsPrice
import com.distributedLab.rarime.domain.points.PointsWithdrawal
import com.distributedLab.rarime.domain.points.VerifyPassportAttributes
import com.distributedLab.rarime.domain.points.VerifyPassportBody
import com.distributedLab.rarime.domain.points.VerifyPassportPayload
import com.distributedLab.rarime.domain.points.WithdrawBody
import com.distributedLab.rarime.domain.points.WithdrawPayload
import com.distributedLab.rarime.domain.points.WithdrawPayloadAttributes
import javax.inject.Inject

class PointsManager @Inject constructor(
    private val jsonApiPointsSvcManager: JsonApiPointsSvcManager,
    private val identityManager: IdentityManager
) {
    /* BALANCE */

    suspend fun createPointsBalance(referralCode: String): PointsBalance? {
        val userNullifier = identityManager.getUserNullifier()

        if (userNullifier.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val body = CreateBalanceBody(
            data = CreateBalancePayload(
                id = userNullifier,
                type = "crete_balance",
                attributes = CreateBalanceAttributes(
                    referredBy = referralCode
                )
            )
        )

        val response = jsonApiPointsSvcManager.createPointsBalance(body)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }

    suspend fun getLeaderboard(): List<PointsBalance>? {
        val response = jsonApiPointsSvcManager.getLeaderboard()

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }

    suspend fun getPointsBalance(): PointsBalance? {
        val userNullifier = identityManager.getUserNullifier()

        if (userNullifier.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val response = jsonApiPointsSvcManager.getPointsBalance(userNullifier)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }

    suspend fun verifyPassport(): Unit {
        val userNullifier = identityManager.getUserNullifier()

        if (userNullifier.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        if (identityManager.registrationProof.value == null) {
            throw Exception("registration proof is null")
        }

        val body = VerifyPassportBody(
            data = VerifyPassportPayload(
                id = userNullifier,
                type = "verify_passport",
                attributes = VerifyPassportAttributes(
                    proof = identityManager.registrationProof.value!!.proof
                )
            )
        )

        val response = jsonApiPointsSvcManager.verifyPassport(userNullifier, body)

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

        throw Exception(response.errorBody()?.string().toString())
    }

    suspend fun withdrawPoints(amount: Double): PointsWithdrawal? {
        val userNullifier = identityManager.getUserNullifier()

        if (userNullifier.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val response = jsonApiPointsSvcManager.withdrawPoints(
            userNullifier,
            WithdrawBody(
                data = WithdrawPayload(
                    id = userNullifier,
                    type = "withdraw",
                    attributes = WithdrawPayloadAttributes(
                        amount = amount.toLong(),
                        address = identityManager.rarimoAddress(),
                        proof = identityManager.registrationProof.value!!.proof
                    )
                )
            )
        )

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }

    suspend fun getPointPrice(): PointsPrice? {
        val response = jsonApiPointsSvcManager.getPointPrice()

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }

    // TODO: implement
    // suspend fun isWithdrawalAllowed(): Boolean {}

    /* EVENTS */

    suspend fun getEventsList(): List<PointsEvent>? {
        val response = jsonApiPointsSvcManager.getEventsList()

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }

    suspend fun getEvent(id: String): PointsEvent? {
        val response = jsonApiPointsSvcManager.getEvent(id)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }

    suspend fun claimPointsByEvent(id: String, payload: ClaimEventPayload): PointsEvent? {
        val response = jsonApiPointsSvcManager.claimPointsByEvent(
            id, ClaimEventBody(
                data = payload
            )
        )

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }
}