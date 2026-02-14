package com.focusclinic.domain.model

import com.focusclinic.domain.valueobject.Coin
import com.focusclinic.domain.valueobject.ExperiencePoints

data class WillpowerGoal(
    val id: String,
    val title: String,
    val description: String,
    val coinReward: Coin,
    val xpReward: ExperiencePoints,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
