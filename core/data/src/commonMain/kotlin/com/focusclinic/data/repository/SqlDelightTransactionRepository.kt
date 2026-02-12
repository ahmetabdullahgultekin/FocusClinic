package com.focusclinic.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.focusclinic.data.database.FocusClinicDatabase
import com.focusclinic.data.mapper.toDomain
import com.focusclinic.data.mapper.toDbString
import com.focusclinic.domain.model.Transaction
import com.focusclinic.domain.repository.TransactionRepository
import com.focusclinic.domain.valueobject.Coin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightTransactionRepository(
    private val database: FocusClinicDatabase,
) : TransactionRepository {

    private val queries get() = database.transactionsQueries

    override fun observeBalance(): Flow<Coin> =
        queries.getBalance()
            .asFlow()
            .mapToOne(Dispatchers.Default)
            .map { balance -> Coin(balance.coerceAtLeast(0)) }

    override fun observeTransactions(): Flow<List<Transaction>> =
        queries.getAllByDateDesc()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getBalance(): Coin {
        val balance = queries.getBalance().executeAsOne()
        return Coin(balance.coerceAtLeast(0))
    }

    override suspend fun record(transaction: Transaction) {
        queries.insert(
            id = transaction.id,
            type = transaction.type.toDbString(),
            amount = transaction.amount,
            reference_id = transaction.referenceId,
            description = transaction.description,
            created_at = transaction.createdAt,
        )
    }
}
