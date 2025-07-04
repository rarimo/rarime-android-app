package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.modules.wallet.models.Transaction
import java.math.BigInteger

// TODO: mb add provider to init ContractsManager
abstract class Token(var address: String) {
    abstract var name: String
    abstract var symbol: String
    abstract var decimals: Int
    abstract var icon: Int

    abstract val tokenType: TokenType

    abstract suspend fun loadDetails()

    abstract suspend fun balanceOf(address: String): BigInteger

    abstract suspend fun transfer(to: String, amount: BigInteger): Transaction

    abstract suspend fun loadTransactions(address: String): List<Transaction>

    abstract suspend fun estimateTransferFee(
        from: String,
        to: String,
        amount: BigInteger,
        gasPrice: BigInteger? = null,
        gasLimit: BigInteger? = null
    ): BigInteger
}
