package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.Transaction
import com.focusclinic.domain.model.TransactionType
import com.focusclinic.domain.repository.CustomRewardRepository
import com.focusclinic.domain.repository.TransactionRepository

class PurchaseCustomRewardUseCase(
    private val rewardRepository: CustomRewardRepository,
    private val transactionRepository: TransactionRepository,
    private val idGenerator: () -> String,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(rewardId: String): DomainResult<Unit> {
        val reward = rewardRepository.getById(rewardId)
            ?: return DomainResult.Failure(DomainError.PurchaseError.RewardNotFound(rewardId))

        val balance = transactionRepository.getBalance()
        if (balance.amount < reward.cost.amount) {
            return DomainResult.Failure(
                DomainError.PurchaseError.InsufficientCoins(
                    required = reward.cost.amount,
                    available = balance.amount,
                )
            )
        }

        transactionRepository.record(
            Transaction(
                id = idGenerator(),
                type = TransactionType.SpendReward,
                amount = -reward.cost.amount,
                referenceId = rewardId,
                description = "Redeemed: ${reward.title}",
                createdAt = clock(),
            )
        )

        return DomainResult.Success(Unit)
    }
}
