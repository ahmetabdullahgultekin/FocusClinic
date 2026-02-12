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
    INTERN(1, "Intern", ExperiencePoints(0)),
    ASSISTANT(2, "Assistant", ExperiencePoints(1_000)),
    RESIDENT(3, "Resident", ExperiencePoints(3_000)),
    SPECIALIST(5, "Specialist", ExperiencePoints(10_000)),
    ASSOCIATE_PROFESSOR(7, "Associate Professor", ExperiencePoints(25_000)),
    PROFESSOR(10, "Professor", ExperiencePoints(60_000));

    companion object {
        fun fromXp(xp: ExperiencePoints): PlayerLevel =
            entries.lastOrNull { xp >= it.requiredXp } ?: INTERN
    }
}
