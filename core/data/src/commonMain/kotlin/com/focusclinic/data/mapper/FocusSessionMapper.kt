package com.focusclinic.data.mapper

import com.focusclinic.data.database.Focus_sessions
import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints
import com.focusclinic.domain.valueobject.FocusDuration

fun Focus_sessions.toDomain(): FocusSession = FocusSession(
    id = id,
    startTime = start_time,
    endTime = end_time,
    plannedDuration = FocusDuration(planned_duration_minutes.toInt()),
    actualFocusMinutes = actual_focus_minutes.toInt(),
    status = status.toSessionStatus(),
    earnedXp = ExperiencePoints(earned_xp),
    earnedCoins = Coin(earned_coins),
)
