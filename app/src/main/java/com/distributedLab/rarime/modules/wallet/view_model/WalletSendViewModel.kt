package com.distributedLab.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.data.tokens.RarimoToken
import com.distributedLab.rarime.modules.common.WalletAsset
import com.distributedLab.rarime.modules.common.WalletManager
import com.distributedLab.rarime.util.NumberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class WalletSendViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    // FIXME: add multiple tokens support
    val _rmoAsset = MutableStateFlow(walletManager.walletAssets.value.find { it.token is RarimoToken })

    val rmoAsset: StateFlow<WalletAsset?>
        get() = _rmoAsset

    suspend fun sendTokens(to: String, humanAmount: String) {
        rmoAsset.value?.let { it ->
            val bigIntAmount = NumberUtil.toBigIntAmount(humanAmount.toDouble(), it.token.decimals)

            withContext(Dispatchers.IO) {
                it.token.transfer(to, BigInteger.valueOf(bigIntAmount.toLong()))
                it.loadBalance() // TODO: does it trigger recompose?
            }
        }
    }

    suspend fun fetchBalance() {
        rmoAsset.value?.let {
            withContext(Dispatchers.IO) {
                it.loadBalance() // TODO: does it trigger recompose?
            }
        }
    }
}