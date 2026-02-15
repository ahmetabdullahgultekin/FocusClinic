package com.focusclinic.app.presentation.screens.focus

import com.focusclinic.app.platform.HapticFeedback
import com.focusclinic.app.platform.TimerNotification
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.model.SessionStatus
import com.focusclinic.domain.repository.WillpowerGoalRepository
import com.focusclinic.domain.rule.FocusRules
import com.focusclinic.domain.usecase.CompleteFocusSessionUseCase
import com.focusclinic.domain.usecase.CompleteWillpowerGoalUseCase
import com.focusclinic.domain.usecase.GetUserStatsUseCase
import com.focusclinic.domain.usecase.InterruptFocusSessionUseCase
import com.focusclinic.domain.usecase.StartFocusSessionUseCase
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import com.focusclinic.domain.valueobject.FocusDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusViewModel(
    private val startFocusSession: StartFocusSessionUseCase,
    private val completeFocusSession: CompleteFocusSessionUseCase,
    private val interruptFocusSession: InterruptFocusSessionUseCase,
    private val getUserStats: GetUserStatsUseCase,
    private val goalRepository: WillpowerGoalRepository,
    private val completeWillpowerGoal: CompleteWillpowerGoalUseCase,
    private val timerNotification: TimerNotification,
    private val hapticFeedback: HapticFeedback,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(FocusState())
    val state: StateFlow<FocusState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var graceJob: Job? = null
    private var activeSessionId: String? = null

    init {
        observeUserStats()
        observeActiveGoals()
    }

    fun onIntent(intent: FocusIntent) {
        when (intent) {
            is FocusIntent.SelectDuration -> selectDuration(intent.duration)
            is FocusIntent.QuickCompleteGoal -> quickCompleteGoal(intent.goalId)
            FocusIntent.StartSession -> startSession()
            FocusIntent.CancelSession -> cancelSession()
            FocusIntent.DismissResult -> dismissResult()
            FocusIntent.DismissCelebration -> _state.update { it.copy(showCelebration = false) }
            FocusIntent.AppBackgrounded -> onAppBackgrounded()
            FocusIntent.AppResumed -> onAppResumed()
            FocusIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeUserStats() {
        scope.launch {
            getUserStats().collect { stats ->
                _state.update {
                    it.copy(
                        balance = stats.balance,
                        xpMultiplier = stats.xpMultiplier,
                        coinMultiplier = stats.coinMultiplier,
                    )
                }
            }
        }
    }

    private fun selectDuration(duration: FocusDuration) {
        if (_state.value.phase != FocusPhase.Idle) return
        _state.update { it.copy(selectedDuration = duration) }
    }

    private fun startSession() {
        if (_state.value.phase != FocusPhase.Idle) return

        val duration = _state.value.selectedDuration
        val totalSeconds = duration.minutes * SECONDS_PER_MINUTE

        scope.launch {
            when (val result = startFocusSession(duration)) {
                is DomainResult.Success -> {
                    hapticFeedback.medium()
                    activeSessionId = result.data.id
                    _state.update {
                        it.copy(
                            phase = FocusPhase.Focusing,
                            remainingSeconds = totalSeconds,
                            totalSeconds = totalSeconds,
                        )
                    }
                    timerNotification.onSessionStarted(duration.minutes)
                    startTimer()
                }
                is DomainResult.Failure -> {
                    _state.update { it.copy(errorMessage = result.error.message) }
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            while (_state.value.remainingSeconds > 0) {
                delay(TICK_INTERVAL_MS)
                _state.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            onTimerFinished()
        }
    }

    private suspend fun onTimerFinished() {
        val sessionId = activeSessionId ?: return
        when (val result = completeFocusSession(sessionId)) {
            is DomainResult.Success -> showResult(result.data, wasInterrupted = false)
            is DomainResult.Failure -> resetToIdle()
        }
    }

    private fun cancelSession() {
        if (_state.value.phase != FocusPhase.Focusing) return
        timerJob?.cancel()
        graceJob?.cancel()
        timerNotification.onSessionStopped()

        val sessionId = activeSessionId ?: return
        val elapsedMinutes = elapsedMinutes()

        scope.launch {
            interruptFocusSession(sessionId, elapsedMinutes)
            resetToIdle()
        }
    }

    private fun onAppBackgrounded() {
        if (_state.value.phase != FocusPhase.Focusing) return

        graceJob?.cancel()
        graceJob = scope.launch {
            delay(FocusRules.INTERRUPTION_GRACE_PERIOD_SECONDS * MILLIS_PER_SECOND)
            interruptSession()
        }
    }

    private fun onAppResumed() {
        graceJob?.cancel()
        graceJob = null
    }

    private suspend fun interruptSession() {
        timerJob?.cancel()
        val sessionId = activeSessionId ?: return
        val elapsedMinutes = elapsedMinutes()

        when (val result = interruptFocusSession(sessionId, elapsedMinutes)) {
            is DomainResult.Success -> showResult(result.data, wasInterrupted = true)
            is DomainResult.Failure -> resetToIdle()
        }
    }

    private fun showResult(session: FocusSession, wasInterrupted: Boolean) {
        val phase = if (wasInterrupted) FocusPhase.Interrupted else FocusPhase.Completed

        if (wasInterrupted) {
            timerNotification.onSessionStopped()
            hapticFeedback.error()
        } else {
            timerNotification.onSessionCompleted(
                earnedXp = session.earnedXp.value,
                earnedCoins = session.earnedCoins.amount,
            )
            hapticFeedback.success()
        }

        _state.update {
            it.copy(
                phase = phase,
                remainingSeconds = 0,
                showCelebration = !wasInterrupted,
                sessionResult = SessionResult(
                    earnedXp = session.earnedXp,
                    earnedCoins = session.earnedCoins,
                    wasInterrupted = wasInterrupted,
                ),
            )
        }
        activeSessionId = null
    }

    private fun dismissResult() {
        resetToIdle()
    }

    private fun resetToIdle() {
        timerJob?.cancel()
        graceJob?.cancel()
        activeSessionId = null
        _state.update {
            it.copy(
                phase = FocusPhase.Idle,
                remainingSeconds = 0,
                totalSeconds = 0,
                sessionResult = null,
            )
        }
    }

    private fun observeActiveGoals() {
        scope.launch {
            goalRepository.observeActiveGoals().collect { goals ->
                _state.update { it.copy(activeGoals = goals.take(MAX_DISPLAYED_GOALS)) }
            }
        }
    }

    private fun quickCompleteGoal(goalId: String) {
        scope.launch {
            when (completeWillpowerGoal(goalId, "")) {
                is DomainResult.Success -> hapticFeedback.success()
                is DomainResult.Failure -> hapticFeedback.error()
            }
        }
    }

    private fun elapsedMinutes(): Int {
        val current = _state.value
        val elapsedSeconds = current.totalSeconds - current.remainingSeconds
        return elapsedSeconds / SECONDS_PER_MINUTE
    }

    companion object {
        private const val TICK_INTERVAL_MS = 1000L
        private const val SECONDS_PER_MINUTE = 60
        private const val MILLIS_PER_SECOND = 1000L
        private const val MAX_DISPLAYED_GOALS = 5
    }
}
