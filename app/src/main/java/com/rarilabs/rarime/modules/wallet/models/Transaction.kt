package com.rarilabs.rarime.modules.wallet.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.util.Date

enum class TransactionState {
    INCOMING,
    OUTGOING
}

data class Transaction(
    val id: Int,
    @DrawableRes val iconId: Int,
    @StringRes val titleId: Int,
    val amount: Double,
    val date: Date,
    val state: TransactionState
)