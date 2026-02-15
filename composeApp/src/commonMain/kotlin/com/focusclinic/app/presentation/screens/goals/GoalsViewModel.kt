package com.focusclinic.app.presentation.screens.goals

import com.focusclinic.app.platform.HapticFeedback
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.model.RecurrenceType
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.repository.WillpowerGoalRepository
import com.focusclinic.domain.rule.StreakRules
import com.focusclinic.domain.usecase.CalculateStreakUseCase
import com.focusclinic.domain.usecase.CompleteWillpowerGoalUseCase
import com.focusclinic.domain.usecase.CreateWillpowerGoalUseCase
import com.focusclinic.domain.usecase.DeactivateWillpowerGoalUseCase
import com.focusclinic.domain.usecase.IsGoalCompletableUseCase
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
    private val isGoalCompletable: IsGoalCompletableUseCase,
    private val calculateStreak: CalculateStreakUseCase,
    private val goalRepository: WillpowerGoalRepository,
    private val hapticFeedback: HapticFeedback,
    private val scope: CoroutineScope,
    private val clock: () -> Long,
) {
    private val _state = MutableStateFlow(GoalsState())
    val state: StateFlow<GoalsState> = _state.asStateFlow()

    init {
        initCalendar()
        observeGoals()
        observeCompletions()
    }

    private fun initCalendar() {
        val now = clock()
        val year = epochMillisToYear(now)
        val month = epochMillisToMonth(now)
        _state.update { it.copy(calendarYear = year, calendarMonth = month) }
    }

    fun onIntent(intent: GoalsIntent) {
        when (intent) {
            is GoalsIntent.CreateGoal -> createGoal(intent)
            is GoalsIntent.UpdateGoal -> updateGoal(intent)
            is GoalsIntent.CompleteGoal -> completeGoal(intent.goalId, intent.note)
            is GoalsIntent.DeactivateGoal -> deactivateGoal(intent.goalId)
            is GoalsIntent.StartEditing -> _state.update { it.copy(editingGoal = intent.goal) }
            is GoalsIntent.ShowCompleteDialog -> _state.update {
                it.copy(completingGoalId = intent.goalId)
            }
            is GoalsIntent.SelectDay -> selectDay(intent.day)
            is GoalsIntent.SelectCategory -> _state.update {
                it.copy(selectedCategory = intent.category)
            }
            GoalsIntent.ShowCreateDialog -> _state.update { it.copy(showCreateDialog = true) }
            GoalsIntent.DismissDialog -> _state.update {
                it.copy(showCreateDialog = false, editingGoal = null)
            }
            GoalsIntent.DismissCompleteDialog -> _state.update {
                it.copy(completingGoalId = null)
            }
            GoalsIntent.DismissCelebration -> _state.update {
                it.copy(showCelebration = false)
            }
            GoalsIntent.DismissDayDetail -> _state.update {
                it.copy(selectedDay = null, selectedDayCompletions = emptyList())
            }
            GoalsIntent.PreviousMonth -> navigateMonth(-1)
            GoalsIntent.NextMonth -> navigateMonth(1)
            GoalsIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
            GoalsIntent.DismissSuccess -> _state.update { it.copy(successMessage = null) }
        }
    }

    private fun observeGoals() {
        scope.launch {
            goalRepository.observeActiveGoals().collect { goals ->
                val categories = goals.map { it.category }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()

                _state.update {
                    it.copy(goals = goals, isLoading = false, availableCategories = categories)
                }

                refreshCompletability(goals)
            }
        }
    }

    private fun observeCompletions() {
        scope.launch {
            goalRepository.observeAllCompletions().collect { completions ->
                val counts = computeCalendarCounts(
                    completions, _state.value.calendarYear, _state.value.calendarMonth,
                )
                _state.update {
                    it.copy(recentCompletions = completions, calendarCompletionCounts = counts)
                }

                refreshStreak()
                refreshCompletability(_state.value.goals)
            }
        }
    }

    private fun refreshStreak() {
        scope.launch {
            val streakInfo = calculateStreak()
            val multiplier = StreakRules.multiplierForStreak(streakInfo.current)
            _state.update {
                it.copy(
                    currentStreak = streakInfo.current,
                    bestStreak = streakInfo.best,
                    streakMultiplier = multiplier,
                )
            }
        }
    }

    private fun refreshCompletability(goals: List<WillpowerGoal>) {
        scope.launch {
            val completableIds = mutableSetOf<String>()
            for (goal in goals) {
                if (isGoalCompletable(goal)) {
                    completableIds.add(goal.id)
                }
            }
            _state.update { it.copy(completableGoalIds = completableIds) }
        }
    }

    private fun navigateMonth(delta: Int) {
        val current = _state.value
        var newMonth = current.calendarMonth + delta
        var newYear = current.calendarYear
        if (newMonth < 1) { newMonth = 12; newYear-- }
        if (newMonth > 12) { newMonth = 1; newYear++ }
        val counts = computeCalendarCounts(current.recentCompletions, newYear, newMonth)
        _state.update {
            it.copy(calendarYear = newYear, calendarMonth = newMonth, calendarCompletionCounts = counts)
        }
    }

    private fun createGoal(intent: GoalsIntent.CreateGoal) {
        if (_state.value.isProcessing) return
        _state.update { it.copy(isProcessing = true) }

        scope.launch {
            try {
                when (val result = createWillpowerGoal(
                    title = intent.title,
                    description = intent.description,
                    coinReward = intent.coinReward,
                    xpReward = intent.xpReward,
                    recurrenceType = intent.recurrenceType,
                    category = intent.category,
                )) {
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

    private fun updateGoal(intent: GoalsIntent.UpdateGoal) {
        if (_state.value.isProcessing) return
        _state.update { it.copy(isProcessing = true) }

        scope.launch {
            try {
                when (val result = updateWillpowerGoal(
                    goalId = intent.goalId,
                    title = intent.title,
                    description = intent.description,
                    coinReward = intent.coinReward,
                    xpReward = intent.xpReward,
                    recurrenceType = intent.recurrenceType,
                    category = intent.category,
                )) {
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
                is DomainResult.Success -> {
                    hapticFeedback.success()
                    _state.update {
                        it.copy(
                            completingGoalId = null,
                            showCelebration = true,
                            successMessage = "goal_completed",
                        )
                    }
                }
                is DomainResult.Failure -> {
                    hapticFeedback.error()
                    _state.update {
                        it.copy(
                            completingGoalId = null,
                            errorMessage = result.error.message,
                        )
                    }
                }
            }
        }
    }

    private fun selectDay(day: Int) {
        val current = _state.value
        if (current.selectedDay == day) {
            _state.update { it.copy(selectedDay = null, selectedDayCompletions = emptyList()) }
            return
        }

        val dayCompletions = current.recentCompletions.filter { completion ->
            val cYear = epochMillisToYear(completion.completedAt)
            val cMonth = epochMillisToMonth(completion.completedAt)
            val cDay = epochMillisToDay(completion.completedAt)
            cYear == current.calendarYear && cMonth == current.calendarMonth && cDay == day
        }

        val details = dayCompletions.map { completion ->
            val goalTitle = current.goals.find { it.id == completion.goalId }?.title
                ?: resolveGoalTitle(completion.goalId)
            GoalCompletionDetail(goalTitle = goalTitle, completion = completion)
        }

        _state.update {
            it.copy(selectedDay = day, selectedDayCompletions = details)
        }
    }

    private fun resolveGoalTitle(goalId: String): String {
        // Fallback — if the goal isn't in active goals, return a generic label
        return "Goal"
    }

    private fun deactivateGoal(goalId: String) {
        scope.launch {
            deactivateWillpowerGoal(goalId)
        }
    }
}

private fun computeCalendarCounts(
    completions: List<GoalCompletion>,
    year: Int,
    month: Int,
): Map<Int, Int> {
    val counts = mutableMapOf<Int, Int>()
    for (completion in completions) {
        val cYear = epochMillisToYear(completion.completedAt)
        val cMonth = epochMillisToMonth(completion.completedAt)
        if (cYear == year && cMonth == month) {
            val day = epochMillisToDay(completion.completedAt)
            counts[day] = (counts[day] ?: 0) + 1
        }
    }
    return counts
}

private const val MILLIS_PER_DAY = 86_400_000L

private fun epochMillisToYear(millis: Long): Int {
    var days = (millis / MILLIS_PER_DAY).toInt()
    var year = 1970
    while (true) {
        val daysInYear = if (isLeapYear(year)) 366 else 365
        if (days < daysInYear) break
        days -= daysInYear
        year++
    }
    return year
}

private fun epochMillisToMonth(millis: Long): Int {
    var days = (millis / MILLIS_PER_DAY).toInt()
    var year = 1970
    while (true) {
        val daysInYear = if (isLeapYear(year)) 366 else 365
        if (days < daysInYear) break
        days -= daysInYear
        year++
    }
    val monthDays = monthLengths(year)
    var month = 1
    for (ml in monthDays) {
        if (days < ml) break
        days -= ml
        month++
    }
    return month
}

private fun epochMillisToDay(millis: Long): Int {
    var days = (millis / MILLIS_PER_DAY).toInt()
    var year = 1970
    while (true) {
        val daysInYear = if (isLeapYear(year)) 366 else 365
        if (days < daysInYear) break
        days -= daysInYear
        year++
    }
    val monthDays = monthLengths(year)
    for (ml in monthDays) {
        if (days < ml) break
        days -= ml
    }
    return days + 1
}

private fun isLeapYear(year: Int): Boolean =
    year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

private fun monthLengths(year: Int): IntArray = intArrayOf(
    31, if (isLeapYear(year)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31,
)
