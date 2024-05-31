package com.distributedLab.rarime.data.tokens

import com.distributedLab.rarime.domain.data.Transaction
import java.math.BigInteger

// TODO: mb add provider to init ContractsManager
abstract class Token(var address: String) {
    abstract var name: String
    abstract var symbol: String
    abstract var decimals: Int

    abstract suspend fun loadDetails(): Unit

    abstract suspend fun balanceOf(address: String): BigInteger

    abstract suspend fun transfer(to: String, value: BigInteger): Transaction

    abstract suspend fun loadTransactions(sender: String?, receiver: String?): List<Transaction>
}
