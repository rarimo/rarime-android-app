package com.distributedLab.rarime.util

import java.text.DecimalFormat
import kotlin.math.floor

object NumberUtil {
    fun formatAmount(amount: Double): String {
        return DecimalFormat("#,###.####")
            .format(floor(amount * 10000) / 10000)
    }
}