package com.distributedLab.rarime.api.points

import com.distributedLab.rarime.api.auth.AuthManager
import com.distributedLab.rarime.api.points.models.CreateBalanceAttributes
import com.distributedLab.rarime.api.points.models.CreateBalanceBody
import com.distributedLab.rarime.api.points.models.CreateBalanceData
import com.distributedLab.rarime.api.points.models.PointsBalanceBody
import com.distributedLab.rarime.api.points.models.VerifyPassportAttributes
import com.distributedLab.rarime.api.points.models.VerifyPassportBody
import com.distributedLab.rarime.api.points.models.VerifyPassportData
import com.distributedLab.rarime.api.points.models.WithdrawBody
import com.distributedLab.rarime.api.points.models.WithdrawPayload
import com.distributedLab.rarime.api.points.models.WithdrawPayloadAttributes
import com.distributedLab.rarime.manager.IdentityManager
import javax.inject.Inject

class PointsManager @Inject constructor(
    private val pointsAPIManager: PointsAPIManager,
    private val identityManager: IdentityManager,
    private val authManager: AuthManager,
) {
    suspend fun createPointsBalance(referralCode: String) {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        pointsAPIManager.createPointsBalance(
            CreateBalanceBody(
                data = CreateBalanceData(
                    id = userNullifierHex,
                    type = "create_balance",
                    attributes = CreateBalanceAttributes(
                        referredBy = referralCode
                    )
                )
            )
        )
    }

    suspend fun getPointsBalance(): PointsBalanceBody {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        return pointsAPIManager.getPointsBalance(userNullifierHex, "Bearer ${authManager.accessToken.value!!}")
    }

    suspend fun verifyPassport() {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        if (identityManager.registrationProof.value == null) {
            throw Exception("registration proof is null")
        }

        pointsAPIManager.verifyPassport(
            userNullifierHex,
            VerifyPassportBody(
                data = VerifyPassportData(
                    id = userNullifierHex,
                    type = "verify_passport",
                    attributes = VerifyPassportAttributes(
                        proof = identityManager.registrationProof.value!!.proof
                    )
                )
            )
        )
    }

    suspend fun withdrawPoints(amount: String) {
        val userNullifierHex = identityManager.getUserPointsNullifierHex()

        if (userNullifierHex.isEmpty()) {
            throw Exception("user nullifier is null")
        }

        pointsAPIManager.withdrawPoints(
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
    }
}