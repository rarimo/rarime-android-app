package com.distributedLab.rarime.modules.common

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.distributedLab.rarime.R
import com.distributedLab.rarime.domain.manager.SecureSharedPrefsManager
import com.distributedLab.rarime.modules.wallet.models.Transaction
import com.distributedLab.rarime.modules.wallet.models.TransactionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val dataStoreManager: SecureSharedPrefsManager
) : ViewModel() {
    var balance = mutableDoubleStateOf(dataStoreManager.readWalletBalance())
        private set
    var isAirdropClaimed = mutableStateOf(false)
        private set
    var transactions = mutableStateOf(emptyList<Transaction>())
        private set

    // TODO: Get the address from the user's wallet
    val address = "rarimo10xf20zsda2hpjstl3l5ahf65tzkkdnhaxlsl8a"

    suspend fun claimAirdrop() {
        if (isAirdropClaimed.value) return

        // TODO: Claim RMO token
        delay(3.seconds)

        balance.doubleValue += 3.0
        dataStoreManager.saveWalletBalance(balance.doubleValue)

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