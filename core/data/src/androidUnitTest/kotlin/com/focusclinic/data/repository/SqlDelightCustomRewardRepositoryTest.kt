package com.focusclinic.data.repository

import com.focusclinic.data.TestDatabaseFactory
import com.focusclinic.domain.model.CustomReward
import com.focusclinic.domain.valueobject.Coin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SqlDelightCustomRewardRepositoryTest {

    private val database = TestDatabaseFactory.create()
    private val repository = SqlDelightCustomRewardRepository(database)

    private fun reward(
        id: String = "reward-1",
        title: String = "Coffee break",
        cost: Long = 100,
        isActive: Boolean = true,
    ) = CustomReward(
        id = id,
        title = title,
        cost = Coin(cost),
        isActive = isActive,
        createdAt = 1000L,
    )

    @Test
    fun save_AndGetById_ShouldRoundTrip() = runTest {
        repository.save(reward())

        val result = repository.getById("reward-1")
        assertNotNull(result)
        assertEquals("Coffee break", result.title)
        assertEquals(Coin(100), result.cost)
        assertTrue(result.isActive)
    }

    @Test
    fun getById_WhenNotExists_ShouldReturnNull() = runTest {
        assertNull(repository.getById("nonexistent"))
    }

    @Test
    fun observeActiveRewards_ShouldOnlyReturnActive() = runTest {
        repository.save(reward(id = "r1", isActive = true))
        repository.save(reward(id = "r2", isActive = false))

        val active = repository.observeActiveRewards().first()
        assertEquals(1, active.size)
        assertEquals("r1", active[0].id)
    }

    @Test
    fun deactivate_ShouldSoftDelete() = runTest {
        repository.save(reward(id = "r1"))
        repository.deactivate("r1")

        val active = repository.observeActiveRewards().first()
        assertTrue(active.isEmpty())

        val reward = repository.getById("r1")
        assertNotNull(reward)
        assertEquals(false, reward.isActive)
    }

    @Test
    fun save_WhenUpdating_ShouldUpsert() = runTest {
        repository.save(reward(title = "Coffee"))
        repository.save(reward(title = "Tea"))

        val result = repository.getById("reward-1")
        assertNotNull(result)
        assertEquals("Tea", result.title)
    }

    @Test
    fun observeActiveRewards_ShouldReturnMultiple() = runTest {
        repository.save(reward(id = "r1", title = "Coffee"))
        repository.save(reward(id = "r2", title = "Walk"))
        repository.save(reward(id = "r3", title = "Snack"))

        val active = repository.observeActiveRewards().first()
        assertEquals(3, active.size)
    }
}
