package com.rarilabs.rarime.store.room.transactons

import com.rarilabs.rarime.modules.wallet.models.Transaction
import com.rarilabs.rarime.store.room.transactons.models.TransactionEntityData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TransactionRepository @Inject constructor(private val transactionDao: TransactionDao) {

    suspend fun getAllTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        transactionDao.getAllTransactions().map { TransactionEntityData.toTransaction(it) }
    }

    suspend fun insertTransaction(tx: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.insertTransaction(TransactionEntityData.fromTransaction(tx))
    }

    suspend fun updateTransaction(tx: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.updateTransaction(TransactionEntityData.fromTransaction(tx))
    }

    suspend fun deleteTransaction(tx: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransaction(TransactionEntityData.fromTransaction(tx))
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        transactionDao.deleteAll()
    }

    // Synchronous
    fun getAllTransactionsSync(): List<Transaction> =
        transactionDao.getAllTransactionsSync().map { TransactionEntityData.toTransaction(it) }

    fun insertTransactionSync(tx: Transaction) {
        transactionDao.insertTransactionSync(TransactionEntityData.fromTransaction(tx))
    }

    fun updateTransactionSync(tx: Transaction) {
        transactionDao.updateTransactionSync(TransactionEntityData.fromTransaction(tx))
    }

    fun deleteTransactionSync(tx: Transaction) {
        transactionDao.deleteTransactionSync(TransactionEntityData.fromTransaction(tx))
    }
}