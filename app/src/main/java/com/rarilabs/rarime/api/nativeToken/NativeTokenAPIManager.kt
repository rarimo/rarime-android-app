package com.rarilabs.rarime.api.nativeToken.models

import com.rarilabs.rarime.api.nativeToken.NativeTokenAPI
import javax.inject.Inject

class NativeTokenAPIManager @Inject constructor(
    private val nativeTokenAPI: NativeTokenAPI
) {
    suspend fun getAddressTransactions(addressHash: String): TransactionResponse? {
        val result = nativeTokenAPI.getTransactions(addressHash)
        return if (result.isSuccessful) {
            result.body()
        } else {
            null
        }
    }
}