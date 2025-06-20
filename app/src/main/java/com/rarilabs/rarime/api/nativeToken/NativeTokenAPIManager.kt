package com.rarilabs.rarime.api.nativeToken.models

import com.rarilabs.rarime.api.nativeToken.NativeTokenAPI
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.util.ErrorHandler
import javax.inject.Inject

class NativeTokenAPIManager @Inject constructor(
    private val nativeTokenAPI: NativeTokenAPI
) {
    suspend fun getAddressTransactions(walletAddress: String): List<Transaction> {
        val result = nativeTokenAPI.getTransactions(walletAddress)
        when (result.code()) {
            in 200..299 -> {
                return result.body()?.items?.map {
                    TransactionItem.toTransaction(
                        entity = it,
                        walletAddress = walletAddress
                    )
                } ?: emptyList()
            }

            404 -> {
                ErrorHandler.logDebug("NativeTokenApi", "User has no transaction")
                return emptyList()
            }

            422 -> {
                ErrorHandler.logError("NativeTokenApi", "Invalid address")
                return emptyList()
            }

            in 500..599 -> {
                ErrorHandler.logError("NativeTokenApi", "Server is down")
                return emptyList()
            }

            else -> {
                ErrorHandler.logError("NativeTokenApi", "Something went wrong")
                return emptyList()
            }

        }

    }

}
