package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import com.rarilabs.rarime.modules.wallet.models.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import org.web3j.tx.response.TransactionReceiptProcessor
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.time.Instant
import java.util.Date
import javax.inject.Inject

class NativeToken @Inject constructor(
    private val web3j: Web3j,
    private val identityManager: IdentityManager
) : Token(address = "") {

    override var name: String = "Ethereum"
    override var symbol: String = "ETH"
    override var decimals: Int = 18
    override var icon: Int = R.drawable.ic_rarimo
    override val tokenType: TokenType = TokenType.RARIMO_ETH

    override suspend fun loadDetails() {

    }

    override suspend fun balanceOf(address: String): BigInteger {
        val ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send()

        return ethGetBalance.balance
    }

    override suspend fun estimateTransferFee(
        from: String,
        to: String,
        amount: BigInteger,
        gasPrice: BigInteger?,
        gasLimit: BigInteger?
    ): BigInteger {


        val tx = org.web3j.protocol.core.methods.request.Transaction.createEtherTransaction(
            from,  // from
            null,  // nonce (null = let node decide)
            null,  // gasPrice (null = estimate based on current)
            null,  // gasLimit (null = estimate)
            to,  // to
            amount // value in wei
        )

        val gasEstimate = withContext(Dispatchers.IO) {
            web3j.ethEstimateGas(tx).send()
        }
        if (gasEstimate.hasError()) {
            throw Exception("Estimate error: ${gasEstimate.error.message}")
        }
        val gasLimit = gasLimit ?: gasEstimate.amountUsed

        // 2. Get current gas price if not provided
        val usedGasPrice = gasPrice ?: withContext(Dispatchers.IO) {
            web3j.ethGasPrice().send().gasPrice.multiply(BigInteger.valueOf(2L))
        }

        return gasLimit.multiply(usedGasPrice)
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {

        val privateKey = Credentials.create(identityManager.privateKey.value)

        val nonce =
            web3j.ethGetTransactionCount(privateKey.address, DefaultBlockParameterName.LATEST)
                .send().transactionCount

        val gasPrice = web3j.ethGasPrice().send().gasPrice.multiply(BigInteger.valueOf(2L))
        val gasLimit = BigInteger.valueOf(21000L)
        val value = amount

        val chainId = 7368L

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


        val isSuccess = checkIsTransactionSuccessful(txHash)

        if (!isSuccess)
            throw Exception()


        return Transaction(
            id = BigInteger(Numeric.hexStringToByteArray(txHash)).toInt(),
            amount = amount.toDouble(),
            date = Date.from(Instant.now()),
            state = TransactionState.OUTGOING,
            to = to,
            from = identityManager.evmAddress(),
            tokenType = tokenType,
            operationType = TransactionType.TRANSFER
        )

    }

    override suspend fun loadTransactions(sender: String?, receiver: String?): List<Transaction> {
        return listOf()
    }

    private suspend fun checkIsTransactionSuccessful(txHash: String): Boolean {
        val receiptProcessor: TransactionReceiptProcessor = PollingTransactionReceiptProcessor(
            web3j, 1000,  // polling interval in milliseconds
            40     // attempts
        )
        val transactionReceipt: TransactionReceipt = withContext(Dispatchers.IO) {
            receiptProcessor.waitForTransactionReceipt(txHash)
        }
        return transactionReceipt.isStatusOK
    }

}