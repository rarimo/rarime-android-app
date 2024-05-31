package com.distributedLab.rarime.modules.wallet.view_model

import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.modules.common.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalletReceiveViewModel @Inject constructor(
    private val walletManager: WalletManager,
) : ViewModel() {
    val address = walletManager.rarimoAddress
}