package com.focusclinic.app.presentation.screens.shop

import com.focusclinic.app.presentation.FakeCustomRewardRepository
import com.focusclinic.app.presentation.FakeInventoryRepository
import com.focusclinic.app.presentation.FakeTransactionRepository
import com.focusclinic.domain.model.ShopCatalog
import com.focusclinic.domain.usecase.DeactivateCustomRewardUseCase
import com.focusclinic.domain.usecase.PurchaseCustomRewardUseCase
import com.focusclinic.domain.usecase.PurchaseShopItemUseCase
import com.focusclinic.domain.usecase.SaveCustomRewardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val inventoryRepo = FakeInventoryRepository()
    private val transactionRepo = FakeTransactionRepository()
    private val rewardRepo = FakeCustomRewardRepository()

    private var idCounter = 0
    private val idGenerator: () -> String = { "test-id-${idCounter++}" }
    private val clock: () -> Long = { 1000L }

    private lateinit var viewModel: ShopViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = ShopViewModel(
            purchaseShopItem = PurchaseShopItemUseCase(inventoryRepo, transactionRepo, idGenerator, clock),
            purchaseCustomReward = PurchaseCustomRewardUseCase(rewardRepo, transactionRepo, idGenerator, clock),
            saveCustomReward = SaveCustomRewardUseCase(rewardRepo, idGenerator, clock),
            deactivateCustomReward = DeactivateCustomRewardUseCase(rewardRepo),
            inventoryRepository = inventoryRepo,
            customRewardRepository = rewardRepo,
            transactionRepository = transactionRepo,
            scope = testScope,
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_ShouldHaveShopCatalogItems() {
        assertEquals(ShopCatalog.items.size, viewModel.state.value.shopItems.size)
    }

    @Test
    fun purchaseItem_WhenSufficientBalance_ShouldSucceed() = testScope.runTest {
        transactionRepo.seedBalance(1000)
        advanceUntilIdle()

        val item = ShopCatalog.items.first()
        viewModel.onIntent(ShopIntent.PurchaseItem(item))
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.purchaseSuccessMessage)
        assertTrue(viewModel.state.value.ownedItemIds.contains(item.id))
    }

    @Test
    fun purchaseItem_WhenInsufficientBalance_ShouldShowError() = testScope.runTest {
        advanceUntilIdle()

        val item = ShopCatalog.items.first()
        viewModel.onIntent(ShopIntent.PurchaseItem(item))
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun createReward_ShouldAddToRewardsList() = testScope.runTest {
        advanceUntilIdle()

        viewModel.onIntent(ShopIntent.CreateReward("Coffee break", 100))
        advanceUntilIdle()

        val rewards = viewModel.state.value.customRewards
        assertEquals(1, rewards.size)
        assertEquals("Coffee break", rewards[0].title)
    }

    @Test
    fun createReward_WithBlankTitle_ShouldBeIgnored() = testScope.runTest {
        advanceUntilIdle()

        viewModel.onIntent(ShopIntent.CreateReward("   ", 100))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.customRewards.isEmpty())
    }

    @Test
    fun createReward_WithZeroCost_ShouldBeIgnored() = testScope.runTest {
        advanceUntilIdle()

        viewModel.onIntent(ShopIntent.CreateReward("Coffee", 0))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.customRewards.isEmpty())
    }

    @Test
    fun deleteReward_ShouldRemoveFromActiveList() = testScope.runTest {
        advanceUntilIdle()

        viewModel.onIntent(ShopIntent.CreateReward("Coffee break", 100))
        advanceUntilIdle()

        val rewardId = viewModel.state.value.customRewards[0].id
        viewModel.onIntent(ShopIntent.DeleteReward(rewardId))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.customRewards.isEmpty())
    }

    @Test
    fun dismissError_ShouldClearErrorMessage() = testScope.runTest {
        advanceUntilIdle()

        viewModel.onIntent(ShopIntent.PurchaseItem(ShopCatalog.items.first()))
        advanceUntilIdle()

        viewModel.onIntent(ShopIntent.DismissError)
        advanceUntilIdle()

        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun selectTab_ShouldUpdateSelectedTab() = testScope.runTest {
        advanceUntilIdle()

        viewModel.onIntent(ShopIntent.SelectTab(ShopTab.CUSTOM_REWARDS))
        advanceUntilIdle()

        assertEquals(ShopTab.CUSTOM_REWARDS, viewModel.state.value.selectedTab)
    }
}
