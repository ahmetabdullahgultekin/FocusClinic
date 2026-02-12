package com.focusclinic.data.mapper

import com.focusclinic.data.database.User_profile
import com.focusclinic.domain.model.UserProfile
import com.focusclinic.domain.rule.PlayerLevel
import com.focusclinic.domain.valueobject.ExperiencePoints

fun User_profile.toDomain(): UserProfile {
    val xp = ExperiencePoints(current_xp)
    return UserProfile(
        totalXp = xp,
        level = PlayerLevel.fromXp(xp),
        createdAt = created_at,
    )
}
