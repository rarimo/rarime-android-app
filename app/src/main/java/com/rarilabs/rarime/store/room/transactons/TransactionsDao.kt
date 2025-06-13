package com.rarilabs.rarime.store.room.transactons

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rarilabs.rarime.store.room.transactons.models.TransactionEntityData

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactions(): List<TransactionEntityData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionEntityData)

    @Update
    suspend fun updateTransaction(tx: TransactionEntityData)

    @Delete
    suspend fun deleteTransaction(tx: TransactionEntityData)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    // Synchronous versions if needed
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsSync(): List<TransactionEntityData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactionSync(tx: TransactionEntityData)

    @Update
    fun updateTransactionSync(tx: TransactionEntityData)

    @Delete
    fun deleteTransactionSync(tx: TransactionEntityData)

    @Query("DELETE FROM transactions")
    fun deleteAllSync()
}