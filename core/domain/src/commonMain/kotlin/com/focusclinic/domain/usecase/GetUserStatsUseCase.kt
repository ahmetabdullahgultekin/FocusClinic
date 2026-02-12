package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.UserProfile
import com.focusclinic.domain.repository.InventoryRepository
import com.focusclinic.domain.repository.TransactionRepository
import com.focusclinic.domain.repository.UserProfileRepository
import com.focusclinic.domain.rule.MultiplierCalculator
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.Multiplier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class UserStats(
    val profile: UserProfile,
    val balance: Coin,
    val xpMultiplier: Multiplier,
    val coinMultiplier: Multiplier,
)

class GetUserStatsUseCase(
    private val userProfileRepository: UserProfileRepository,
    private val transactionRepository: TransactionRepository,
    private val inventoryRepository: InventoryRepository,
) {
    operator fun invoke(): Flow<UserStats> = combine(
        userProfileRepository.observeProfile(),
        transactionRepository.observeBalance(),
        inventoryRepository.observeInventory(),
    ) { profile, balance, inventory ->
        UserStats(
            profile = profile,
            balance = balance,
            xpMultiplier = MultiplierCalculator.computeXpMultiplier(inventory),
            coinMultiplier = MultiplierCalculator.computeCoinMultiplier(inventory),
        )
    }
}
