package com.focusclinic.app.presentation.screens.stats

import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.rule.PlayerLevel
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints

data class StatsState(
    val sessions: List<FocusSession> = emptyList(),
    val totalXp: ExperiencePoints = ExperiencePoints.ZERO,
    val balance: Coin = Coin.ZERO,
    val playerLevel: PlayerLevel = PlayerLevel.INTERN,
    val isLoading: Boolean = true,
) {
    val totalSessions: Int
        get() = sessions.size

    val completedSessions: Int
        get() = sessions.count {
            it.status == com.focusclinic.domain.model.SessionStatus.Completed
        }

    val totalFocusMinutes: Int
        get() = sessions.sumOf { it.actualFocusMinutes }

    val totalEarnedXp: Long
        get() = sessions.sumOf { it.earnedXp.value }

    val totalEarnedCoins: Long
        get() = sessions.sumOf { it.earnedCoins.amount }
}
