package com.focusclinic.data.repository

import com.focusclinic.data.TestDatabaseFactory
import com.focusclinic.domain.model.Transaction
import com.focusclinic.domain.model.TransactionType
import com.focusclinic.domain.valueobject.Coin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SqlDelightTransactionRepositoryTest {

    private val database = TestDatabaseFactory.create()
    private val repository = SqlDelightTransactionRepository(database)

    private fun earnTransaction(id: String, amount: Long) = Transaction(
        id = id,
        type = TransactionType.EarnFocus,
        amount = amount,
        referenceId = "session-1",
        description = "Earn $amount coins",
        createdAt = 1000L,
    )

    private fun spendTransaction(id: String, amount: Long) = Transaction(
        id = id,
        type = TransactionType.SpendShop,
        amount = -amount,
        referenceId = "item-1",
        description = "Spend $amount coins",
        createdAt = 2000L,
    )

    @Test
    fun getBalance_WhenEmpty_ShouldReturnZero() = runTest {
        assertEquals(Coin.ZERO, repository.getBalance())
    }

    @Test
    fun getBalance_AfterEarning_ShouldReturnSum() = runTest {
        repository.record(earnTransaction("t1", 100))
        repository.record(earnTransaction("t2", 200))

        assertEquals(Coin(300), repository.getBalance())
    }

    @Test
    fun getBalance_AfterSpending_ShouldSubtract() = runTest {
        repository.record(earnTransaction("t1", 500))
        repository.record(spendTransaction("t2", 200))

        assertEquals(Coin(300), repository.getBalance())
    }

    @Test
    fun getBalance_WhenNegative_ShouldCoerceToZero() = runTest {
        repository.record(spendTransaction("t1", 100))

        assertEquals(Coin.ZERO, repository.getBalance())
    }

    @Test
    fun observeBalance_ShouldReflectTransactions() = runTest {
        repository.record(earnTransaction("t1", 500))

        val balance = repository.observeBalance().first()
        assertEquals(Coin(500), balance)
    }

    @Test
    fun observeTransactions_ShouldReturnAll() = runTest {
        repository.record(earnTransaction("t1", 100))
        repository.record(spendTransaction("t2", 50))

        val txns = repository.observeTransactions().first()
        assertEquals(2, txns.size)
    }

    @Test
    fun record_ShouldPreserveTransactionType() = runTest {
        val rewardTx = Transaction(
            id = "t1",
            type = TransactionType.SpendReward,
            amount = -100,
            referenceId = "reward-1",
            description = "Redeemed coffee",
            createdAt = 1000L,
        )
        repository.record(rewardTx)

        val txns = repository.observeTransactions().first()
        assertEquals(1, txns.size)
        assertEquals(TransactionType.SpendReward, txns[0].type)
    }
}
