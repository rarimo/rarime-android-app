package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.points.models.PointsBalanceData
import com.rarilabs.rarime.config.Keys
import com.rarilabs.rarime.manager.PointsManager
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import com.rarilabs.rarime.modules.wallet.models.TransactionType
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.time.Instant
import java.util.Date
import javax.inject.Inject

class PointsToken @Inject constructor(
    address: String = "",
    private val pointsManager: PointsManager
) : Token(address) {
    override var name: String = ""
    override var symbol: String = ""
    override var decimals: Int = 0
    override var icon: Int = R.drawable.ic_rarimo
    override val tokenType: TokenType = TokenType.POINTS

    var balanceDetails: PointsBalanceData? = null

    override suspend fun estimateTransferFee(
        from: String,
        to: String,
        amount: BigInteger,
        gasPrice: BigInteger?,
        gasLimit: BigInteger?
    ): BigInteger {

        return BigInteger.ZERO
    }

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
        return Transaction(
            id = "25353",
            amount = amount.toDouble(),
            date = Date.from(Instant.now()),
            state = TransactionState.OUTGOING,
            to = to,
            from = "",//TODO change this
            tokenType = tokenType,
            operationType = TransactionType.TRANSFER
        )
    }

    override suspend fun loadTransactions(address: String): List<Transaction> {
        return emptyList()
    }
}