package com.distributedLab.rarime.data.tokens

import com.distributedLab.rarime.R
import com.distributedLab.rarime.modules.wallet.models.Transaction
import java.math.BigInteger
import javax.inject.Inject

class PointsToken constructor(address: String = "") : Token(address) {
    override var name: String = ""
    override var symbol: String = ""
    override var decimals: Int = 0
    override var icon: Int = R.drawable.ic_rarimo

    override suspend fun loadDetails() {
        name = "Reserved RMO"
        symbol = "RRMO"
        decimals = 0
    }

    override suspend fun balanceOf(address: String): BigInteger {
        return BigInteger.ZERO
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {
        TODO("Not yet implemented")
    }

    override suspend fun loadTransactions(sender: String?, receiver: String?): List<Transaction> {
        TODO("Not yet implemented")
    }

}