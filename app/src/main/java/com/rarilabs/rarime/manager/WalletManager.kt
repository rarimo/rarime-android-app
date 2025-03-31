package com.rarilabs.rarime.manager

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.rarilabs.rarime.api.cosmos.CosmosManager
import com.rarilabs.rarime.api.erc20.Erc20Manager
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.data.tokens.PointsToken
import com.rarilabs.rarime.data.tokens.Token
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import com.rarilabs.rarime.util.ErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.math.BigInteger
import javax.inject.Inject
import kotlin.math.pow

data class WalletAssetJSON(
    val tokenSymbol: String, val balance: String, val transactions: List<Transaction>
)

class WalletAsset(val userAddress: String, val token: Token) {
    var balance = mutableStateOf(BigInteger.ZERO)

    var transactions = mutableStateOf(listOf<Transaction>())

    fun toJSON(): String {
        return Gson().toJson(
            WalletAssetJSON(
                tokenSymbol = token.symbol,
                balance = balance.value.toString(),
                transactions = transactions.value
            )
        )
    }

    suspend fun loadBalance() {
        balance.value = token.balanceOf(userAddress)
    }

    suspend fun loadTransactions() {
        transactions.value = listOf()
    }

    fun humanBalance(): Double {
        return balance.value.toDouble() / 10.0.pow(token.decimals.toDouble())
    }
}

class WalletManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    private val identityManager: IdentityManager,
    private val pointsManager: PointsManager,
    private val cosmosManager: CosmosManager,
    private val stableCoinContractManager: StableCoinContractManager,
    private val erc20Manager: Erc20Manager
) {
    private fun getWalletAssets(): List<WalletAsset> {
        return dataStoreManager.readWalletAssets(
            listOf(
//                WalletAsset(
//                    identityManager.rarimoAddress(), RarimoToken(
//                        chainInfo = if (BuildConfig.isTestnet) Constants.RARIMO_CHAINS[RarimoChains.MainnetBeta.chainId]!! else Constants.RARIMO_CHAINS[RarimoChains.Mainnet.chainId]!!,
//                        identityManager, cosmosManager, dataStoreManager
//                    )
//                ),
                WalletAsset(
                    identityManager.getUserPointsNullifierHex(), PointsToken(
                        pointsManager = pointsManager
                    )
                ),
//                WalletAsset(
//                    identityManager.evmAddress(),
//                    Erc20Token(
//                        BaseConfig.STABLE_COIN_ADDRESS,
//                        stableCoinContractManager,
//                        erc20Manager,
//                        identityManager
//                    )
//                ),
            )
        )
    }

    private var _walletAssets = MutableStateFlow(getWalletAssets())
    val walletAssets: StateFlow<List<WalletAsset>>
        get() = _walletAssets.asStateFlow()

    private val _selectedWalletAsset =
        MutableStateFlow(dataStoreManager.readSelectedWalletAsset(walletAssets.value))
    val selectedWalletAsset: StateFlow<WalletAsset>
        get() = _selectedWalletAsset.asStateFlow()

    private fun getPointsToken(walletAssets: List<WalletAsset>): PointsToken? {
        return walletAssets.find { it.token is PointsToken }?.token as PointsToken?
    }

    private var _pointsToken = MutableStateFlow(getPointsToken(_walletAssets.value))
    val pointsToken: StateFlow<PointsToken?>
        get() = _pointsToken.asStateFlow()

    fun setSelectedWalletAsset(walletAsset: WalletAsset) {
        _selectedWalletAsset.value = walletAsset
        ErrorHandler.logDebug("setSelectedWalletAsset", walletAsset.token.symbol)
        dataStoreManager.saveSelectedWalletAsset(walletAsset)
    }

    suspend fun loadBalances() = withContext(Dispatchers.IO) {
        val balances = getWalletAssets()

        try {
            // Parallel execution of balance updates
            val res = coroutineScope {
                balances.forEach { balance ->
                    async {
                        balance.token.loadDetails()
                        ErrorHandler.logDebug("loadDetails", balance.token.symbol)

                        balance.loadBalance()
                        ErrorHandler.logDebug("loadBalances", balance.balance.value.toString())

                        balance.loadTransactions()
                        ErrorHandler.logDebug("loadTransactions", balance.token.toString())
                    }
                }
            }
            res.toString()

            val newWalletAssets = balances.toList()

            val areBalancesEqual =
                newWalletAssets.size == _walletAssets.value.size && newWalletAssets.zip(
                    _walletAssets.value
                ).all { (newAsset, oldAsset) ->
                    newAsset.balance.value == oldAsset.balance.value && newAsset.token.symbol == oldAsset.token.symbol
                }

            // Update _walletAssets only if data has changed
            if (!areBalancesEqual) {
                Log.i("Updated wallet", "_walletAssets.value")
                _walletAssets.value = newWalletAssets
                dataStoreManager.saveWalletAssets(newWalletAssets)
            }

            // Update _pointsToken only if it has changed
            val newPointsToken = getPointsToken(newWalletAssets)
            if (_pointsToken.value?.balanceDetails?.attributes?.amount != newPointsToken?.balanceDetails?.attributes?.amount) {

                Log.i("Updated wallet", "_pointsToken.value")
                _pointsToken.value = newPointsToken
            }

            // Update _selectedWalletAsset only if it has changed
            val newSelectedWalletAsset = dataStoreManager.readSelectedWalletAsset(newWalletAssets)
            if (_selectedWalletAsset.value.humanBalance() != newSelectedWalletAsset.humanBalance()) {

                Log.i("Updated wallet", "_selectedWalletAsset.value")
                _selectedWalletAsset.value = newSelectedWalletAsset
            }
        } catch (e: Exception) {
            // Handle exceptions appropriately
            ErrorHandler.logError("loadBalances", "Error during loading balances", e)
        }
    }
}