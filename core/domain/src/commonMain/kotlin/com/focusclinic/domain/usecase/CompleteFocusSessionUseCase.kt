package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.model.SessionStatus
import com.focusclinic.domain.model.Transaction
import com.focusclinic.domain.model.TransactionType
import com.focusclinic.domain.repository.FocusSessionRepository
import com.focusclinic.domain.repository.InventoryRepository
import com.focusclinic.domain.repository.TransactionRepository
import com.focusclinic.domain.repository.UserProfileRepository
import com.focusclinic.domain.rule.MultiplierCalculator
import com.focusclinic.domain.rule.RewardCalculator

class CompleteFocusSessionUseCase(
    private val sessionRepository: FocusSessionRepository,
    private val userProfileRepository: UserProfileRepository,
    private val inventoryRepository: InventoryRepository,
    private val transactionRepository: TransactionRepository,
    private val idGenerator: () -> String,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(sessionId: String): DomainResult<FocusSession> {
        val session = sessionRepository.getById(sessionId)
            ?: return DomainResult.Failure(DomainError.FocusError.NoActiveSession)

        if (session.status != SessionStatus.Focusing) {
            return DomainResult.Failure(DomainError.FocusError.NoActiveSession)
        }

        val inventory = inventoryRepository.getAll()
        val xpMultiplier = MultiplierCalculator.computeXpMultiplier(inventory)
        val coinMultiplier = MultiplierCalculator.computeCoinMultiplier(inventory)

        val reward = RewardCalculator.calculateCompleted(
            duration = session.plannedDuration,
            xpMultiplier = xpMultiplier,
            coinMultiplier = coinMultiplier,
        )

        val now = clock()
        val completedSession = session.copy(
            endTime = now,
            actualFocusMinutes = session.plannedDuration.minutes,
            status = SessionStatus.Completed,
            earnedXp = reward.xp,
            earnedCoins = reward.coins,
        )

        sessionRepository.save(completedSession)
        userProfileRepository.addXp(reward.xp)

        if (reward.coins.amount > 0) {
            transactionRepository.record(
                Transaction(
                    id = idGenerator(),
                    type = TransactionType.EarnFocus,
                    amount = reward.coins.amount,
                    referenceId = sessionId,
                    description = "Completed ${session.plannedDuration.minutes}min session",
                    createdAt = now,
                )
            )
        }

        return DomainResult.Success(completedSession)
    }
}
