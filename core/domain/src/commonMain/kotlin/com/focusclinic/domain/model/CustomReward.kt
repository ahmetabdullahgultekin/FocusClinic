package com.focusclinic.domain.model

import com.focusclinic.domain.valueobject.Coin

data class CustomReward(
    val id: String,
    val title: String,
    val cost: Coin,
    val isActive: Boolean,
    val createdAt: Long,
)
