package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.model.SessionStatus
import com.focusclinic.domain.test.FakeFocusSessionRepository
import com.focusclinic.domain.test.FakeInventoryRepository
import com.focusclinic.domain.test.FakeTransactionRepository
import com.focusclinic.domain.test.FakeUserProfileRepository
import com.focusclinic.domain.valueobject.FocusDuration
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InterruptFocusSessionUseCaseTest {

    private val sessionRepo = FakeFocusSessionRepository()
    private val userProfileRepo = FakeUserProfileRepository()
    private val inventoryRepo = FakeInventoryRepository()
    private val transactionRepo = FakeTransactionRepository()
    private var idCounter = 0

    private val useCase = InterruptFocusSessionUseCase(
        sessionRepository = sessionRepo,
        userProfileRepository = userProfileRepo,
        inventoryRepository = inventoryRepo,
        transactionRepository = transactionRepo,
        idGenerator = { "tx-${++idCounter}" },
        clock = { 2000L },
    )

    private suspend fun createActiveSession(duration: Int = 10): FocusSession {
        val session = FocusSession.create(
            id = "s1",
            startTime = 1000L,
            plannedDuration = FocusDuration(duration),
        )
        sessionRepo.save(session)
        return session
    }

    @Test
    fun invoke_WhenSessionNotFound_ShouldReturnNoActiveSession() = runTest {
        val result = useCase("nonexistent", 5)
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.FocusError.NoActiveSession>(result.error)
    }

    @Test
    fun invoke_WhenSessionNotFocusing_ShouldReturnNoActiveSession() = runTest {
        val session = createActiveSession().copy(status = SessionStatus.Completed)
        sessionRepo.save(session)

        val result = useCase("s1", 5)
        assertIs<DomainResult.Failure>(result)
    }

    @Test
    fun invoke_WhenValidSession_ShouldReturnInterrupted() = runTest {
        createActiveSession(10)
        val result = useCase("s1", 7)
        assertIs<DomainResult.Success<*>>(result)
        assertEquals(SessionStatus.Interrupted, (result as DomainResult.Success).data.status)
    }

    @Test
    fun invoke_WhenValidSession_ShouldSetActualMinutes() = runTest {
        createActiveSession(10)
        val result = useCase("s1", 7) as DomainResult.Success
        assertEquals(7, result.data.actualFocusMinutes)
    }

    @Test
    fun invoke_WhenValidSession_ShouldSetEndTime() = runTest {
        createActiveSession(10)
        val result = useCase("s1", 7) as DomainResult.Success
        assertEquals(2000L, result.data.endTime)
    }

    @Test
    fun invoke_WhenAboveMinDuration_ShouldCalculateReducedRewards() = runTest {
        createActiveSession(10)
        val result = useCase("s1", 7) as DomainResult.Success
        // 7 * 10 * 1.0 * (7/10) * 0.5 = 24.5 → 24
        assertEquals(24, result.data.earnedXp.value)
    }

    @Test
    fun invoke_WhenBelowMinDuration_ShouldReturnZeroRewards() = runTest {
        createActiveSession(10)
        val result = useCase("s1", 3) as DomainResult.Success
        assertEquals(0, result.data.earnedXp.value)
        assertEquals(0, result.data.earnedCoins.amount)
    }

    @Test
    fun invoke_WhenAboveMinDuration_ShouldAddXpToProfile() = runTest {
        createActiveSession(10)
        useCase("s1", 7)
        assertEquals(1, userProfileRepo.addXpCalls.size)
    }
}
