package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.StreakInfo
import com.focusclinic.domain.repository.WillpowerGoalRepository

class CalculateStreakUseCase(
    private val goalRepository: WillpowerGoalRepository,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(): StreakInfo {
        val allDates = goalRepository.getAllCompletionDates()
        if (allDates.isEmpty()) return StreakInfo(current = 0, best = 0)

        val completionDays = allDates.map { daysSinceEpoch(it) }.toSet()
        val today = daysSinceEpoch(clock())

        val currentStreak = calculateCurrentStreak(completionDays, today)
        val bestStreak = calculateBestStreak(completionDays)

        return StreakInfo(current = currentStreak, best = bestStreak)
    }

    private fun calculateCurrentStreak(completionDays: Set<Long>, today: Long): Int {
        // Start checking from today; if no completion today, try yesterday
        var checkDay = today
        if (checkDay !in completionDays) {
            checkDay = today - 1
        }
        if (checkDay !in completionDays) return 0

        var streak = 0
        while (checkDay in completionDays) {
            streak++
            checkDay--
        }
        return streak
    }

    private fun calculateBestStreak(completionDays: Set<Long>): Int {
        if (completionDays.isEmpty()) return 0

        var bestStreak = 0
        var currentRun = 0
        var previousDay = Long.MIN_VALUE

        for (day in completionDays.sorted()) {
            currentRun = if (day == previousDay + 1) currentRun + 1 else 1
            if (currentRun > bestStreak) bestStreak = currentRun
            previousDay = day
        }
        return bestStreak
    }
}

private const val MILLIS_PER_DAY = 86_400_000L

private fun daysSinceEpoch(epochMillis: Long): Long = epochMillis / MILLIS_PER_DAY
