package com.focusclinic.data.repository

import com.focusclinic.data.TestDatabaseFactory
import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SqlDelightWillpowerGoalRepositoryTest {

    private val database = TestDatabaseFactory.create()
    private val repository = SqlDelightWillpowerGoalRepository(database = database)

    private val testGoal = WillpowerGoal(
        id = "goal-1",
        title = "Show patience",
        description = "Be patient with siblings",
        coinReward = Coin(10),
        xpReward = ExperiencePoints(20),
        isActive = true,
        createdAt = 1000L,
        updatedAt = 1000L,
    )

    @Test
    fun saveGoal_AndGetById_ShouldRoundTrip() = runTest {
        repository.saveGoal(testGoal)
        val loaded = repository.getGoalById("goal-1")
        assertNotNull(loaded)
        assertEquals("Show patience", loaded.title)
        assertEquals("Be patient with siblings", loaded.description)
        assertEquals(Coin(10), loaded.coinReward)
        assertEquals(ExperiencePoints(20), loaded.xpReward)
        assertTrue(loaded.isActive)
    }

    @Test
    fun getById_WhenNotExists_ShouldReturnNull() = runTest {
        assertNull(repository.getGoalById("nonexistent"))
    }

    @Test
    fun observeActiveGoals_ShouldReturnOnlyActive() = runTest {
        repository.saveGoal(testGoal)
        repository.saveGoal(testGoal.copy(id = "goal-2", title = "Another", isActive = false))

        val active = repository.observeActiveGoals().first()
        assertEquals(1, active.size)
        assertEquals("goal-1", active[0].id)
    }

    @Test
    fun deactivateGoal_ShouldMakeInactive() = runTest {
        repository.saveGoal(testGoal)
        repository.deactivateGoal("goal-1")

        val active = repository.observeActiveGoals().first()
        assertTrue(active.isEmpty())
    }

    @Test
    fun saveGoal_WhenAlreadyExists_ShouldUpsert() = runTest {
        repository.saveGoal(testGoal)
        repository.saveGoal(testGoal.copy(title = "Updated", updatedAt = 2000L))

        val loaded = repository.getGoalById("goal-1")
        assertEquals("Updated", loaded?.title)
        assertEquals(2000L, loaded?.updatedAt)
    }

    @Test
    fun recordCompletion_ShouldPersist() = runTest {
        repository.saveGoal(testGoal)
        val completion = GoalCompletion(
            id = "comp-1",
            goalId = "goal-1",
            completedAt = 2000L,
            earnedCoins = Coin(10),
            earnedXp = ExperiencePoints(20),
            note = "Did great",
        )
        repository.recordCompletion(completion)

        val completions = repository.observeCompletions("goal-1").first()
        assertEquals(1, completions.size)
        assertEquals("comp-1", completions[0].id)
        assertEquals("Did great", completions[0].note)
    }

    @Test
    fun observeAllCompletions_ShouldReturnAll() = runTest {
        repository.saveGoal(testGoal)
        repository.recordCompletion(
            GoalCompletion("c1", "goal-1", 2000L, Coin(10), ExperiencePoints(20), "")
        )
        repository.recordCompletion(
            GoalCompletion("c2", "goal-1", 3000L, Coin(10), ExperiencePoints(20), "")
        )

        val all = repository.observeAllCompletions().first()
        assertEquals(2, all.size)
    }
}
