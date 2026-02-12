package com.focusclinic.data.mapper

import com.focusclinic.data.database.Custom_rewards
import com.focusclinic.domain.model.CustomReward
import com.focusclinic.domain.valueobject.Coin

fun Custom_rewards.toDomain(): CustomReward = CustomReward(
    id = id,
    title = title,
    cost = Coin(cost),
    isActive = is_active == 1L,
    createdAt = created_at,
)
