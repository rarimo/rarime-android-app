package com.rarilabs.rarime.util

import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.floor
import kotlin.math.pow

object NumberUtil {
    /**
     * Format human amount without trailing zeros
     * @param amount
     */
    fun removeTrailingZeros(amount: String): String {
        val parts = amount.split(".")
        val integer = parts[0]
        val fraction = if (parts.size > 1) parts[1] else null

        if (fraction == null) return integer

        var result = integer

        for (i in fraction.length - 1 downTo 0) {
            if (fraction[i] != '0') {
                result += ".${fraction.substring(0, i + 1)}"
                break
            }
        }

        return result
    }

    /**
     * Format human amount with prefix
     * @param value
     */
    fun convertNumberWithPrefix(value: Double): String {
        val K_PREFIX_AMOUNT = 100_000
        val M_PREFIX_AMOUNT = 1_000_000
        val B_PREFIX_AMOUNT = 1_000_000_000
        val T_PREFIX_AMOUNT = 1_000_000_000_000

        fun getPrefix(value: Double): String {
            return when {
                value >= T_PREFIX_AMOUNT -> "T"
                value >= B_PREFIX_AMOUNT -> "B"
                value >= M_PREFIX_AMOUNT -> "M"
                value >= K_PREFIX_AMOUNT -> "K"
                else -> ""
            }
        }

        val prefix = getPrefix(value)

        val divider = when (prefix) {
            "T" -> T_PREFIX_AMOUNT
            "B" -> B_PREFIX_AMOUNT
            "M" -> M_PREFIX_AMOUNT
            "K" -> K_PREFIX_AMOUNT
            else -> 1
        }

        val finalAmount =
            (value.div(divider.toDouble())).toBigDecimal().setScale(3, RoundingMode.HALF_EVEN)
                .toPlainString()

        return "${removeTrailingZeros(finalAmount)}$prefix"
    }

    fun convertNumberWithPrefix(value: BigDecimal): String {
        val K_PREFIX_AMOUNT = BigDecimal("100000")
        val M_PREFIX_AMOUNT = BigDecimal("1000000")
        val B_PREFIX_AMOUNT = BigDecimal("1000000000")
        val T_PREFIX_AMOUNT = BigDecimal("1000000000000")

        fun getPrefix(value: BigDecimal): String {
            return when {
                value >= T_PREFIX_AMOUNT -> "T"
                value >= B_PREFIX_AMOUNT -> "B"
                value >= M_PREFIX_AMOUNT -> "M"
                value >= K_PREFIX_AMOUNT -> "K"
                else -> ""
            }
        }

        val prefix = getPrefix(value)

        val divider = when (prefix) {
            "T" -> T_PREFIX_AMOUNT
            "B" -> B_PREFIX_AMOUNT
            "M" -> M_PREFIX_AMOUNT
            "K" -> K_PREFIX_AMOUNT
            else -> BigDecimal.ONE
        }

        val finalAmount =
            value.divide(divider, 3, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString()

        return "$finalAmount$prefix"
    }

    fun formatAmount(amount: BigDecimal, pattern: String = "#,###.####"): String {
        return try {
            val rounded = amount.setScale(4, RoundingMode.DOWN)
            val format = DecimalFormat(pattern, DecimalFormatSymbols(Locale.US))
            format.format(rounded)
        } catch (e: Exception) {
            ""
        }
    }



    fun formatAmount(amount: Double, pattern: String? = "#,###.####"): String {
        try {
            return DecimalFormat(pattern)
                .format(floor(amount * 10000) / 10000) // rounding
        } catch (e: Exception) {
            return ""
        }
    }


    fun formatBalance(humanAmount: BigDecimal): String {
        return convertNumberWithPrefix(humanAmount)
    }

    fun formatBalance(humanAmount: Double): String {
        return convertNumberWithPrefix(humanAmount)
    }

    fun toHumanAmount(amount: Double, decimals: Int): Double {
        return amount.div(10.0.pow(decimals.toDouble()))
    }
    fun weiToEth(wei: BigInteger): BigDecimal {
        return Convert.fromWei(wei.toBigDecimal(), Convert.Unit.ETHER)
    }

    fun toBigIntAmount(amount: Double, decimals: Int): BigInteger {
        return BigDecimal.valueOf(amount * 10.0.pow(decimals.toDouble())).toBigInteger()
    }
}