package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import com.rarilabs.rarime.modules.wallet.models.TransactionType
import java.math.BigInteger
import java.time.Instant
import java.util.Date

class PreviewerToken(
    address: String,
    override var name: String = "Previewer Token",
    override var symbol: String = "PRE",
    override var decimals: Int = 0,
    override var icon: Int = R.drawable.ic_info,
) : Token(address) {
    override suspend fun loadDetails(): Unit {}
    override val tokenType: TokenType = TokenType.DEFAULT


    override suspend fun balanceOf(address: String): BigInteger {
        return BigInteger.ZERO
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {
        return Transaction(
            id = 0,
            amount = 0.0,
            date = Date.from(Instant.now()),
            state = TransactionState.INCOMING,
            from = "0x0000000000000000000000",
            to = to,
            tokenType = tokenType,
            operationType = TransactionType.TRANSFER
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
                amount = 0.0,
                date = Date.from(Instant.now()),
                state = TransactionState.INCOMING,
                from = sender,
                to = receiver,
                tokenType = tokenType,
                operationType = TransactionType.TRANSFER
            )
        )
    }
}