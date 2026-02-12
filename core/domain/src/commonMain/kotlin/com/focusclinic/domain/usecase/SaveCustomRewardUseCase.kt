package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.CustomReward
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.repository.CustomRewardRepository
import com.focusclinic.domain.valueobject.Coin

class SaveCustomRewardUseCase(
    private val rewardRepository: CustomRewardRepository,
    private val idGenerator: () -> String,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(title: String, cost: Long): DomainResult<CustomReward> {
        val reward = CustomReward(
            id = idGenerator(),
            title = title,
            cost = Coin(cost),
            isActive = true,
            createdAt = clock(),
        )
        rewardRepository.save(reward)
        return DomainResult.Success(reward)
    }
}
