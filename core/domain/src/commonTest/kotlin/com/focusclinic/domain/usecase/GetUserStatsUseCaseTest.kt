package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.InventoryItem
import com.focusclinic.domain.model.ItemType
import com.focusclinic.domain.model.ModifierType
import com.focusclinic.domain.test.FakeInventoryRepository
import com.focusclinic.domain.test.FakeTransactionRepository
import com.focusclinic.domain.test.FakeUserProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserStatsUseCaseTest {

    private val userProfileRepo = FakeUserProfileRepository()
    private val transactionRepo = FakeTransactionRepository()
    private val inventoryRepo = FakeInventoryRepository()

    private val useCase = GetUserStatsUseCase(
        userProfileRepository = userProfileRepo,
        transactionRepository = transactionRepo,
        inventoryRepository = inventoryRepo,
    )

    @Test
    fun invoke_ShouldCombineProfileBalanceAndMultipliers() = runTest {
        transactionRepo.seedBalance(500)
        val stats = useCase().first()
        assertEquals(0, stats.profile.totalXp.value)
        assertEquals(500, stats.balance.amount)
    }

    @Test
    fun invoke_WhenEmptyInventory_ShouldReturnBaseMultipliers() = runTest {
        val stats = useCase().first()
        assertEquals(1.0, stats.xpMultiplier.value)
        assertEquals(1.0, stats.coinMultiplier.value)
    }

    @Test
    fun invoke_WhenInventoryHasXpBonus_ShouldComputeXpMultiplier() = runTest {
        inventoryRepo.setItems(
            listOf(
                InventoryItem(
                    itemId = "chair",
                    name = "Chair",
                    type = ItemType.EQUIPMENT,
                    modifier = ModifierType.XpBonus(0.10),
                    purchasedAt = 0L,
                )
            )
        )
        val stats = useCase().first()
        assertEquals(1.1, stats.xpMultiplier.value)
        assertEquals(1.0, stats.coinMultiplier.value)
    }

    @Test
    fun invoke_WhenInventoryHasCoinBonus_ShouldComputeCoinMultiplier() = runTest {
        inventoryRepo.setItems(
            listOf(
                InventoryItem(
                    itemId = "lamp",
                    name = "Lamp",
                    type = ItemType.EQUIPMENT,
                    modifier = ModifierType.CoinBonus(0.05),
                    purchasedAt = 0L,
                )
            )
        )
        val stats = useCase().first()
        assertEquals(1.0, stats.xpMultiplier.value)
        assertEquals(1.05, stats.coinMultiplier.value)
    }
}
