package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.time.Instant
import java.util.Date
import javax.inject.Inject

class NativeToken @Inject constructor(
    private val web3j: Web3j,
    private val identityManager: IdentityManager
) : Token(address = "") {

    override var name: String = "RMO"
    override var symbol: String = "RMO"
    override var decimals: Int = 2
    override var icon: Int = R.drawable.ic_rarimo

    override suspend fun loadDetails() {

    }

    override suspend fun balanceOf(address: String): BigInteger {
        val ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send()

        return ethGetBalance.balance
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {

        val privateKey = Credentials.create(identityManager.privateKey.value)


        val nonce =
            web3j.ethGetTransactionCount(privateKey.address, DefaultBlockParameterName.LATEST)
                .send().transactionCount

        val gasPrice = web3j.ethGasPrice().send().gasPrice
        val gasLimit = BigInteger.valueOf(21000L)
        val value = amount

        //TODO: fix it
        val chainId = 201411L

        val rawTransaction = RawTransaction.createEtherTransaction(
            nonce, gasPrice, gasLimit, to, value
        )

        val signedMessage =
            TransactionEncoder.signMessage(rawTransaction, chainId, privateKey)
        val hexValue = Numeric.toHexString(signedMessage)

        val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send()

        if (ethSendTransaction.hasError()) {
            throw RuntimeException(ethSendTransaction.error.message)
        }

        val txHash = ethSendTransaction.transactionHash


        return Transaction(
            id = BigInteger(Numeric.hexStringToByteArray(txHash)).toInt(),
            iconId = R.drawable.ic_arrow_up,
            titleId = R.string.send_btn,
            amount = amount.toDouble(),
            date = Date.from(Instant.now()),
            state = TransactionState.OUTGOING,
        )

    }

    override suspend fun loadTransactions(sender: String?, receiver: String?): List<Transaction> {
        return listOf()
    }

}