package com.distributedLab.rarime.util

object WalletUtil {
    fun formatAddress(address: String): String {
        return address.substring(0, 12) + "..." + address.substring(address.length - 8)
    }
}
