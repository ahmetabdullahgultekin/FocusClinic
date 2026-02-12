package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.SessionStatus
import com.focusclinic.domain.test.FakeFocusSessionRepository
import com.focusclinic.domain.valueobject.FocusDuration
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class StartFocusSessionUseCaseTest {

    private val sessionRepo = FakeFocusSessionRepository()
    private var idCounter = 0
    private val useCase = StartFocusSessionUseCase(
        sessionRepository = sessionRepo,
        idGenerator = { "session-${++idCounter}" },
        clock = { 1000L },
    )

    @Test
    fun invoke_WhenNoActiveSession_ShouldReturnSuccess() = runTest {
        val result = useCase(FocusDuration(25))
        assertIs<DomainResult.Success<*>>(result)
    }

    @Test
    fun invoke_WhenNoActiveSession_ShouldCreateSessionWithFocusingStatus() = runTest {
        val result = useCase(FocusDuration(25)) as DomainResult.Success
        assertEquals(SessionStatus.Focusing, result.data.status)
    }

    @Test
    fun invoke_ShouldUseProvidedIdAndClock() = runTest {
        val result = useCase(FocusDuration(25)) as DomainResult.Success
        assertEquals("session-1", result.data.id)
        assertEquals(1000L, result.data.startTime)
    }

    @Test
    fun invoke_ShouldSetCorrectDuration() = runTest {
        val result = useCase(FocusDuration(25)) as DomainResult.Success
        assertEquals(25, result.data.plannedDuration.minutes)
    }

    @Test
    fun invoke_ShouldInitializeZeroRewards() = runTest {
        val result = useCase(FocusDuration(25)) as DomainResult.Success
        assertEquals(0, result.data.earnedXp.value)
        assertEquals(0, result.data.earnedCoins.amount)
        assertNull(result.data.endTime)
    }

    @Test
    fun invoke_ShouldSaveSession() = runTest {
        val result = useCase(FocusDuration(25)) as DomainResult.Success
        val saved = sessionRepo.getById(result.data.id)
        assertEquals(result.data, saved)
    }

    @Test
    fun invoke_WhenActiveSessionExists_ShouldReturnSessionAlreadyActive() = runTest {
        useCase(FocusDuration(25)) // first session
        val result = useCase(FocusDuration(10)) // second session
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.FocusError.SessionAlreadyActive>(result.error)
    }
}
