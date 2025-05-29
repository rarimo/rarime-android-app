package com.rarilabs.rarime.manager

import android.util.Log
import com.google.gson.Gson
import com.rarilabs.rarime.data.tokens.NativeToken
import com.rarilabs.rarime.data.tokens.PointsToken
import com.rarilabs.rarime.data.tokens.Token
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.store.SecureSharedPrefsManager
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
import java.math.BigInteger
import javax.inject.Inject
import kotlin.math.pow

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

    suspend fun loadTransactions() {
        transactions = emptyList()
    }

    fun humanBalance(): Double = balance.toDouble() / 10.0.pow(token.decimals.toDouble())
}

class WalletManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    private val identityManager: IdentityManager,
    private val pointsManager: PointsManager,
    private val web3j: Web3j
) {

    private fun createWalletAssets(): List<WalletAsset> {
        return listOf(
            WalletAsset(
                identityManager.evmAddress(),
                NativeToken(web3j, identityManager = identityManager),
            ),
            WalletAsset(
                identityManager.getUserPointsNullifierHex(), PointsToken(
                    pointsManager = pointsManager
                ),
                showInAssets = false
            ),
        )
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

    suspend fun loadBalances() = withContext(Dispatchers.IO) {
        val assets = createWalletAssets()

        try {
            coroutineScope {
                assets.map { asset ->
                    async {
                        asset.token.loadDetails()
                        asset.loadBalance()
                        asset.loadTransactions()
                        ErrorHandler.logDebug("Loaded asset", asset.token.symbol)
                    }
                }.awaitAll()
            }

            if (assets != _walletAssets.value) {
                Log.i("WalletManager", "Updating wallet assets")
                _walletAssets.value = assets
                dataStoreManager.saveWalletAssets(assets)
            }

            val newPointsToken = getPointsToken(assets)
            if (_pointsToken.value?.balanceDetails?.attributes?.amount != newPointsToken?.balanceDetails?.attributes?.amount) {
                Log.i("WalletManager", "Updating points token")
                _pointsToken.value = newPointsToken
            }

            val newSelectedWalletAsset = dataStoreManager.readSelectedWalletAsset(assets)
            if (_selectedWalletAsset.value.humanBalance() != newSelectedWalletAsset.humanBalance()) {
                Log.i("WalletManager", "Updating selected wallet asset")
                _selectedWalletAsset.value = newSelectedWalletAsset
            }
        } catch (e: Exception) {
            ErrorHandler.logError("WalletManager.loadBalances", "Failed to load balances", e)
        }
    }
}