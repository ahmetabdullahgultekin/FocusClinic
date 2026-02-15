package com.focusclinic.domain.rule

object StreakRules {

    const val STREAK_TIER_1_DAYS = 3
    const val STREAK_TIER_1_MULTIPLIER = 1.1

    const val STREAK_TIER_2_DAYS = 7
    const val STREAK_TIER_2_MULTIPLIER = 1.25

    const val STREAK_TIER_3_DAYS = 14
    const val STREAK_TIER_3_MULTIPLIER = 1.5

    const val STREAK_TIER_4_DAYS = 30
    const val STREAK_TIER_4_MULTIPLIER = 2.0

    const val BASE_MULTIPLIER = 1.0

    fun multiplierForStreak(streakDays: Int): Double = when {
        streakDays >= STREAK_TIER_4_DAYS -> STREAK_TIER_4_MULTIPLIER
        streakDays >= STREAK_TIER_3_DAYS -> STREAK_TIER_3_MULTIPLIER
        streakDays >= STREAK_TIER_2_DAYS -> STREAK_TIER_2_MULTIPLIER
        streakDays >= STREAK_TIER_1_DAYS -> STREAK_TIER_1_MULTIPLIER
        else -> BASE_MULTIPLIER
    }
}
