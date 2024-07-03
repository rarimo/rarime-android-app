package com.rarilabs.rarime.util

object WalletUtil {
    fun formatAddress(
        address: String,
        charsStartAmount: Int = 12,
        charsEndAmount: Int = 8
    ): String {
        try {
            return address.substring(0, charsStartAmount) + "..." + address.substring(address.length - charsEndAmount)
        } catch (e: Exception) {
            ErrorHandler.logError("WalletUtil.formatAddress", e.toString(), e)
            return address
        }
    }
}
