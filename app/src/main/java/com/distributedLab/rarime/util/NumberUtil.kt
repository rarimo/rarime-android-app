package com.distributedLab.rarime.util

import java.text.DecimalFormat
import kotlin.math.round

object NumberUtil {
    fun formatAmount(amount: Double): String {
        return DecimalFormat("#,###.##")
            .format(round(amount * 100) / 100)
    }
}