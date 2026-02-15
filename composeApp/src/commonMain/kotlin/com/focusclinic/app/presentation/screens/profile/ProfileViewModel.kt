package com.focusclinic.app.presentation.screens.profile

import com.focusclinic.domain.model.ItemType
import com.focusclinic.domain.repository.InventoryRepository
import com.focusclinic.domain.usecase.GetUserStatsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserStats: GetUserStatsUseCase,
    private val inventoryRepository: InventoryRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        observeUserStats()
        observeInventory()
    }

    private fun observeUserStats() {
        scope.launch {
            getUserStats().collect { stats ->
                _state.update {
                    it.copy(
                        playerLevel = stats.profile.level,
                        totalXp = stats.profile.totalXp,
                        xpMultiplier = stats.xpMultiplier,
                        coinMultiplier = stats.coinMultiplier,
                    )
                }
            }
        }
    }

    private fun observeInventory() {
        scope.launch {
            inventoryRepository.observeInventory().collect { items ->
                _state.update {
                    it.copy(
                        equipment = items.filter { item -> item.type == ItemType.EQUIPMENT },
                        decorations = items.filter { item -> item.type == ItemType.DECORATION },
                    )
                }
            }
        }
    }
}
