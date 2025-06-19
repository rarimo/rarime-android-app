package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.ChainInfo
import com.rarilabs.rarime.manager.CosmosManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import com.rarilabs.rarime.modules.wallet.models.TransactionType
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.Constants.RARIMO_CHAINS
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.decodeHexString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.time.Instant
import java.util.Date
import javax.inject.Inject

class RarimoToken @Inject constructor(
    val chainInfo: ChainInfo, // todo: fix null
    private val identityManager: IdentityManager,
    private val cosmosManager: CosmosManager,
    private val dataStoreManager: SecureSharedPrefsManager,
    address: String = ""
) : Token(address) {
    override var name: String = "" // TODO: make nullable
    override var symbol: String = ""
    override var decimals: Int = 0
    override var icon: Int = R.drawable.ic_rarimo
    override val tokenType: TokenType = TokenType.RARIMO_COSMOS


    override suspend fun loadDetails() {
        val currency = chainInfo.currencies[0]

        this.name = currency.coinDenom
        this.symbol = currency.coinDenom
        this.decimals = currency.coinDecimals
    }

    override suspend fun balanceOf(address: String): BigInteger {
        return withContext(Dispatchers.IO) {
            try {
                val response = cosmosManager.getBalance(address)

                if (response.balances.isNullOrEmpty()) {
                    return@withContext BigInteger.ZERO
                }

                return@withContext BigInteger.valueOf(response.balances.first().amount.toLong())
            } catch (e: Exception) {
                ErrorHandler.logError("RarimoToken:balanceOf", e.message.toString(), e)
                return@withContext BigInteger.ZERO
            }
        }
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {

        val profiler = identityManager.getProfiler()
            .newProfile(dataStoreManager.readPrivateKey()!!.decodeHexString())

        val rarimoChain = RARIMO_CHAINS[BaseConfig.CHAIN.chainId]

        withContext(Dispatchers.IO) {
            profiler.walletSend(
                to,
                amount.toString(),
                rarimoChain?.chainId,
                rarimoChain?.stakeCurrency?.coinDenom,
                rarimoChain?.rpc,
            ).decodeToString()
        }

        return Transaction(
            id = 12,
            amount = amount.toDouble(),
            date = Date.from(Instant.now()),
            state = TransactionState.INCOMING,
            from = identityManager.rarimoAddress(),
            to = to,
            tokenType = tokenType,
            operationType = TransactionType.TRANSFER
        )
    }

    override suspend fun loadTransactions(address: String): List<Transaction> {

        TODO()
        //return emptyList()
//        listOf(
//            Transaction(
//                id = 0,
//                amount = 0.0,
//                date = Date.from(Instant.now()),
//                state = TransactionState.INCOMING,
//                from = identityManager.rarimoAddress(),
//                to = receiver,
//                tokenType = tokenType,
//                operationType = TransactionType.TRANSFER
//            )
//        )
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
}