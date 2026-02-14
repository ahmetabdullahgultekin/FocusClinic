package com.focusclinic.app.presentation.screens.goals

import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.repository.WillpowerGoalRepository
import com.focusclinic.domain.usecase.CompleteWillpowerGoalUseCase
import com.focusclinic.domain.usecase.CreateWillpowerGoalUseCase
import com.focusclinic.domain.usecase.DeactivateWillpowerGoalUseCase
import com.focusclinic.domain.usecase.UpdateWillpowerGoalUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val createWillpowerGoal: CreateWillpowerGoalUseCase,
    private val completeWillpowerGoal: CompleteWillpowerGoalUseCase,
    private val updateWillpowerGoal: UpdateWillpowerGoalUseCase,
    private val deactivateWillpowerGoal: DeactivateWillpowerGoalUseCase,
    private val goalRepository: WillpowerGoalRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(GoalsState())
    val state: StateFlow<GoalsState> = _state.asStateFlow()

    init {
        observeGoals()
        observeCompletions()
    }

    fun onIntent(intent: GoalsIntent) {
        when (intent) {
            is GoalsIntent.CreateGoal -> createGoal(
                intent.title, intent.description, intent.coinReward, intent.xpReward,
            )
            is GoalsIntent.UpdateGoal -> updateGoal(
                intent.goalId, intent.title, intent.description, intent.coinReward, intent.xpReward,
            )
            is GoalsIntent.CompleteGoal -> completeGoal(intent.goalId, intent.note)
            is GoalsIntent.DeactivateGoal -> deactivateGoal(intent.goalId)
            is GoalsIntent.StartEditing -> _state.update { it.copy(editingGoal = intent.goal) }
            GoalsIntent.ShowCreateDialog -> _state.update { it.copy(showCreateDialog = true) }
            GoalsIntent.DismissDialog -> _state.update {
                it.copy(showCreateDialog = false, editingGoal = null)
            }
            GoalsIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
            GoalsIntent.DismissSuccess -> _state.update { it.copy(successMessage = null) }
        }
    }

    private fun observeGoals() {
        scope.launch {
            goalRepository.observeActiveGoals().collect { goals ->
                _state.update { it.copy(goals = goals, isLoading = false) }
            }
        }
    }

    private fun observeCompletions() {
        scope.launch {
            goalRepository.observeAllCompletions().collect { completions ->
                _state.update { it.copy(recentCompletions = completions) }
            }
        }
    }

    private fun createGoal(title: String, description: String, coinReward: Long, xpReward: Long) {
        if (_state.value.isProcessing) return
        _state.update { it.copy(isProcessing = true) }

        scope.launch {
            try {
                when (val result = createWillpowerGoal(title, description, coinReward, xpReward)) {
                    is DomainResult.Success -> _state.update {
                        it.copy(showCreateDialog = false, successMessage = result.data.title)
                    }
                    is DomainResult.Failure -> _state.update {
                        it.copy(errorMessage = result.error.message)
                    }
                }
            } finally {
                _state.update { it.copy(isProcessing = false) }
            }
        }
    }

    private fun updateGoal(
        goalId: String, title: String, description: String, coinReward: Long, xpReward: Long,
    ) {
        if (_state.value.isProcessing) return
        _state.update { it.copy(isProcessing = true) }

        scope.launch {
            try {
                when (val result = updateWillpowerGoal(goalId, title, description, coinReward, xpReward)) {
                    is DomainResult.Success -> _state.update {
                        it.copy(editingGoal = null, successMessage = result.data.title)
                    }
                    is DomainResult.Failure -> _state.update {
                        it.copy(errorMessage = result.error.message)
                    }
                }
            } finally {
                _state.update { it.copy(isProcessing = false) }
            }
        }
    }

    private fun completeGoal(goalId: String, note: String) {
        scope.launch {
            when (val result = completeWillpowerGoal(goalId, note)) {
                is DomainResult.Success -> _state.update {
                    it.copy(successMessage = "goal_completed")
                }
                is DomainResult.Failure -> _state.update {
                    it.copy(errorMessage = result.error.message)
                }
            }
        }
    }

    private fun deactivateGoal(goalId: String) {
        scope.launch {
            deactivateWillpowerGoal(goalId)
        }
    }
}
