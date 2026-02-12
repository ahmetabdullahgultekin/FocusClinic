package com.focusclinic.app.presentation.screens.shop

import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.ShopCatalog
import com.focusclinic.domain.model.ShopItem
import com.focusclinic.domain.usecase.DeactivateCustomRewardUseCase
import com.focusclinic.domain.usecase.PurchaseCustomRewardUseCase
import com.focusclinic.domain.usecase.PurchaseShopItemUseCase
import com.focusclinic.domain.usecase.SaveCustomRewardUseCase
import com.focusclinic.domain.repository.CustomRewardRepository
import com.focusclinic.domain.repository.InventoryRepository
import com.focusclinic.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShopViewModel(
    private val purchaseShopItem: PurchaseShopItemUseCase,
    private val purchaseCustomReward: PurchaseCustomRewardUseCase,
    private val saveCustomReward: SaveCustomRewardUseCase,
    private val deactivateCustomReward: DeactivateCustomRewardUseCase,
    private val inventoryRepository: InventoryRepository,
    private val customRewardRepository: CustomRewardRepository,
    private val transactionRepository: TransactionRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(ShopState(shopItems = ShopCatalog.items))
    val state: StateFlow<ShopState> = _state.asStateFlow()

    init {
        observeBalance()
        observeInventory()
        observeRewards()
    }

    fun onIntent(intent: ShopIntent) {
        when (intent) {
            is ShopIntent.PurchaseItem -> purchaseItem(intent.item)
            is ShopIntent.PurchaseReward -> purchaseReward(intent.rewardId)
            is ShopIntent.CreateReward -> createReward(intent.title, intent.cost)
            is ShopIntent.DeleteReward -> deleteReward(intent.rewardId)
            ShopIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
            ShopIntent.DismissPurchaseSuccess -> _state.update { it.copy(purchaseSuccessMessage = null) }
            is ShopIntent.SelectTab -> _state.update { it.copy(selectedTab = intent.tab) }
        }
    }

    private fun observeBalance() {
        scope.launch {
            transactionRepository.observeBalance().collect { balance ->
                _state.update { it.copy(balance = balance) }
            }
        }
    }

    private fun observeInventory() {
        scope.launch {
            inventoryRepository.observeInventory().collect { items ->
                _state.update { it.copy(ownedItemIds = items.map { item -> item.itemId }.toSet()) }
            }
        }
    }

    private fun observeRewards() {
        scope.launch {
            customRewardRepository.observeActiveRewards().collect { rewards ->
                _state.update { it.copy(customRewards = rewards) }
            }
        }
    }

    private fun purchaseItem(item: ShopItem) {
        if (_state.value.isProcessing) return
        _state.update { it.copy(isProcessing = true) }

        scope.launch {
            try {
                when (val result = purchaseShopItem(item)) {
                    is DomainResult.Success -> {
                        _state.update { it.copy(purchaseSuccessMessage = item.name) }
                    }
                    is DomainResult.Failure -> {
                        _state.update { it.copy(errorMessage = result.error.message) }
                    }
                }
            } finally {
                _state.update { it.copy(isProcessing = false) }
            }
        }
    }

    private fun purchaseReward(rewardId: String) {
        if (_state.value.isProcessing) return
        _state.update { it.copy(isProcessing = true) }

        scope.launch {
            try {
                val rewardName = _state.value.customRewards.find { it.id == rewardId }?.title
                when (val result = purchaseCustomReward(rewardId)) {
                    is DomainResult.Success -> {
                        _state.update { it.copy(purchaseSuccessMessage = rewardName) }
                    }
                    is DomainResult.Failure -> {
                        _state.update { it.copy(errorMessage = result.error.message) }
                    }
                }
            } finally {
                _state.update { it.copy(isProcessing = false) }
            }
        }
    }

    private fun createReward(title: String, cost: Long) {
        if (title.isBlank() || cost <= 0) return
        scope.launch {
            saveCustomReward(title.trim(), cost)
        }
    }

    private fun deleteReward(rewardId: String) {
        scope.launch {
            deactivateCustomReward(rewardId)
        }
    }
}
