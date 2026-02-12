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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CompleteFocusSessionUseCaseTest {

    private val sessionRepo = FakeFocusSessionRepository()
    private val userProfileRepo = FakeUserProfileRepository()
    private val inventoryRepo = FakeInventoryRepository()
    private val transactionRepo = FakeTransactionRepository()
    private var idCounter = 0

    private val useCase = CompleteFocusSessionUseCase(
        sessionRepository = sessionRepo,
        userProfileRepository = userProfileRepo,
        inventoryRepository = inventoryRepo,
        transactionRepository = transactionRepo,
        idGenerator = { "tx-${++idCounter}" },
        clock = { 2000L },
    )

    private suspend fun createActiveSession(duration: Int = 25): FocusSession {
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
        val result = useCase("nonexistent")
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.FocusError.NoActiveSession>(result.error)
    }

    @Test
    fun invoke_WhenSessionNotFocusing_ShouldReturnNoActiveSession() = runTest {
        val session = createActiveSession().copy(status = SessionStatus.Completed)
        sessionRepo.save(session)

        val result = useCase("s1")
        assertIs<DomainResult.Failure>(result)
    }

    @Test
    fun invoke_WhenValidSession_ShouldReturnCompleted() = runTest {
        createActiveSession()
        val result = useCase("s1")
        assertIs<DomainResult.Success<*>>(result)
        assertEquals(SessionStatus.Completed, (result as DomainResult.Success).data.status)
    }

    @Test
    fun invoke_WhenValidSession_ShouldSetEndTime() = runTest {
        createActiveSession()
        val result = useCase("s1") as DomainResult.Success
        assertEquals(2000L, result.data.endTime)
    }

    @Test
    fun invoke_WhenValidSession_ShouldSetActualMinutesToPlanned() = runTest {
        createActiveSession(25)
        val result = useCase("s1") as DomainResult.Success
        assertEquals(25, result.data.actualFocusMinutes)
    }

    @Test
    fun invoke_WhenValidSession_ShouldCalculateRewards() = runTest {
        createActiveSession(25) // 25min * 10 XP/min * 1.0 = 250 XP
        val result = useCase("s1") as DomainResult.Success
        assertEquals(250, result.data.earnedXp.value)
        assertEquals(125, result.data.earnedCoins.amount) // 25 * 5 * 1.0
    }

    @Test
    fun invoke_WhenValidSession_ShouldAddXpToProfile() = runTest {
        createActiveSession(25)
        useCase("s1")
        assertEquals(1, userProfileRepo.addXpCalls.size)
        assertEquals(250, userProfileRepo.addXpCalls.first().value)
    }

    @Test
    fun invoke_WhenCoinsEarned_ShouldRecordTransaction() = runTest {
        createActiveSession(25)
        useCase("s1")
        val balance = transactionRepo.getBalance()
        assertTrue(balance.amount > 0)
    }

    @Test
    fun invoke_WhenBelowMinDuration_ShouldStillComplete() = runTest {
        createActiveSession(4) // Below min reward duration
        val result = useCase("s1") as DomainResult.Success
        assertEquals(SessionStatus.Completed, result.data.status)
        assertEquals(0, result.data.earnedXp.value)
        assertEquals(0, result.data.earnedCoins.amount)
    }
}
