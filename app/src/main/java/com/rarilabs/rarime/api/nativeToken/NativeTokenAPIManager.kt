package com.rarilabs.rarime.api.nativeToken.models

import com.rarilabs.rarime.api.nativeToken.NativeTokenAPI
import com.rarilabs.rarime.modules.wallet.models.Transaction
import javax.inject.Inject

class NativeTokenAPIManager @Inject constructor(
    private val nativeTokenAPI: NativeTokenAPI
) {
    suspend fun getAddressTransactions(walletAddress: String): List<Transaction> {
        val result = nativeTokenAPI.getTransactions(walletAddress)
        return if (result.isSuccessful) {
            result.body()?.items?.map {
                TransactionItem.toTransaction(
                    entity = it,
                    walletAddress = walletAddress
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }
}