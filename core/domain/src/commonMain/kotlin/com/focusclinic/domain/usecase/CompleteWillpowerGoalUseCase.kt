package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.model.Transaction
import com.focusclinic.domain.model.TransactionType
import com.focusclinic.domain.repository.TransactionRepository
import com.focusclinic.domain.repository.UserProfileRepository
import com.focusclinic.domain.repository.WillpowerGoalRepository
import com.focusclinic.domain.rule.StreakRules
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints

class CompleteWillpowerGoalUseCase(
    private val goalRepository: WillpowerGoalRepository,
    private val transactionRepository: TransactionRepository,
    private val userProfileRepository: UserProfileRepository,
    private val calculateStreak: CalculateStreakUseCase,
    private val idGenerator: () -> String,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(
        goalId: String,
        note: String = "",
    ): DomainResult<GoalCompletion> {
        val goal = goalRepository.getGoalById(goalId)
            ?: return DomainResult.Failure(DomainError.GoalError.GoalNotFound)

        val now = clock()

        val streakInfo = calculateStreak()
        val streakMultiplier = StreakRules.multiplierForStreak(streakInfo.current)

        val adjustedCoins = Coin((goal.coinReward.amount * streakMultiplier).toLong())
        val adjustedXp = ExperiencePoints((goal.xpReward.value * streakMultiplier).toLong())

        val completion = GoalCompletion(
            id = idGenerator(),
            goalId = goalId,
            completedAt = now,
            earnedCoins = adjustedCoins,
            earnedXp = adjustedXp,
            note = note.trim(),
        )

        goalRepository.recordCompletion(completion)
        userProfileRepository.addXp(adjustedXp)

        if (adjustedCoins.amount > 0) {
            val streakDesc = if (streakMultiplier > StreakRules.BASE_MULTIPLIER) {
                " (${streakMultiplier}x streak bonus)"
            } else {
                ""
            }
            transactionRepository.record(
                Transaction(
                    id = idGenerator(),
                    type = TransactionType.EarnGoal,
                    amount = adjustedCoins.amount,
                    referenceId = goalId,
                    description = "Completed goal: ${goal.title}$streakDesc",
                    createdAt = now,
                )
            )
        }

        return DomainResult.Success(completion)
    }
}
