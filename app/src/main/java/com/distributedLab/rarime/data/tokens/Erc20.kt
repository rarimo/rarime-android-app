package com.distributedLab.rarime.data.tokens

import com.distributedLab.rarime.R
import com.distributedLab.rarime.api.erc20.Erc20Manager
import com.distributedLab.rarime.api.erc20.models.Attributes
import com.distributedLab.rarime.api.erc20.models.Data
import com.distributedLab.rarime.api.erc20.models.PermitHashAttributesRequest
import com.distributedLab.rarime.api.erc20.models.PermitHashDataRequest
import com.distributedLab.rarime.api.erc20.models.PermitHashRequest
import com.distributedLab.rarime.api.erc20.models.TransferErc20Request
import com.distributedLab.rarime.contracts.Erc20Contract
import com.distributedLab.rarime.manager.IdentityManager
import com.distributedLab.rarime.manager.StableCoinContractManager
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.modules.wallet.models.TransactionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECDSASignature
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date

class Erc20Token(
    address: String,
    private val stableCoinContractManager: StableCoinContractManager? = null,
    private val erc20Manager: Erc20Manager? = null,
    private val identityManager: IdentityManager? = null
) : Token(address) {
    override var name: String = ""
    override var symbol: String = ""
    override var decimals: Int = 0
    override var icon: Int = R.drawable.ic_metamask

    override suspend fun loadDetails() {
        val erc20Contract: Erc20Contract =
            stableCoinContractManager!!.getErc20ContractReadOnly(address)

        val name = withContext(Dispatchers.IO) { erc20Contract.name().send() }
        val symbol = withContext(Dispatchers.IO) { erc20Contract.symbol().send() }
        val decimals = withContext(Dispatchers.IO) { erc20Contract.decimals().send().toInt() }

        this.name = name
        this.symbol = symbol
        this.decimals = 0
    }

    override suspend fun balanceOf(address: String): BigInteger {
        return erc20Manager!!.getBalance(address)!!
    }

    override suspend fun transfer(to: String, amount: BigInteger): Transaction {
        val credentials = Credentials.create(identityManager!!.privateKey.value)

        val currentDateTimeInGMT = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(10)
        val deadLine = currentDateTimeInGMT.toEpochSecond()

        val permitHashRequest = PermitHashRequest(
            data = PermitHashDataRequest(
                type = "transfer_erc20", attributes = PermitHashAttributesRequest(
                    sender = identityManager.evmAddress()!!,
                    amount = amount.toLong(),
                    deadline = deadLine
                )
            )
        )

        val hash = erc20Manager!!.permitHash(permitHashRequest)!!.data.attributes.hash

        val sig: ECDSASignature = credentials.ecKeyPair.sign(Numeric.hexStringToByteArray(hash))

        val signature = Sign.createSignatureData(
            sig, credentials.ecKeyPair.publicKey, Numeric.hexStringToByteArray(hash)
        )

        val transferErc20Request = TransferErc20Request(
            data = Data(
                type = "transfer_erc20", attributes = Attributes(
                    sender = identityManager.evmAddress(),
                    receiver = to,
                    amount = amount.toLong(),
                    deadline = deadLine,
                    r = Numeric.toHexString(signature.r),
                    s = Numeric.toHexString(signature.s),
                    v = if (sig.isCanonical) 27 else 28
                )
            )
        )

        val res = erc20Manager.sendErc20Transfer(transferErc20Request)
            ?: throw IllegalStateException("Couldn't send transfer'")

        val successful =
            stableCoinContractManager!!.checkIsTransactionSuccessful(res.data.attributes.hash)

        if (!successful) {
            throw Exception("Tx has not sent")
        }

        return Transaction(
            id = deadLine.toInt(),
            iconId = R.drawable.ic_arrow_up,
            titleId = R.string.send_btn,
            amount = res.data.attributes.amount.toDouble(),
            date = Date.from(Instant.now()),
            state = TransactionState.OUTGOING,
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