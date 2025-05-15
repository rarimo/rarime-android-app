package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.config.Keys
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.modules.wallet.models.Transaction
import java.math.BigInteger
import javax.inject.Inject

class PointsToken @Inject constructor(
    address: String = "",
    private val pointsManager: PointsManager
) : Token(address) {
    override var name: String = ""
    override var symbol: String = ""
    override var decimals: Int = 0
    override var icon: Int = R.drawable.ic_rarimo

    var balanceDetails: PointsBalanceData? = null

    override suspend fun loadDetails() {
        name = "Reserved RMO"
        symbol = "RESERVED"
        decimals = 0

        try {
            balanceDetails = pointsManager.getPointsBalance()?.data
        } catch (e: Exception) {

        }
    }

    override suspend fun balanceOf(address: String): BigInteger {
        try {
            balanceDetails = pointsManager.getPointsBalance()?.data

            if (balanceDetails?.attributes?.amount == null) {
                pointsManager.createPointsBalance(Keys.genesisReferralCode)
            }

            return balanceDetails?.attributes?.amount?.let { BigInteger.valueOf(it) }
                ?: BigInteger.ZERO
        } catch (e: Exception) {
            return BigInteger.ZERO
        }
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {
        TODO("Not yet implemented")
    }

    override suspend fun loadTransactions(sender: String?, receiver: String?): List<Transaction> {
        TODO("Not yet implemented")
    }
}