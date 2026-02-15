package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.RecurrenceType
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.test.FakeWillpowerGoalRepository
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UpdateWillpowerGoalUseCaseTest {

    private val goalRepo = FakeWillpowerGoalRepository()

    private val useCase = UpdateWillpowerGoalUseCase(
        goalRepository = goalRepo,
        clock = { 3000L },
    )

    private val existingGoal = WillpowerGoal(
        id = "goal-1",
        title = "Old title",
        description = "Old desc",
        coinReward = Coin(5),
        xpReward = ExperiencePoints(10),
        isActive = true,
        recurrenceType = RecurrenceType.None,
        category = "",
        createdAt = 1000L,
        updatedAt = 1000L,
    )

    @Test
    fun invoke_WhenGoalExists_ShouldUpdateFields() = runTest {
        goalRepo.saveGoal(existingGoal)
        val result = useCase(
            goalId = "goal-1",
            title = "New title",
            description = "New desc",
            coinReward = 15,
            xpReward = 30,
        )
        assertIs<DomainResult.Success<WillpowerGoal>>(result)
        assertEquals("New title", result.data.title)
        assertEquals("New desc", result.data.description)
        assertEquals(Coin(15), result.data.coinReward)
        assertEquals(ExperiencePoints(30), result.data.xpReward)
        assertEquals(3000L, result.data.updatedAt)
        assertEquals(1000L, result.data.createdAt)
    }

    @Test
    fun invoke_WhenGoalNotFound_ShouldReturnError() = runTest {
        val result = useCase(
            goalId = "nonexistent",
            title = "Title",
            description = "Desc",
            coinReward = 5,
            xpReward = 10,
        )
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.GoalError.GoalNotFound>(result.error)
    }

    @Test
    fun invoke_WithBlankTitle_ShouldReturnError() = runTest {
        goalRepo.saveGoal(existingGoal)
        val result = useCase(
            goalId = "goal-1",
            title = "",
            description = "Desc",
            coinReward = 5,
            xpReward = 10,
        )
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.GoalError.BlankTitle>(result.error)
    }

    @Test
    fun invoke_WithZeroRewards_ShouldReturnError() = runTest {
        goalRepo.saveGoal(existingGoal)
        val result = useCase(
            goalId = "goal-1",
            title = "Title",
            description = "Desc",
            coinReward = 0,
            xpReward = 0,
        )
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.GoalError.InvalidReward>(result.error)
    }
}
