package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.cosmos.CosmosManager
import com.rarilabs.rarime.data.ChainInfo
import com.rarilabs.rarime.api.cosmos.models.CosmosTransferResponse
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import com.rarilabs.rarime.util.DateFormatType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class RarimoToken @Inject constructor(
    val chainInfo: ChainInfo,
    private val identityManager: IdentityManager,
    private val cosmosManager: CosmosManager,
    address: String = ""
) : Token(address) {
    override var name: String = "" // TODO: make nullable
    override var symbol: String = ""
    override var decimals: Int = 0
    override var icon: Int = R.drawable.ic_rarimo

    override suspend fun loadDetails() {
        val currency = chainInfo.currencies[0]

        this.name = currency.coinDenom
        this.symbol = currency.coinDenom
        this.decimals = currency.coinDecimals
    }

    override suspend fun balanceOf(address: String): BigInteger {
        return withContext(Dispatchers.IO) {
            val response = cosmosManager.getBalance(address)

            response?.let {
                it.balances.ifEmpty {
                    return@withContext BigInteger.ZERO
                }

                return@withContext BigInteger.valueOf(it.balances.first().amount.toLong())
            } ?: BigInteger.ZERO
        }
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {
        val response = withContext(Dispatchers.IO) {
            identityManager.profiler.walletSend(
                to,
                amount.toString(),
                chainInfo.chainId,
                chainInfo.currencies[0].coinDenom,
                chainInfo.rpc,
            ).toString()
        }

        val jsonResponse = Gson().fromJson(response, CosmosTransferResponse::class.java)

        return Transaction(
            id = jsonResponse.data.id.toInt(),
            iconId = R.drawable.ic_arrow_up,
            titleId = R.string.send_btn,
            amount = jsonResponse.data.attributes.amount.toDouble(),
            date = SimpleDateFormat(DateFormatType.MRZ.pattern, Locale.US)
                .parse(jsonResponse.data.attributes.created_at),
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