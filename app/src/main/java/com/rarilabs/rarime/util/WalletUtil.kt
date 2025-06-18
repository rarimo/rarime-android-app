package com.rarilabs.rarime.util

import java.math.BigDecimal

object WalletUtil {
    fun formatAddress(
        address: String,
        charsStartAmount: Int = 12,
        charsEndAmount: Int = 8
    ): String {
        try {
            return address.substring(
                0,
                charsStartAmount
            ) + "..." + address.substring(address.length - charsEndAmount)
        } catch (e: Exception) {
            ErrorHandler.logError("WalletUtil.formatAddress", e.toString(), e)
            return address
        }
    }

    fun isValidateAmountForSend(rawAmount: String, balance: BigDecimal? = null): Boolean {
        val amount = rawAmount.toBigDecimalOrNull() ?: return false
        if (amount.stripTrailingZeros() == BigDecimal.ZERO)
            return false
        val biggerThanAmount = amount > BigDecimal.ZERO


        if (balance == null)
            return biggerThanAmount

        val res = amount <= balance
        return res
    }

    fun isValidAddressForSend(address: String, userAddress: String): Boolean {
        return isValidAddress(address)
    }

    fun isValidAddress(address: String): Boolean {
        return org.web3j.crypto.WalletUtils.isValidAddress(address)
    }
}
