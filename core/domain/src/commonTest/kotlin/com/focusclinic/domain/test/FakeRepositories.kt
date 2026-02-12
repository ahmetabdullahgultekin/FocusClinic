package com.focusclinic.domain.test

import com.focusclinic.domain.model.CustomReward
import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.model.InventoryItem
import com.focusclinic.domain.model.Transaction
import com.focusclinic.domain.model.UserProfile
import com.focusclinic.domain.repository.CustomRewardRepository
import com.focusclinic.domain.repository.FocusSessionRepository
import com.focusclinic.domain.repository.InventoryRepository
import com.focusclinic.domain.repository.TransactionRepository
import com.focusclinic.domain.repository.UserProfileRepository
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import com.focusclinic.domain.rule.PlayerLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFocusSessionRepository : FocusSessionRepository {
    private val sessions = MutableStateFlow<Map<String, FocusSession>>(emptyMap())

    override fun observeActiveSession(): Flow<FocusSession?> =
        sessions.map { map -> map.values.firstOrNull { !it.status.isTerminal } }

    override fun observeSessionHistory(): Flow<List<FocusSession>> =
        sessions.map { it.values.toList() }

    override suspend fun save(session: FocusSession) {
        sessions.value = sessions.value + (session.id to session)
    }

    override suspend fun getById(id: String): FocusSession? = sessions.value[id]
}

class FakeUserProfileRepository : UserProfileRepository {
    private val profile = MutableStateFlow(
        UserProfile(
            totalXp = ExperiencePoints.ZERO,
            level = PlayerLevel.INTERN,
            createdAt = 0L,
        )
    )

    var addXpCalls = mutableListOf<ExperiencePoints>()

    override fun observeProfile(): Flow<UserProfile> = profile

    override suspend fun getProfile(): UserProfile = profile.value

    override suspend fun addXp(xp: ExperiencePoints) {
        addXpCalls.add(xp)
        val current = profile.value
        val newXp = current.totalXp + xp
        profile.value = current.copy(
            totalXp = newXp,
            level = PlayerLevel.fromXp(newXp),
        )
    }

    override suspend fun ensureProfileExists() {}
}

class FakeInventoryRepository : InventoryRepository {
    private val items = MutableStateFlow<List<InventoryItem>>(emptyList())

    fun setItems(newItems: List<InventoryItem>) {
        items.value = newItems
    }

    override fun observeInventory(): Flow<List<InventoryItem>> = items

    override suspend fun getAll(): List<InventoryItem> = items.value

    override suspend fun add(item: InventoryItem) {
        items.value = items.value + item
    }

    override suspend fun exists(itemId: String): Boolean =
        items.value.any { it.itemId == itemId }
}

class FakeTransactionRepository : TransactionRepository {
    private val transactions = MutableStateFlow<List<Transaction>>(emptyList())

    override fun observeBalance(): Flow<Coin> =
        transactions.map { txns ->
            Coin(txns.sumOf { it.amount }.coerceAtLeast(0))
        }

    override fun observeTransactions(): Flow<List<Transaction>> = transactions

    override suspend fun getBalance(): Coin {
        val sum = transactions.value.sumOf { it.amount }
        return Coin(sum.coerceAtLeast(0))
    }

    override suspend fun record(transaction: Transaction) {
        transactions.value = transactions.value + transaction
    }

    fun seedBalance(amount: Long) {
        transactions.value = listOf(
            Transaction(
                id = "seed",
                type = com.focusclinic.domain.model.TransactionType.EarnFocus,
                amount = amount,
                referenceId = "seed",
                description = "Seed balance",
                createdAt = 0L,
            )
        )
    }
}

class FakeCustomRewardRepository : CustomRewardRepository {
    private val rewards = MutableStateFlow<Map<String, CustomReward>>(emptyMap())

    override fun observeActiveRewards(): Flow<List<CustomReward>> =
        rewards.map { map -> map.values.filter { it.isActive } }

    override suspend fun getById(id: String): CustomReward? = rewards.value[id]

    override suspend fun save(reward: CustomReward) {
        rewards.value = rewards.value + (reward.id to reward)
    }

    override suspend fun deactivate(id: String) {
        val reward = rewards.value[id] ?: return
        rewards.value = rewards.value + (id to reward.copy(isActive = false))
    }
}
