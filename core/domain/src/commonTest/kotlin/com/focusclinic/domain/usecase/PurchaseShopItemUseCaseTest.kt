package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.ItemType
import com.focusclinic.domain.model.ModifierType
import com.focusclinic.domain.model.ShopItem
import com.focusclinic.domain.test.FakeInventoryRepository
import com.focusclinic.domain.test.FakeTransactionRepository
import com.focusclinic.domain.valueobject.Coin
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PurchaseShopItemUseCaseTest {

    private val inventoryRepo = FakeInventoryRepository()
    private val transactionRepo = FakeTransactionRepository()
    private var idCounter = 0

    private val useCase = PurchaseShopItemUseCase(
        inventoryRepository = inventoryRepo,
        transactionRepository = transactionRepo,
        idGenerator = { "tx-${++idCounter}" },
        clock = { 3000L },
    )

    private val testItem = ShopItem(
        id = "ergonomic_chair",
        name = "Ergonomic Chair",
        type = ItemType.EQUIPMENT,
        modifier = ModifierType.XpBonus(0.10),
        cost = Coin(500),
    )

    @Test
    fun invoke_WhenItemAlreadyOwned_ShouldReturnItemAlreadyOwned() = runTest {
        transactionRepo.seedBalance(1000)
        // First purchase succeeds
        useCase(testItem)
        // Second purchase should fail
        val result = useCase(testItem)
        assertIs<DomainResult.Failure>(result)
        assertIs<DomainError.PurchaseError.ItemAlreadyOwned>(result.error)
    }

    @Test
    fun invoke_WhenInsufficientCoins_ShouldReturnInsufficientCoins() = runTest {
        transactionRepo.seedBalance(100) // Only 100 coins, need 500
        val result = useCase(testItem)
        assertIs<DomainResult.Failure>(result)
        val error = result.error as DomainError.PurchaseError.InsufficientCoins
        assertEquals(500, error.required)
        assertEquals(100, error.available)
    }

    @Test
    fun invoke_WhenSufficientCoins_ShouldReturnSuccess() = runTest {
        transactionRepo.seedBalance(500)
        val result = useCase(testItem)
        assertIs<DomainResult.Success<*>>(result)
    }

    @Test
    fun invoke_WhenSuccess_ShouldAddToInventory() = runTest {
        transactionRepo.seedBalance(500)
        useCase(testItem)
        assertTrue(inventoryRepo.exists("ergonomic_chair"))
    }

    @Test
    fun invoke_WhenSuccess_ShouldRecordNegativeTransaction() = runTest {
        transactionRepo.seedBalance(1000)
        useCase(testItem)
        // 1000 seed + (-500) spend = 500 remaining
        assertEquals(500, transactionRepo.getBalance().amount)
    }

    @Test
    fun invoke_WhenSuccess_ShouldReturnInventoryItem() = runTest {
        transactionRepo.seedBalance(500)
        val result = useCase(testItem) as DomainResult.Success
        assertEquals("ergonomic_chair", result.data.itemId)
        assertEquals("Ergonomic Chair", result.data.name)
        assertEquals(ItemType.EQUIPMENT, result.data.type)
        assertEquals(3000L, result.data.purchasedAt)
    }

    @Test
    fun invoke_WhenExactBalance_ShouldSucceed() = runTest {
        transactionRepo.seedBalance(500)
        val result = useCase(testItem)
        assertIs<DomainResult.Success<*>>(result)
    }
}
