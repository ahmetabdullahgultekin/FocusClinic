package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.repository.WillpowerGoalRepository
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints

class UpdateWillpowerGoalUseCase(
    private val goalRepository: WillpowerGoalRepository,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(
        goalId: String,
        title: String,
        description: String,
        coinReward: Long,
        xpReward: Long,
    ): DomainResult<WillpowerGoal> {
        if (title.isBlank()) {
            return DomainResult.Failure(DomainError.GoalError.BlankTitle)
        }
        if (coinReward <= 0 && xpReward <= 0) {
            return DomainResult.Failure(DomainError.GoalError.InvalidReward)
        }

        val existing = goalRepository.getGoalById(goalId)
            ?: return DomainResult.Failure(DomainError.GoalError.GoalNotFound)

        val updated = existing.copy(
            title = title.trim(),
            description = description.trim(),
            coinReward = Coin(coinReward),
            xpReward = ExperiencePoints(xpReward),
            updatedAt = clock(),
        )
        goalRepository.saveGoal(updated)
        return DomainResult.Success(updated)
    }
}
