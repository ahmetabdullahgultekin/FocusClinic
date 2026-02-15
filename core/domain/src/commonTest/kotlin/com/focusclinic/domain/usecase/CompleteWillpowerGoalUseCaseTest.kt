package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.model.RecurrenceType
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.test.FakeTransactionRepository
import com.focusclinic.domain.test.FakeUserProfileRepository
import com.focusclinic.domain.test.FakeWillpowerGoalRepository
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CompleteWillpowerGoalUseCaseTest {

    private val goalRepo = FakeWillpowerGoalRepository()
    private val transactionRepo = FakeTransactionRepository()
    private val userProfileRepo = FakeUserProfileRepository()
    private var idCounter = 0

    private val useCase = CompleteWillpowerGoalUseCase(
        goalRepository = goalRepo,
        transactionRepository = transactionRepo,
        userProfileRepository = userProfileRepo,
        calculateStreak = CalculateStreakUseCase(goalRepo, clock = { 2000L }),
        idGenerator = { "comp-${++idCounter}" },
        clock = { 2000L },
    )

    private val testGoal = WillpowerGoal(
        id = "goal-1",
        title = "Show patience",
        description = "Be patient with siblings",
        coinReward = Coin(10),
        xpReward = ExperiencePoints(20),
        isActive = true,
        recurrenceType = RecurrenceType.None,
        category = "",
        createdAt = 1000L,
        updatedAt = 1000L,
    )

    @Test
    fun invoke_WhenGoalExists_ShouldReturnCompletion() = runTest {
        goalRepo.saveGoal(testGoal)
        val result = useCase(goalId = "goal-1", note = "Did well today")
        assertIs<DomainResult.Success<GoalCompletion>>(result)
        assertEquals("goal-1", result.data.goalId)
        assertEquals(Coin(10), result.data.earnedCoins)
        assertEquals(ExperiencePoints(20), result.data.earnedXp)
        assertEquals("Did well today", result.data.note)
    }

    @Test
    fun invoke_WhenGoalNotFound_ShouldReturnError() = runTest {
        val result = useCase(goalId = "nonexistent")
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.GoalError.GoalNotFound>(result.error)
    }

    @Test
    fun invoke_ShouldAddXpToProfile() = runTest {
        goalRepo.saveGoal(testGoal)
        useCase(goalId = "goal-1")
        assertEquals(1, userProfileRepo.addXpCalls.size)
        assertEquals(ExperiencePoints(20), userProfileRepo.addXpCalls[0])
    }

    @Test
    fun invoke_ShouldRecordCoinTransaction() = runTest {
        goalRepo.saveGoal(testGoal)
        useCase(goalId = "goal-1")
        assertEquals(Coin(10), transactionRepo.getBalance())
    }

    @Test
    fun invoke_WithZeroCoinReward_ShouldNotRecordTransaction() = runTest {
        val zeroCoinGoal = testGoal.copy(coinReward = Coin(0))
        goalRepo.saveGoal(zeroCoinGoal)
        useCase(goalId = "goal-1")
        assertEquals(Coin(0), transactionRepo.getBalance())
    }
}
