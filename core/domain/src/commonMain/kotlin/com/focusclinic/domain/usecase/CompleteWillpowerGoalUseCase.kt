package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.model.Transaction
import com.focusclinic.domain.model.TransactionType
import com.focusclinic.domain.repository.TransactionRepository
import com.focusclinic.domain.repository.UserProfileRepository
import com.focusclinic.domain.repository.WillpowerGoalRepository

class CompleteWillpowerGoalUseCase(
    private val goalRepository: WillpowerGoalRepository,
    private val transactionRepository: TransactionRepository,
    private val userProfileRepository: UserProfileRepository,
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
        val completion = GoalCompletion(
            id = idGenerator(),
            goalId = goalId,
            completedAt = now,
            earnedCoins = goal.coinReward,
            earnedXp = goal.xpReward,
            note = note.trim(),
        )

        goalRepository.recordCompletion(completion)
        userProfileRepository.addXp(goal.xpReward)

        if (goal.coinReward.amount > 0) {
            transactionRepository.record(
                Transaction(
                    id = idGenerator(),
                    type = TransactionType.EarnGoal,
                    amount = goal.coinReward.amount,
                    referenceId = goalId,
                    description = "Completed goal: ${goal.title}",
                    createdAt = now,
                )
            )
        }

        return DomainResult.Success(completion)
    }
}
