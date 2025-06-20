package com.rarilabs.rarime.api.nativeToken.models

import com.rarilabs.rarime.api.nativeToken.NativeTokenAPI
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.util.ErrorHandler
import javax.inject.Inject

class NativeTokenAPIManager @Inject constructor(
    private val nativeTokenAPI: NativeTokenAPI
) {
    suspend fun getAddressTransactions(walletAddress: String): List<Transaction> {
        val response = nativeTokenAPI.getTransactions(walletAddress)

        if (response.isSuccessful) {
            return response.body()?.items?.map {
                TransactionItem.toTransaction(
                    entity = it,
                    walletAddress = walletAddress
                )
            } ?: emptyList()
        }

        when (response.code()) {
            404 -> {
                ErrorHandler.logDebug("NativeTokenApi", "User has no transactions")
                return emptyList()
            }

            422 -> {
                ErrorHandler.logError("NativeTokenApi", "Invalid address provided: $walletAddress")
                throw IllegalStateException("Invalid wallet address")
            }

            in 500..599 -> {
                val errorMsg = response.errorBody()?.string().orEmpty()
                ErrorHandler.logError("NativeTokenApi", "Server error: $errorMsg")
                throw Exception("Server error: $errorMsg")
            }

            else -> {
                val errorMsg = response.errorBody()?.string().orEmpty()
                ErrorHandler.logError("NativeTokenApi", "Unexpected error: $errorMsg")
                throw Exception("Unexpected error: $errorMsg")
            }
        }
    }

}
