package com.focusclinic.app.presentation.screens.goals

import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.GoalCompletion
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
            GoalsIntent.PreviousMonth -> navigateMonth(-1)
            GoalsIntent.NextMonth -> navigateMonth(1)
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
                val counts = computeCalendarCounts(
                    completions, _state.value.calendarYear, _state.value.calendarMonth,
                )
                _state.update {
                    it.copy(recentCompletions = completions, calendarCompletionCounts = counts)
                }
            }
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
