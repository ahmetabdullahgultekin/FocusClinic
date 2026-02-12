package com.focusclinic.domain.repository

import com.focusclinic.domain.model.Transaction
import com.focusclinic.domain.valueobject.Coin
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeBalance(): Flow<Coin>
    fun observeTransactions(): Flow<List<Transaction>>
    suspend fun getBalance(): Coin
    suspend fun record(transaction: Transaction)
}
