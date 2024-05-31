package com.distributedLab.rarime.data.tokens

import com.distributedLab.rarime.domain.data.Transaction
import java.math.BigInteger

// TODO: implement contracts manager
class Erc20Token(address: String) : Token(address) {
    override var name: String = ""
    override var symbol: String = ""
    override var decimals: Int = 0

    override suspend fun loadDetails(): Unit {
        this.name = "Erc20Token"
        this.symbol = "ERC"
        this.decimals = 18
    }

    override suspend fun balanceOf(address: String): BigInteger {
        return BigInteger.ZERO
    }

    override suspend fun transfer(to: String, value: BigInteger): Transaction {
        return Transaction(
            height = 0L,
            txhash = "",
            data = "",
            raw_log = "",
            logs = listOf(),
            gas_wanted = 0L,
            gas_used = 0L,
            events = listOf(),
        )
    }

    override suspend fun loadTransactions(sender: String?, receiver: String?): List<Transaction> {
        if (sender == null && receiver == null) {
            throw IllegalArgumentException("sender or receiver must be not null")
        }

        return listOf(
            Transaction(
                height = 0L,
                txhash = "",
                data = "",
                raw_log = "",
                logs = listOf(),
                gas_wanted = 0L,
                gas_used = 0L,
                events = listOf(),
            )
        )
    }
}