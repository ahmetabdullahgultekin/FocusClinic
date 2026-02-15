package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.RecurrenceType
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.test.FakeWillpowerGoalRepository
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DeactivateWillpowerGoalUseCaseTest {

    private val goalRepo = FakeWillpowerGoalRepository()

    private val useCase = DeactivateWillpowerGoalUseCase(
        goalRepository = goalRepo,
    )

    @Test
    fun invoke_ShouldDeactivateGoal() = runTest {
        val goal = WillpowerGoal(
            id = "goal-1",
            title = "Test",
            description = "Desc",
            coinReward = Coin(5),
            xpReward = ExperiencePoints(10),
            isActive = true,
            recurrenceType = RecurrenceType.None,
            category = "",
            createdAt = 1000L,
            updatedAt = 1000L,
        )
        goalRepo.saveGoal(goal)

        val result = useCase("goal-1")
        assertIs<DomainResult.Success<Unit>>(result)

        val activeGoals = goalRepo.observeActiveGoals().first()
        assertTrue(activeGoals.isEmpty())
    }

    @Test
    fun invoke_WhenGoalNotFound_ShouldStillReturnSuccess() = runTest {
        val result = useCase("nonexistent")
        assertIs<DomainResult.Success<Unit>>(result)
    }
}
