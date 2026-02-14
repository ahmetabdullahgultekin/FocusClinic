package com.focusclinic.domain.model

import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints

data class GoalCompletion(
    val id: String,
    val goalId: String,
    val completedAt: Long,
    val earnedCoins: Coin,
    val earnedXp: ExperiencePoints,
    val note: String,
)
