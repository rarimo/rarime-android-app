package com.rarilabs.rarime.data.tokens

import android.util.Log
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.R
import com.rarilabs.rarime.data.ChainInfo
import com.rarilabs.rarime.manager.CosmosManager
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
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
    override var decimals: Int = 6
    override var icon: Int = R.drawable.ic_rarimo

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

        Log.i("ChainId",rarimoChain?.chainId.toString())
        Log.i("coinMinimalDenom",rarimoChain?.stakeCurrency?.coinMinimalDenom.toString())
        Log.i("rpc",rarimoChain?.rpc.toString())

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
            iconId = R.drawable.ic_arrow_up,
            titleId = R.string.send_btn,
            amount = amount.toDouble(),
            date = Date.from(Instant.now()),
            state = TransactionState.INCOMING,
        )
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