package com.rarilabs.rarime.data.tokens

import com.rarilabs.rarime.R
import com.rarilabs.rarime.manager.IdentityManager
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.util.Date
import javax.inject.Inject

class NativeToken @Inject constructor(
    private val web3j: Web3j,
    private val identityManager: IdentityManager
) : Token(address = "") {

    override var name: String = "RMO"
    override var symbol: String = "R"
    override var decimals: Int = 2
    override var icon: Int = R.drawable.ic_bulb

    override suspend fun loadDetails() {

    }

    override suspend fun balanceOf(address: String): BigInteger {
        val ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send()

        return ethGetBalance.balance
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {

        val privateKey = Credentials.create(identityManager.privateKey.value)

        val transactionReceipt = Transfer.sendFunds(
            web3j, privateKey,
            to,
            BigDecimal.valueOf(amount.toLong()),
            Convert.Unit.ETHER
        ).send()

        return Transaction(
            id = transactionReceipt.transactionIndex.toInt(),
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