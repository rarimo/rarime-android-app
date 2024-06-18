package com.distributedLab.rarime.manager

import com.distributedLab.rarime.contracts.Erc20Contract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import org.web3j.tx.response.TransactionReceiptProcessor
import javax.inject.Inject

class StableCoinContractManager @Inject constructor(private val web3j: Web3j) {
    fun getErc20ContractReadOnly(address: String): Erc20Contract {
        val ecKeyPair = Keys.createEcKeyPair()

        val credentials = Credentials.create(ecKeyPair)
        val gasProvider = DefaultGasProvider()


        val transactionManager = ReadonlyTransactionManager(web3j, credentials.address)

        return Erc20Contract.load(
            address, web3j, transactionManager, gasProvider
        )
    }

    suspend fun checkIsTransactionSuccessful(txHash: String): Boolean {
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