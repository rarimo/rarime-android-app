package com.rarilabs.rarime.store.room.transactons.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rarilabs.rarime.data.tokens.TokenType
import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.modules.wallet.models.TransactionState
import com.rarilabs.rarime.modules.wallet.models.TransactionType
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntityData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tokenType: String,
    val operationType: Int,
    val from: String,
    val to: String,
    val amount: Double,
    val date: Long,
    val state: String
) {
    companion object {
        fun fromTransaction(tx: Transaction): TransactionEntityData {
            return TransactionEntityData(
                id = tx.id,
                tokenType = tx.tokenType.name,
                operationType = tx.operationType.value,
                from = tx.from,
                to = tx.to,
                amount = tx.amount,
                date = tx.date.time,
                state = tx.state.name
            )
        }

        fun toTransaction(entity: TransactionEntityData): Transaction {
            return Transaction(
                id = entity.id,
                tokenType = TokenType.valueOf(entity.tokenType),
                operationType = TransactionType.entries.first { it.value == entity.operationType },
                from = entity.from,
                to = entity.to,
                amount = entity.amount,
                date = Date(entity.date),
                state = TransactionState.valueOf(entity.state)
            )
        }
    }
}