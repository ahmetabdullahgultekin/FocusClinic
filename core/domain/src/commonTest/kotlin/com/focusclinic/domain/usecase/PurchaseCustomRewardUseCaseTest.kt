package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.CustomReward
import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.test.FakeCustomRewardRepository
import com.focusclinic.domain.test.FakeTransactionRepository
import com.focusclinic.domain.valueobject.Coin
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PurchaseCustomRewardUseCaseTest {

    private val rewardRepo = FakeCustomRewardRepository()
    private val transactionRepo = FakeTransactionRepository()
    private var idCounter = 0

    private val useCase = PurchaseCustomRewardUseCase(
        rewardRepository = rewardRepo,
        transactionRepository = transactionRepo,
        idGenerator = { "tx-${++idCounter}" },
        clock = { 3000L },
    )

    private val testReward = CustomReward(
        id = "r1",
        title = "Movie Night",
        cost = Coin(200),
        isActive = true,
        createdAt = 1000L,
    )

    @Test
    fun invoke_WhenRewardNotFound_ShouldReturnRewardNotFound() = runTest {
        val result = useCase("nonexistent")
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.PurchaseError.RewardNotFound>(result.error)
    }

    @Test
    fun invoke_WhenInsufficientCoins_ShouldReturnInsufficientCoins() = runTest {
        rewardRepo.save(testReward)
        transactionRepo.seedBalance(100) // Need 200
        val result = useCase("r1")
        assertIs<DomainResult.Failure>(result)
        val error = result.error as DomainError.PurchaseError.InsufficientCoins
        assertEquals(200, error.required)
        assertEquals(100, error.available)
    }

    @Test
    fun invoke_WhenValid_ShouldReturnSuccess() = runTest {
        rewardRepo.save(testReward)
        transactionRepo.seedBalance(500)
        val result = useCase("r1")
        assertIs<DomainResult.Success<*>>(result)
    }

    @Test
    fun invoke_WhenValid_ShouldDeductCoins() = runTest {
        rewardRepo.save(testReward)
        transactionRepo.seedBalance(500)
        useCase("r1")
        // 500 - 200 = 300
        assertEquals(300, transactionRepo.getBalance().amount)
    }
}
