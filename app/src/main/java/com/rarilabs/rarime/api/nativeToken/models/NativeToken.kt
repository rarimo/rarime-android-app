package com.rarilabs.rarime.api.nativeToken.models

import android.text.format.DateUtils
import android.util.Log
import com.google.android.gms.common.util.DataUtils
import com.rarilabs.rarime.data.tokens.NativeToken
import com.rarilabs.rarime.data.tokens.TokenType
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import com.rarilabs.rarime.modules.wallet.models.TransactionType
import com.rarilabs.rarime.store.room.transactons.models.TransactionEntityData
import com.rarilabs.rarime.util.WalletUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.util.Date

@JsonClass(generateAdapter = true)
data class TransactionResponse(
    val items: List<TransactionItem>
)


@JsonClass(generateAdapter = true)
data class TransactionItem(
    val timestamp: String?,
    val status: String?,
    val method: String?,
    val confirmations: Int?,
    val type: Int?,
    @Json(name = "exchange_rate") val exchangeRate: String?,
    val to: Address,
    @Json(name = "result") val result: String?,
    val hash: String,
    @Json(name = "from") val from: Address,
    @Json(name = "token_transfers") val tokenTransfers: List<TokenTransfer>? = emptyList(),
    @Json(name = "transaction_types") val transactionTypes: List<String>? = emptyList(),
    @Json(name = "gas_used") val gasUsed: String?,
    @Json(name = "created_contract") val createdContract: Address? = null,
    val position: Int?,
    val nonce: Int?,
    @Json(name = "has_error_in_internal_transactions") val hasErrorInInternalTransactions: Boolean?,
    val actions: List<Action> = emptyList(),
    val value: String,
    @Json(name = "token_transfers_overflow") val tokenTransfersOverflow: Boolean?,
    @Json(name = "max_priority_fee_per_gas") val maxPriorityFeePerGas: String?,
    @Json(name = "revert_reason") val revertReason: String?,
    @Json(name = "confirmation_duration") val confirmationDuration: List<Long>? = emptyList(),
    @Json(name = "transaction_tag") val transactionTag: String?,
    @Json(name = "decoded_input") val decodedInput: DecodedInput? = null,
    @Json(name = "raw_input") val rawInput: String?
){
    companion object {
        fun toTransaction(entity: TransactionItem, walletAddress: String) : Transaction{
            Log.d("Wallet Address:", walletAddress)
            Log.d("From Address", entity.from.hash)
            Log.d("To Address", entity.from.hash )
            return Transaction(
                tokenType = TokenType.RARIMO_ETH,
                operationType = TransactionType.TRANSFER,
                from = entity.from.toString(),
                to = entity.to.toString(),
                amount = entity.value.toDouble(),
                date = Date.from(Instant.parse(entity.timestamp)),
                state = if(walletAddress.lowercase() == entity.from.hash.lowercase()) TransactionState.OUTGOING else TransactionState.INCOMING,
                id = 600
            )
        }
    }

}
@JsonClass(generateAdapter = true)
data class DecodedInput(
    @Json(name = "method_call") val methodCall: String,
    @Json(name = "method_id") val methodId: String,
    val parameters: List<DecodedInputParameter>
)

@JsonClass(generateAdapter = true)
data class DecodedInputParameter(
    val name: String,
    val type: String,
    val value: String
)

@JsonClass(generateAdapter = true)
data class Fee(
    val type: String,
    val value: String
)

@JsonClass(generateAdapter = true)
data class Address(
    val hash: String,
    @Json(name = "implementation_name") val implementationName: String?,
    val name: String?,
    @Json(name = "ens_domain_name") val ensDomainName: String?,
    val metadata: Metadata?,
    @Json(name = "is_contract") val isContract: Boolean,
    @Json(name = "private_tags") val privateTags: List<Tag>?,
    @Json(name = "watchlist_names") val watchlistNames: List<Tag>?,
    @Json(name = "public_tags") val publicTags: List<Tag>?,
    @Json(name = "is_verified") val isVerified: Boolean
)

@JsonClass(generateAdapter = true)
data class Metadata(
    val slug: String?,
    val name: String?,
    val tagType: String?,
    val ordinal: Int?,
    val meta: Map<String, Any>?
)

@JsonClass(generateAdapter = true)
data class Tag(
    @Json(name = "address_hash") val addressHash: String,
    @Json(name = "display_name") val displayName: String,
    val label: String
)

@JsonClass(generateAdapter = true)
data class TokenTransfer(
    @Json(name = "block_hash") val blockHash: String,
    @Json(name = "from") val from: Address,
    @Json(name = "log_index") val logIndex: Int,
    val method: String,
    val timestamp: String,
    val to: Address,
    val token: Token,
    val total: Total,
    @Json(name = "transaction_hash") val transactionHash: String,
    val type: String
)

@JsonClass(generateAdapter = true)
data class Token(
    @Json(name = "circulating_market_cap") val circulatingMarketCap: String?,
    @Json(name = "icon_url") val iconUrl: String?,
    val name: String,
    val decimals: String,
    val symbol: String,
    val address: String,
    val type: String,
    val holders: String?,
    @Json(name = "exchange_rate") val exchangeRate: String?,
    @Json(name = "total_supply") val totalSupply: String?
)

@JsonClass(generateAdapter = true)
data class Total(
    val decimals: String,
    val value: String
)

@JsonClass(generateAdapter = true)
data class Action(
    val data: ActionData,
    val protocol: String,
    val type: String
)

@JsonClass(generateAdapter = true)
data class ActionData(
    @Json(name = "debt_amount") val debtAmount: String,
    @Json(name = "debt_symbol") val debtSymbol: String,
    @Json(name = "debt_address") val debtAddress: String,
    @Json(name = "collateral_amount") val collateralAmount: String,
    @Json(name = "collateral_symbol") val collateralSymbol: String,
    @Json(name = "collateral_address") val collateralAddress: String,
    @Json(name = "block_number") val blockNumber: Int
)
