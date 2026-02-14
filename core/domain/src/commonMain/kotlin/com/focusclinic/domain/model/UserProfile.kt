package com.focusclinic.domain.model

import com.focusclinic.domain.rule.PlayerLevel
import com.focusclinic.domain.valueobject.ExperiencePoints

data class UserProfile(
    val totalXp: ExperiencePoints,
    val level: PlayerLevel,
    val createdAt: Long,
) {
    companion object {
        fun create(createdAt: Long): UserProfile = UserProfile(
            totalXp = ExperiencePoints.ZERO,
            level = PlayerLevel.BEGINNER,
            createdAt = createdAt,
        )
    }
}
