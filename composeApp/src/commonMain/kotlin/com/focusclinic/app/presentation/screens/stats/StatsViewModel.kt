package com.focusclinic.app.presentation.screens.stats

import com.focusclinic.domain.repository.FocusSessionRepository
import com.focusclinic.domain.usecase.GetUserStatsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatsViewModel(
    private val getUserStats: GetUserStatsUseCase,
    private val sessionRepository: FocusSessionRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(StatsState())
    val state: StateFlow<StatsState> = _state.asStateFlow()

    init {
        observeUserStats()
        observeSessionHistory()
    }

    private fun observeUserStats() {
        scope.launch {
            getUserStats().collect { stats ->
                _state.update {
                    it.copy(
                        totalXp = stats.profile.totalXp,
                        balance = stats.balance,
                        playerLevel = stats.profile.level,
                    )
                }
            }
        }
    }

    private fun observeSessionHistory() {
        scope.launch {
            sessionRepository.observeSessionHistory().collect { sessions ->
                _state.update { it.copy(sessions = sessions, isLoading = false) }
            }
        }
    }
}
