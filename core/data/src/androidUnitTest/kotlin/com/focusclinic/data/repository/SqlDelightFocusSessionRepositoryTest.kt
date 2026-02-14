package com.focusclinic.data.repository

import com.focusclinic.data.TestDatabaseFactory
import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.model.SessionStatus
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import com.focusclinic.domain.valueobject.FocusDuration
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SqlDelightFocusSessionRepositoryTest {

    private val database = TestDatabaseFactory.create()
    private val repository = SqlDelightFocusSessionRepository(database)

    private fun createSession(
        id: String = "session-1",
        status: SessionStatus = SessionStatus.Focusing,
        plannedMinutes: Int = 25,
        actualMinutes: Int = 0,
        earnedXp: Long = 0,
        earnedCoins: Long = 0,
    ) = FocusSession(
        id = id,
        startTime = 1000L,
        endTime = if (status.isTerminal) 2000L else null,
        plannedDuration = FocusDuration(plannedMinutes),
        actualFocusMinutes = actualMinutes,
        status = status,
        earnedXp = ExperiencePoints(earnedXp),
        earnedCoins = Coin(earnedCoins),
    )

    @Test
    fun save_AndGetById_ShouldRoundTrip() = runTest {
        val session = createSession()
        repository.save(session)

        val result = repository.getById("session-1")

        assertNotNull(result)
        assertEquals("session-1", result.id)
        assertEquals(SessionStatus.Focusing, result.status)
        assertEquals(25, result.plannedDuration.minutes)
    }

    @Test
    fun getById_WhenNotExists_ShouldReturnNull() = runTest {
        assertNull(repository.getById("nonexistent"))
    }

    @Test
    fun save_WhenUpdating_ShouldUpsert() = runTest {
        val session = createSession()
        repository.save(session)

        val completed = session.copy(
            status = SessionStatus.Completed,
            endTime = 2000L,
            actualFocusMinutes = 25,
            earnedXp = ExperiencePoints(250),
            earnedCoins = Coin(125),
        )
        repository.save(completed)

        val result = repository.getById("session-1")
        assertNotNull(result)
        assertEquals(SessionStatus.Completed, result.status)
        assertEquals(250L, result.earnedXp.value)
        assertEquals(125L, result.earnedCoins.amount)
    }

    @Test
    fun observeActiveSession_WhenFocusing_ShouldReturnSession() = runTest {
        repository.save(createSession(status = SessionStatus.Focusing))

        val active = repository.observeActiveSession().first()
        assertNotNull(active)
        assertEquals("session-1", active.id)
    }

    @Test
    fun observeActiveSession_WhenCompleted_ShouldReturnNull() = runTest {
        repository.save(createSession(id = "s1", status = SessionStatus.Completed))

        val active = repository.observeActiveSession().first()
        assertNull(active)
    }

    @Test
    fun observeSessionHistory_ShouldReturnAllSessions() = runTest {
        repository.save(createSession(id = "s1", status = SessionStatus.Completed))
        repository.save(createSession(id = "s2", status = SessionStatus.Interrupted))

        val history = repository.observeSessionHistory().first()
        assertEquals(2, history.size)
    }
}
