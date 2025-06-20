package com.rarilabs.rarime.manager

import android.util.Log
import com.google.gson.Gson
import com.rarilabs.rarime.data.tokens.NativeToken
import com.rarilabs.rarime.data.tokens.PointsToken
import com.rarilabs.rarime.data.tokens.Token
import com.rarilabs.rarime.data.tokens.TokenType
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.store.room.transactons.TransactionRepository
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.web3j.protocol.Web3j
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

data class WalletAssetJSON(
    val tokenSymbol: String, val balance: String, val transactions: List<Transaction>
)

data class WalletAsset(
    val userAddress: String,
    val token: Token,
    var balance: BigInteger = BigInteger.ZERO,
    var transactions: List<Transaction> = emptyList(),
    var showInAssets: Boolean = true
) {
    fun toJSON(): String = Gson().toJson(
        WalletAssetJSON(
            tokenSymbol = token.symbol, balance = balance.toString(), transactions = transactions
        )
    )

    suspend fun loadBalance() {
        balance = token.balanceOf(userAddress)
    }

    fun humanBalance(): BigDecimal = balance.toBigDecimal()
        .divide(BigDecimal.TEN.pow(token.decimals), token.decimals, java.math.RoundingMode.DOWN)
}

@Singleton
class WalletManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    private val identityManager: IdentityManager,
    private val pointsManager: PointsManager,
    private val transactionRepository: TransactionRepository,
    private val web3j: Web3j
) {

    private fun createWalletAssets(): List<WalletAsset> {
        val list = listOf(
            WalletAsset(
                identityManager.evmAddress(),
                NativeToken(web3j, identityManager = identityManager),
            ),
            WalletAsset(
                identityManager.getUserPointsNullifierHex(), PointsToken(
                    pointsManager = pointsManager
                ), showInAssets = false
            ),
        )
        return list
    }

    private val _walletAssets = MutableStateFlow(createWalletAssets())
    val walletAssets: StateFlow<List<WalletAsset>> = _walletAssets.asStateFlow()

    private val _selectedWalletAsset = MutableStateFlow(
        dataStoreManager.readSelectedWalletAsset(_walletAssets.value)
    )
    val selectedWalletAsset: StateFlow<WalletAsset> = _selectedWalletAsset.asStateFlow()

    private val _pointsToken = MutableStateFlow(getPointsToken(_walletAssets.value))
    val pointsToken: StateFlow<PointsToken?> = _pointsToken.asStateFlow()

    private fun getPointsToken(walletAssets: List<WalletAsset>) =
        walletAssets.find { it.token is PointsToken }?.token as? PointsToken

    fun setSelectedWalletAsset(walletAsset: WalletAsset) {
        _selectedWalletAsset.value = walletAsset
        ErrorHandler.logDebug("setSelectedWalletAsset", walletAsset.token.symbol)
        dataStoreManager.saveSelectedWalletAsset(walletAsset)
    }


    suspend fun insertTransaction(transaction: Transaction) {
        transactionRepository.insertTransaction(transaction)
    }

    private suspend fun loadTransactionsByTokenType(tokenType: TokenType): List<Transaction> {
        val allTransactions = transactionRepository.getAllTransactions()

        return allTransactions.filter { it.tokenType == tokenType }
    }

    suspend fun loadBalances() = withContext(Dispatchers.IO) {
        val assets = createWalletAssets()

        try {
            coroutineScope {
                assets.map { asset ->
                    async {
                        asset.token.loadDetails()
                        asset.loadBalance()
                        asset.transactions = loadTransactionsByTokenType(asset.token.tokenType)
                    }
                }.awaitAll()
            }


            //Default token asset
            setSelectedWalletAsset(assets.first { it.token is NativeToken })

            Log.i("WalletManager", "Updating wallet assets")
            _walletAssets.value = assets
            dataStoreManager.saveWalletAssets(assets)


            val newPointsToken = getPointsToken(assets)
            if (_pointsToken.value?.balanceDetails?.attributes?.amount != newPointsToken?.balanceDetails?.attributes?.amount) {
                Log.i("WalletManager", "Updating points token")
                _pointsToken.value = newPointsToken
            }

            val newSelectedWalletAsset = dataStoreManager.readSelectedWalletAsset(assets)
            if (_selectedWalletAsset.value.humanBalance() != newSelectedWalletAsset.humanBalance()) {
                Log.i("WalletManager", "Updating selected wallet asset")
                setSelectedWalletAsset(newSelectedWalletAsset)
            }
        } catch (e: Exception) {
            ErrorHandler.logError("WalletManager.loadBalances", "Failed to load balances", e)
        }
    }
}