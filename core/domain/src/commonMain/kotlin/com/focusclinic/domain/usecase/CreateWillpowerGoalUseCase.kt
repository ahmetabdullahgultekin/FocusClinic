package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.RecurrenceType
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.repository.WillpowerGoalRepository
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints

class CreateWillpowerGoalUseCase(
    private val goalRepository: WillpowerGoalRepository,
    private val idGenerator: () -> String,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        coinReward: Long,
        xpReward: Long,
        recurrenceType: RecurrenceType = RecurrenceType.None,
        category: String = "",
    ): DomainResult<WillpowerGoal> {
        if (title.isBlank()) {
            return DomainResult.Failure(DomainError.GoalError.BlankTitle)
        }
        if (coinReward <= 0 && xpReward <= 0) {
            return DomainResult.Failure(DomainError.GoalError.InvalidReward)
        }

        val now = clock()
        val goal = WillpowerGoal(
            id = idGenerator(),
            title = title.trim(),
            description = description.trim(),
            coinReward = Coin(coinReward),
            xpReward = ExperiencePoints(xpReward),
            isActive = true,
            recurrenceType = recurrenceType,
            category = category.trim(),
            createdAt = now,
            updatedAt = now,
        )
        goalRepository.saveGoal(goal)
        return DomainResult.Success(goal)
    }
}
