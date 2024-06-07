package com.distributedLab.rarime.util

object WalletUtil {
    fun formatAddress(
        address: String,
        charsStartAmount: Int = 12,
        charsEndAmount: Int = 8
    ): String {
        return address.substring(0, charsStartAmount) + "..." + address.substring(address.length - charsEndAmount)
    }
}
