package com.distributedLab.rarime.util

import java.text.DecimalFormat
import kotlin.math.floor

object NumberUtil {
    fun formatAmount(amount: Double, pattern: String? = "#,###.####"): String {
        return DecimalFormat(pattern)
            .format(floor(amount * 10000) / 10000) // rounding
    }

    fun toHumanAmount(amount: Double, decimals: Int): Double {
        return amount.div(Math.pow(10.0, decimals.toDouble()))
    }

    fun toBigIntAmount(amount: Double, decimals: Int): Double {
        return amount * Math.pow(10.0, decimals.toDouble())
    }
}