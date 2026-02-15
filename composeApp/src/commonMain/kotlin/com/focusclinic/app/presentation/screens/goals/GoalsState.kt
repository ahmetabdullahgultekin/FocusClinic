package com.focusclinic.app.presentation.screens.goals

import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.model.WillpowerGoal

data class GoalCompletionDetail(
    val goalTitle: String,
    val completion: GoalCompletion,
)

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
    val showCelebration: Boolean = false,
    val completingGoalId: String? = null,
    val selectedDay: Int? = null,
    val selectedDayCompletions: List<GoalCompletionDetail> = emptyList(),
    val selectedCategory: String? = null,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val streakMultiplier: Double = 1.0,
    val completableGoalIds: Set<String> = emptySet(),
    val availableCategories: List<String> = emptyList(),
)
