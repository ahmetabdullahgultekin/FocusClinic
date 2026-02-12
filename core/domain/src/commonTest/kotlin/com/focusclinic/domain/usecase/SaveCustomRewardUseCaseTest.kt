package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.test.FakeCustomRewardRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SaveCustomRewardUseCaseTest {

    private val rewardRepo = FakeCustomRewardRepository()
    private var idCounter = 0

    private val useCase = SaveCustomRewardUseCase(
        rewardRepository = rewardRepo,
        idGenerator = { "reward-${++idCounter}" },
        clock = { 5000L },
    )

    @Test
    fun invoke_ShouldReturnSuccess() = runTest {
        val result = useCase("Movie Night", 200)
        assertIs<DomainResult.Success<*>>(result)
    }

    @Test
    fun invoke_ShouldCreateRewardWithCorrectTitle() = runTest {
        val result = useCase("Movie Night", 200) as DomainResult.Success
        assertEquals("Movie Night", result.data.title)
    }

    @Test
    fun invoke_ShouldCreateRewardWithCorrectCost() = runTest {
        val result = useCase("Movie Night", 200) as DomainResult.Success
        assertEquals(200, result.data.cost.amount)
    }

    @Test
    fun invoke_ShouldCreateActiveReward() = runTest {
        val result = useCase("Movie Night", 200) as DomainResult.Success
        assertTrue(result.data.isActive)
    }

    @Test
    fun invoke_ShouldSetCreatedAt() = runTest {
        val result = useCase("Movie Night", 200) as DomainResult.Success
        assertEquals(5000L, result.data.createdAt)
    }

    @Test
    fun invoke_ShouldSaveToRepository() = runTest {
        val result = useCase("Movie Night", 200) as DomainResult.Success
        val saved = rewardRepo.getById(result.data.id)
        assertNotNull(saved)
        assertEquals("Movie Night", saved.title)
    }

    @Test
    fun invoke_ShouldUseIdGenerator() = runTest {
        val result = useCase("Movie Night", 200) as DomainResult.Success
        assertEquals("reward-1", result.data.id)
    }
}
