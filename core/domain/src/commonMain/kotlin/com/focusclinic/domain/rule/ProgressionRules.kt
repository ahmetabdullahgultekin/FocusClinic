package com.focusclinic.domain.rule

import com.focusclinic.domain.valueobject.ExperiencePoints

object ProgressionRules {
    const val BASE_MULTIPLIER = 1.0
    const val MAX_MULTIPLIER_CAP = 3.0

    const val XP_PER_MINUTE = 10
    const val COINS_PER_MINUTE = 5
}

enum class PlayerLevel(
    val level: Int,
    val title: String,
    val requiredXp: ExperiencePoints,
) {
    BEGINNER(1, "beginner", ExperiencePoints(0)),
    APPRENTICE(2, "apprentice", ExperiencePoints(1_000)),
    DETERMINED(3, "determined", ExperiencePoints(3_000)),
    STRONG(5, "strong", ExperiencePoints(10_000)),
    MASTER(7, "master", ExperiencePoints(25_000)),
    LEGEND(10, "legend", ExperiencePoints(60_000));

    companion object {
        fun fromXp(xp: ExperiencePoints): PlayerLevel =
            entries.lastOrNull { xp >= it.requiredXp } ?: BEGINNER
    }
}
