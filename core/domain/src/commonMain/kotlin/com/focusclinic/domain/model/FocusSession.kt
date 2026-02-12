package com.focusclinic.domain.model

import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import com.focusclinic.domain.valueobject.FocusDuration

data class FocusSession(
    val id: String,
    val startTime: Long,
    val endTime: Long?,
    val plannedDuration: FocusDuration,
    val actualFocusMinutes: Int,
    val status: SessionStatus,
    val earnedXp: ExperiencePoints,
    val earnedCoins: Coin,
) {
    companion object {
        fun create(
            id: String,
            startTime: Long,
            plannedDuration: FocusDuration,
        ): FocusSession = FocusSession(
            id = id,
            startTime = startTime,
            endTime = null,
            plannedDuration = plannedDuration,
            actualFocusMinutes = 0,
            status = SessionStatus.Focusing,
            earnedXp = ExperiencePoints.ZERO,
            earnedCoins = Coin.ZERO,
        )
    }
}
