package com.focusclinic.data.mapper

import com.focusclinic.data.database.Goal_completions
import com.focusclinic.domain.model.GoalCompletion
import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints

fun Goal_completions.toDomain(): GoalCompletion = GoalCompletion(
    id = id,
    goalId = goal_id,
    completedAt = completed_at,
    earnedCoins = Coin(earned_coins),
    earnedXp = ExperiencePoints(earned_xp),
    note = note,
)
