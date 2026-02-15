package com.focusclinic.app.presentation.screens.focus

import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import com.focusclinic.domain.valueobject.FocusDuration
import com.focusclinic.domain.valueobject.Multiplier

data class FocusState(
    val phase: FocusPhase = FocusPhase.Idle,
    val selectedDuration: FocusDuration = FocusDuration.ALLOWED_DURATIONS[3],
    val availableDurations: List<FocusDuration> = FocusDuration.ALLOWED_DURATIONS,
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val balance: Coin = Coin.ZERO,
    val xpMultiplier: Multiplier = Multiplier.BASE,
    val coinMultiplier: Multiplier = Multiplier.BASE,
    val sessionResult: SessionResult? = null,
    val showCelebration: Boolean = false,
    val errorMessage: String? = null,
    val activeGoals: List<WillpowerGoal> = emptyList(),
) {
    val progress: Float
        get() = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f

    val timerText: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
}

sealed interface FocusPhase {
    data object Idle : FocusPhase
    data object Focusing : FocusPhase
    data object Completed : FocusPhase
    data object Interrupted : FocusPhase
}

data class SessionResult(
    val earnedXp: ExperiencePoints,
    val earnedCoins: Coin,
    val wasInterrupted: Boolean,
)
