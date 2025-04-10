package com.rarilabs.rarime.manager

import com.rarilabs.rarime.contracts.rarimo.PoseidonSMT
import com.rarilabs.rarime.contracts.rarimo.ProposalsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import org.web3j.tx.response.TransactionReceiptProcessor
import javax.inject.Inject
import javax.inject.Named

class TestContractManager @Inject constructor(@Named("Test") private val web3j: Web3j) {
    fun getProposalsStateContract(address: String): ProposalsState {
        val ecKeyPair = Keys.createEcKeyPair()

        val credentials = Credentials.create(ecKeyPair)
        val gasProvider = DefaultGasProvider()

        return ProposalsState.load(
            address, web3j, credentials, gasProvider
        )
    }

    fun getPoseidonSMT(address: String): PoseidonSMT {
        val ecKeyPair = Keys.createEcKeyPair()
        val credentials = Credentials.create(ecKeyPair)
        val gasProvider = DefaultGasProvider()

        return PoseidonSMT.load(
            address, web3j, credentials, gasProvider
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