package com.rarilabs.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.data.tokens.Erc20Token
import com.rarilabs.rarime.manager.WalletManager
import com.rarilabs.rarime.util.NumberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class WalletSendViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    var selectedWalletAsset = walletManager.selectedWalletAsset
        private set

    suspend fun sendTokens(to: String, humanAmount: String) {
        withContext(Dispatchers.IO) {
            selectedWalletAsset.value.let { it ->
                val bigIntAmount: BigInteger = if (it.token !is Erc20Token) {
                    NumberUtil.toBigIntAmount(humanAmount.toDouble(), it.token.decimals)
                } else {
                    BigInteger(humanAmount)
                }
                it.token.transfer(to, bigIntAmount)
                it.loadBalance()

                walletManager.loadBalances()
            }
        }

    }

    suspend fun fetchBalance() {
        selectedWalletAsset.value.let {
            withContext(Dispatchers.IO) {
                it.loadBalance()
            }
        }
    }
}