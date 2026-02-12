package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.repository.CustomRewardRepository

class DeactivateCustomRewardUseCase(
    private val rewardRepository: CustomRewardRepository,
) {
    suspend operator fun invoke(rewardId: String): DomainResult<Unit> {
        rewardRepository.deactivate(rewardId)
        return DomainResult.Success(Unit)
    }
}
