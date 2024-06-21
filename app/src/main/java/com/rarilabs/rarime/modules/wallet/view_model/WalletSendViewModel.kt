package com.rarilabs.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.rarilabs.rarime.manager.WalletManager
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
            selectedWalletAsset.value.let {
//                val bigIntAmount: Double = if (it.token !is Erc20Token){
//                    NumberUtil.toBigIntAmount(humanAmount.toDouble(), it.token.decimals)
//                }else{
//                    humanAmount.toDouble()
//                }
                it.token.transfer(to, BigInteger(humanAmount))


                walletManager.loadBalances()
            }
        }

    }

    suspend fun fetchBalance() {
        selectedWalletAsset.value?.let {
            withContext(Dispatchers.IO) {
                it.loadBalance()
            }
        }
    }
}