package com.focusclinic.domain.model

import com.focusclinic.domain.valueobject.Coin

data class ShopItem(
    val id: String,
    val name: String,
    val type: ItemType,
    val modifier: ModifierType,
    val cost: Coin,
)
