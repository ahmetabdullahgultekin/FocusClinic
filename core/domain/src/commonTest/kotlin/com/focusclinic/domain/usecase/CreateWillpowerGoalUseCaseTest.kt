package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.test.FakeWillpowerGoalRepository
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CreateWillpowerGoalUseCaseTest {

    private val goalRepo = FakeWillpowerGoalRepository()
    private var idCounter = 0

    private val useCase = CreateWillpowerGoalUseCase(
        goalRepository = goalRepo,
        idGenerator = { "goal-${++idCounter}" },
        clock = { 1000L },
    )

    @Test
    fun invoke_WithValidInput_ShouldReturnSuccess() = runTest {
        val result = useCase(
            title = "Show patience",
            description = "Be patient with siblings",
            coinReward = 10,
            xpReward = 20,
        )
        assertIs<DomainResult.Success<WillpowerGoal>>(result)
        assertEquals("Show patience", result.data.title)
        assertEquals(Coin(10), result.data.coinReward)
        assertEquals(ExperiencePoints(20), result.data.xpReward)
        assertTrue(result.data.isActive)
    }

    @Test
    fun invoke_WithBlankTitle_ShouldReturnBlankTitleError() = runTest {
        val result = useCase(title = "  ", description = "", coinReward = 10, xpReward = 20)
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.GoalError.BlankTitle>(result.error)
    }

    @Test
    fun invoke_WithZeroRewards_ShouldReturnInvalidRewardError() = runTest {
        val result = useCase(title = "Test", description = "", coinReward = 0, xpReward = 0)
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.GoalError.InvalidReward>(result.error)
    }

    @Test
    fun invoke_ShouldSaveGoalToRepository() = runTest {
        useCase(title = "Test Goal", description = "Desc", coinReward = 5, xpReward = 10)
        val saved = goalRepo.getGoalById("goal-1")
        assertEquals("Test Goal", saved?.title)
    }

    @Test
    fun invoke_ShouldTrimWhitespace() = runTest {
        val result = useCase(
            title = "  Patience  ",
            description = "  Be kind  ",
            coinReward = 5,
            xpReward = 10,
        ) as DomainResult.Success
        assertEquals("Patience", result.data.title)
        assertEquals("Be kind", result.data.description)
    }
}
