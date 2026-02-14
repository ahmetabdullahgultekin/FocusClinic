package com.focusclinic.app.presentation.screens.goals

import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.model.WillpowerGoal

data class GoalsState(
    val goals: List<WillpowerGoal> = emptyList(),
    val recentCompletions: List<GoalCompletion> = emptyList(),
    val calendarYear: Int = 2026,
    val calendarMonth: Int = 1,
    val calendarCompletionCounts: Map<Int, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showCreateDialog: Boolean = false,
    val editingGoal: WillpowerGoal? = null,
)
