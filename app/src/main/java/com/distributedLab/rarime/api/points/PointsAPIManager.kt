package com.distributedLab.rarime.api.points

import android.util.Log
import com.distributedLab.rarime.api.points.models.ClaimEventBody
import com.distributedLab.rarime.api.points.models.ClaimEventPayload
import com.distributedLab.rarime.api.points.models.CreateBalanceAttributes
import com.distributedLab.rarime.api.points.models.CreateBalanceBody
import com.distributedLab.rarime.api.points.models.CreateBalancePayload
import com.distributedLab.rarime.api.points.models.PointsBalance
import com.distributedLab.rarime.api.points.models.PointsEvent
import com.distributedLab.rarime.api.points.models.PointsPrice
import com.distributedLab.rarime.api.points.models.PointsWithdrawal
import com.distributedLab.rarime.api.points.models.VerifyPassportAttributes
import com.distributedLab.rarime.api.points.models.VerifyPassportBody
import com.distributedLab.rarime.api.points.models.VerifyPassportPayload
import com.distributedLab.rarime.api.points.models.WithdrawBody
import com.distributedLab.rarime.api.points.models.WithdrawPayload
import com.distributedLab.rarime.api.points.models.WithdrawPayloadAttributes
import com.distributedLab.rarime.modules.common.IdentityManager
import com.google.gson.Gson
import javax.inject.Inject

class PointsAPIManager @Inject constructor(
    private val jsonApiPointsSvcManager: PointsAPI,
    private val identityManager: IdentityManager
) {
    /* BALANCE */

    suspend fun createPointsBalance(referralCode: String): PointsBalance? {
        val userNullifier = identityManager.getUserNullifier()
        val userNullifierHex = identityManager.getUserNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val body = CreateBalanceBody(
            data = CreateBalancePayload(
                id = userNullifierHex,
                type = "create_balance",
                attributes = CreateBalanceAttributes(
                    referredBy = referralCode
                )
            )
        )

        Log.i("user nullifier", userNullifier)
        Log.i("create balance body", Gson().toJson(body))

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
        val userNullifierHex = identityManager.getUserNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val response = jsonApiPointsSvcManager.getPointsBalance(userNullifierHex)

        if (response.isSuccessful) {
            return response.body()!!
        }

        throw Exception(response.errorBody()?.string().toString())
    }

    suspend fun verifyPassport(): Unit {
        val userNullifierHex = identityManager.getUserNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        if (identityManager.registrationProof.value == null) {
            throw Exception("registration proof is null")
        }

        val body = VerifyPassportBody(
            data = VerifyPassportPayload(
                id = userNullifierHex,
                type = "verify_passport",
                attributes = VerifyPassportAttributes(
                    proof = identityManager.registrationProof.value!!.proof
                )
            )
        )

        val response = jsonApiPointsSvcManager.verifyPassport(userNullifierHex, body)

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
        val userNullifierHex = identityManager.getUserNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        val response = jsonApiPointsSvcManager.withdrawPoints(
            userNullifierHex,
            WithdrawBody(
                data = WithdrawPayload(
                    id = userNullifierHex,
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