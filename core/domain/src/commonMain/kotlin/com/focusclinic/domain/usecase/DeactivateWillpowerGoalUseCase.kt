package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.repository.WillpowerGoalRepository

class DeactivateWillpowerGoalUseCase(
    private val goalRepository: WillpowerGoalRepository,
) {
    suspend operator fun invoke(goalId: String): DomainResult<Unit> {
        goalRepository.deactivateGoal(goalId)
        return DomainResult.Success(Unit)
    }
}
