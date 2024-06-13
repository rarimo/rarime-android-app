package com.distributedLab.rarime.modules.common

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.distributedLab.rarime.BaseConfig
import com.distributedLab.rarime.api.cosmos.CosmosManager
import com.distributedLab.rarime.api.points.PointsAPIManager
import com.distributedLab.rarime.data.RarimoChains
import com.distributedLab.rarime.data.tokens.Erc20Token
import com.distributedLab.rarime.data.tokens.PointsToken
import com.distributedLab.rarime.data.tokens.RarimoToken
import com.distributedLab.rarime.data.tokens.Token
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.math.BigInteger
import javax.inject.Inject

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
        return balance.value.divide(
            BigInteger.TEN.pow(token.decimals)
        ).toDouble()
    }
}

class WalletManager @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager,
    private val identityManager: IdentityManager,
    private val pointsAPIManager: PointsAPIManager,
    private val cosmosManager: CosmosManager
) {
    var _isPointsBalanceCreated = MutableStateFlow(false)
        private set

    val isPointsBalanceCreated: StateFlow<Boolean>
        get() = _isPointsBalanceCreated.asStateFlow()

    private var _walletAssets = MutableStateFlow(
        dataStoreManager.readWalletAssets(
            listOf(
                WalletAsset(
                    identityManager.rarimoAddress(), RarimoToken(
                        BaseConfig.RARIMO_CHAINS[RarimoChains.MainnetBeta.chainId]!!, // FIXME: !!
                        identityManager,
                        cosmosManager,
                    )
                ), WalletAsset(
                    identityManager.evmAddress,
                    Erc20Token("0x0000000000000000000000000000000000000000")
                ), WalletAsset(
                    identityManager.rarimoAddress(),
                    PointsToken(
                        identityManager = identityManager,
                        pointsAPIManager = pointsAPIManager
                    )
                )
            )
        )
    )

    private val _isSpecificClaimed = MutableStateFlow(
        dataStoreManager.readIsSpecificClaimed()
    )
    val isSpecificClaimed: StateFlow<Boolean>
        get() = _isSpecificClaimed.asStateFlow()


    private val _isReserved = MutableStateFlow(
        dataStoreManager.readIsReserved()
    )

    val isReserved: StateFlow<Boolean>
        get() = _isReserved.asStateFlow()


    val walletAssets: StateFlow<List<WalletAsset>>
        get() = _walletAssets.asStateFlow()

    private val _selectedWalletAsset =
        MutableStateFlow(dataStoreManager.readSelectedWalletAsset(walletAssets.value))

    fun updateIsReserved() {
        _isReserved.value = true
        dataStoreManager.saveIsReserved()
    }

    fun updateIsSpecificClaimed() {
        _isSpecificClaimed.value = true
        dataStoreManager.readIsSpecificClaimed()
    }

    val selectedWalletAsset: StateFlow<WalletAsset>
        get() = _selectedWalletAsset.asStateFlow()

    fun setSelectedWalletAsset(walletAsset: WalletAsset) {
        _selectedWalletAsset.value = walletAsset
        Log.i("setSelectedWalletAsset", _selectedWalletAsset.value.toJSON())

        dataStoreManager.saveSelectedWalletAsset(walletAsset)
    }

    suspend fun loadBalances() {
        withContext(Dispatchers.IO) {
            _walletAssets.value.forEach {
                it.token.loadDetails()

                // TODO: Promise.all
                it.loadBalance()
                it.loadTransactions()
            }

            dataStoreManager.saveWalletAssets(_walletAssets.value)
        }
    }

    private var isAirdropClaimed = mutableStateOf(false)
}