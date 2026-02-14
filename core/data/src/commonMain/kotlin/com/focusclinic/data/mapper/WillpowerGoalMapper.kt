package com.focusclinic.data.mapper

import com.focusclinic.data.database.Willpower_goals
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints

fun Willpower_goals.toDomain(): WillpowerGoal = WillpowerGoal(
    id = id,
    title = title,
    description = description,
    coinReward = Coin(coin_reward),
    xpReward = ExperiencePoints(xp_reward),
    isActive = is_active == 1L,
    createdAt = created_at,
    updatedAt = updated_at,
)
