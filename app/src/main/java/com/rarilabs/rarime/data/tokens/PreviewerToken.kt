package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import java.math.BigInteger
import java.time.Instant
import java.util.Date

class PreviewerToken(
    address: String,
    override var name: String = "Previewer Token",
    override var symbol: String = "PRE",
    override var decimals: Int = 18,
    override var icon: Int = R.drawable.ic_info,
) : Token(address) {
    override suspend fun loadDetails(): Unit {}

    override suspend fun balanceOf(address: String): BigInteger {
        return BigInteger.ZERO
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {
        return Transaction(
            id = 0,
            iconId = 0,
            titleId = 0,
            amount = 0.0,
            date = Date.from(Instant.now()),
            state = TransactionState.INCOMING,
        )
    }

    override suspend fun estimateTransferFee(
        from: String,
        to: String,
        amount: BigInteger,
        gasPrice: BigInteger?,
        gasLimit: BigInteger?
    ): BigInteger {
        return BigInteger.ZERO
    }

    override suspend fun loadTransactions(sender: String?, receiver: String?): List<Transaction> {
        if (sender == null || receiver == null) {
            throw IllegalArgumentException("sender or receiver must be not null")
        }

        return listOf(
            Transaction(
                id = 0,
                iconId = 0,
                titleId = 0,
                amount = 0.0,
                date = Date.from(Instant.now()),
                state = TransactionState.INCOMING,
            )
        )
    }
}