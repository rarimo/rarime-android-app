package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributedLab.rarime.R
import com.distributedLab.rarime.domain.manager.DataStoreManager
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.modules.wallet.models.TransactionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    var balance = mutableDoubleStateOf(0.0)
        private set
    var isAirdropClaimed = mutableStateOf(false)
        private set
    var transactions = mutableStateOf(emptyList<Transaction>())
        private set

    // TODO: Get the address from the user's wallet
    val address = "rarimo10xf20zsda2hpjstl3l5ahf65tzkkdnhaxlsl8a"

    init {
        viewModelScope.launch {
            balance.doubleValue = dataStoreManager.readWalletBalance().first()
        }
    }

    suspend fun claimAirdrop() {
        if (isAirdropClaimed.value) return

        // TODO: Claim RMO token
        delay(3.seconds)
        balance.doubleValue += 3.0
        transactions.value = listOf(
            Transaction(
                id = 1,
                iconId = R.drawable.ic_airdrop,
                titleId = R.string.airdrop_tx_title,
                amount = 3.0,
                date = Date(),
                state = TransactionState.INCOMING
            )
        )
        isAirdropClaimed.value = true
    }
}