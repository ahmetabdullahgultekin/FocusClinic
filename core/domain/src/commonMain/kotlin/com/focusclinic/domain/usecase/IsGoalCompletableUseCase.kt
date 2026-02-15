package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.RecurrenceType
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.repository.WillpowerGoalRepository

class IsGoalCompletableUseCase(
    private val goalRepository: WillpowerGoalRepository,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(goal: WillpowerGoal): Boolean {
        return when (goal.recurrenceType) {
            RecurrenceType.None -> true
            RecurrenceType.Daily -> {
                val todayStart = startOfDay(clock())
                val todayEnd = todayStart + MILLIS_PER_DAY
                val completions = goalRepository.getCompletionsInRange(goal.id, todayStart, todayEnd)
                completions.isEmpty()
            }
            RecurrenceType.Weekly -> {
                val weekStart = startOfWeek(clock())
                val weekEnd = weekStart + MILLIS_PER_WEEK
                val completions = goalRepository.getCompletionsInRange(goal.id, weekStart, weekEnd)
                completions.isEmpty()
            }
        }
    }
}

private const val MILLIS_PER_DAY = 86_400_000L
private const val MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY

/**
 * Returns the start of the day (00:00:00 UTC) for the given epoch millis.
 */
internal fun startOfDay(epochMillis: Long): Long {
    return (epochMillis / MILLIS_PER_DAY) * MILLIS_PER_DAY
}

/**
 * Returns the start of the week (Monday 00:00:00 UTC) for the given epoch millis.
 * Unix epoch (1970-01-01) was a Thursday, so we offset by 3 days to align to Monday.
 */
internal fun startOfWeek(epochMillis: Long): Long {
    val daysSinceEpoch = epochMillis / MILLIS_PER_DAY
    // 1970-01-01 is Thursday (day 4 of ISO week, where Monday=1)
    // daysSinceEpoch % 7: 0=Thu, 1=Fri, 2=Sat, 3=Sun, 4=Mon, 5=Tue, 6=Wed
    // To get offset from Monday: (daysSinceEpoch + 3) % 7
    val daysSinceMonday = ((daysSinceEpoch + 3) % 7 + 7) % 7
    val mondayDays = daysSinceEpoch - daysSinceMonday
    return mondayDays * MILLIS_PER_DAY
}
