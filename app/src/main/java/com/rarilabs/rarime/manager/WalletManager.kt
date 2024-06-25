package com.rarilabs.rarime.manager

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.rarilabs.rarime.BaseConfig
import com.rarilabs.rarime.api.cosmos.CosmosManager
import com.rarilabs.rarime.api.erc20.Erc20Manager
import com.rarilabs.rarime.api.points.PointsManager
import com.rarilabs.rarime.data.RarimoChains
import com.rarilabs.rarime.data.tokens.PointsToken
import com.rarilabs.rarime.data.tokens.RarimoToken
import com.rarilabs.rarime.data.tokens.Token
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.store.SecureSharedPrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private var _walletAssets = MutableStateFlow(
        dataStoreManager.readWalletAssets(
            listOf(
                WalletAsset(
                    identityManager.rarimoAddress(), RarimoToken(
                        BaseConfig.RARIMO_CHAINS[RarimoChains.Mainnet.chainId]!!, // FIXME: !!
                        identityManager, cosmosManager, dataStoreManager
                    )
                ), WalletAsset(
                    identityManager.getUserPointsNullifierHex(), PointsToken(
                        pointsManager = pointsManager
                    )
                )/*WalletAsset(
                                   identityManager.evmAddress(), Erc20Token(
                                       BaseConfig.STABLE_COIN_ADDRESS,
                                       stableCoinContractManager,
                                       erc20Manager,
                                       identityManager
                                   )
                               ),
                               )*/
            )
        )
    )
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
        Log.i("setSelectedWalletAsset", _selectedWalletAsset.value.toJSON())
        dataStoreManager.saveSelectedWalletAsset(walletAsset)
    }

    suspend fun loadBalances() {
        withContext(Dispatchers.IO) {
            val balances = _walletAssets.value

            coroutineScope {
                balances.forEach { balance ->
                    launch {
                        balance.token.loadDetails()
                        Log.i("loadDetails", balance.token.symbol)
                    }
                    launch {
                        balance.loadBalance()
                        Log.i("loadBalances", balance.balance.value.toString())
                    }
                    launch {
                        balance.loadTransactions()
                        Log.i("loadTransactions", balance.token.toString())
                    }
                }
            }
            _walletAssets.value = balances.toList()

            dataStoreManager.saveWalletAssets(_walletAssets.value)

            _pointsToken.value = getPointsToken(balances.toList())

            Log.i(
                "_pointsToken.value?.balanceDetails",
                Gson().toJson(_pointsToken.value?.balanceDetails)
            )
        }
    }
}