package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.CustomReward
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.test.FakeCustomRewardRepository
import com.focusclinic.domain.valueobject.Coin
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs

class DeactivateCustomRewardUseCaseTest {

    private val rewardRepo = FakeCustomRewardRepository()
    private val useCase = DeactivateCustomRewardUseCase(rewardRepository = rewardRepo)

    @Test
    fun invoke_ShouldReturnSuccess() = runTest {
        rewardRepo.save(
            CustomReward(
                id = "r1",
                title = "Test",
                cost = Coin(100),
                isActive = true,
                createdAt = 0L,
            )
        )
        val result = useCase("r1")
        assertIs<DomainResult.Success<*>>(result)
    }

    @Test
    fun invoke_ShouldDeactivateReward() = runTest {
        rewardRepo.save(
            CustomReward(
                id = "r1",
                title = "Test",
                cost = Coin(100),
                isActive = true,
                createdAt = 0L,
            )
        )
        useCase("r1")
        val reward = rewardRepo.getById("r1")
        assertFalse(reward!!.isActive)
    }
}
